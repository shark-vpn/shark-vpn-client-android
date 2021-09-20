#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>

#include <sys/socket.h>
#include <sys/epoll.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <fcntl.h>
#include <unistd.h>

#include <jni.h>
#include <string>

#include <android/log.h>
#define  LOG_TAG    "zzz nactive"
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#define  LOGW(...)  __android_log_print(ANDROID_LOG_WARN,LOG_TAG,__VA_ARGS__)
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)

#include "chacha20.h"
#include "key.c"

char SERVER_ADDR[64] = "192.168.33.167";
char PASSWORD[64] = { 0 };
#define MAX_PKG_LEN 65535

int SERVER_PORT = 7194;
int OPEN_MAX = 500;
int LISTENQ = 5;

sockaddr* GetServerAddr();
void* RecvNetPkg(void* args);

extern "C" JNIEXPORT jstring JNICALL
Java_me_tt5397194_sharkcpp_MainActivity_stringFromJNI(JNIEnv* env, jobject)
{
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

int udp_fd = -1;
extern "C" JNIEXPORT jint JNICALL
Java_me_tt5397194_sharkcpp_MyVpnService_getClientFd(JNIEnv*, jobject)
{
    udp_fd = socket(AF_INET, SOCK_DGRAM, 0);
    if (udp_fd < 0)
    {
       printf("create socket fail!\n");
       return -1;
    }
    return udp_fd;
}

extern "C" JNIEXPORT jint JNICALL
Java_me_tt5397194_sharkcpp_MyVpnService_setServer(JNIEnv* env, jobject, jstring server_adr, jint server_port, jstring password)
{
    const char *nativeString = env->GetStringUTFChars(server_adr, nullptr);
    strncpy(SERVER_ADDR, nativeString, 64);
    env->ReleaseStringUTFChars(server_adr, nativeString);
    const char *nativeString2 = env->GetStringUTFChars(password, nullptr);
    LOGE("nativeString2[%s]", nativeString2);
    int len = strlen(nativeString2);
    len = len > 12 ? 12 : len;
    strncpy(PASSWORD, nativeString2, len);
    for (int i = 0; i < 12; i++)
    {
        if (i < len)
            nonce[i] = nonce0[i] ^ nativeString2[i];
        else
            nonce[i] = nonce0[i];
    }
    env->ReleaseStringUTFChars(server_adr, nativeString2);
    SERVER_PORT = server_port;
    return 0;
}

extern "C" JNIEXPORT jint JNICALL
Java_me_tt5397194_sharkcpp_MyVpnService_sendFd(JNIEnv*, jobject, jint tun_fd)
{
    LOGD("sendFd");
    int epollfd = epoll_create(OPEN_MAX);
    if (epollfd <= 0)
    {
        LOGE("epollfd[%d]", epollfd);
        return epollfd;
    }

    int ret;
    struct epoll_event epev1;
    epev1.events = EPOLLIN | EPOLLRDHUP;
    epev1.data.fd = tun_fd;
    ret = epoll_ctl(epollfd, EPOLL_CTL_ADD, tun_fd, &epev1);
    if (ret != 0)
    {
        LOGE("ret1[%d]", ret);
        return ret;
    }
    listen(tun_fd, LISTENQ);

    struct epoll_event epev2;
    epev2.events = EPOLLIN;
    epev2.data.fd = udp_fd;
    ret = epoll_ctl(epollfd, EPOLL_CTL_ADD, udp_fd, &epev2);
    if (ret != 0)
    {
        LOGE("ret2[%d]", ret);
        return ret;
    }
    listen(udp_fd, LISTENQ);

    struct epoll_event events_in[OPEN_MAX];
    uint8_t buf[MAX_PKG_LEN] = { 0 };
    sockaddr* ser_addr = GetServerAddr();

    socklen_t len;
    struct sockaddr_in src;
    struct chacha20_context ctx;
    while(true)
    {
        int event_count = epoll_wait(epollfd, events_in, OPEN_MAX, -1);
        //超时返回 0 , 出错返回 -1 , timeout 设置为 -1 表示无限等待.
        if (event_count == -1)
        {
            LOGE("epoll_wait error");
            return event_count;
        }
        for (int i = 0; i < event_count; i++)
        {
            if(events_in[i].data.fd == tun_fd && events_in[i].events & EPOLLIN)
            {
                int n = read(tun_fd, buf, MAX_PKG_LEN);
                if (n <= 0) continue;
                chacha20_init_context(&ctx, key, nonce, counter);
                chacha20_xor(&ctx, buf, n);
                sendto(udp_fd, buf, n, 0, ser_addr, sizeof(sockaddr_in));
            }
            else if(events_in[i].data.fd == udp_fd && events_in[i].events & EPOLLIN)
            {
                int n = recvfrom(udp_fd, buf, MAX_PKG_LEN, 0, (struct sockaddr*)&src, &len);
                //LOGD("recvfrom n[%d]", n);
                if (n <= 0) continue;
                chacha20_init_context(&ctx, key, nonce, counter);
                chacha20_xor(&ctx, buf, n);
                //LOGD("recvfrom buf[%s]", buf);
                write(tun_fd, buf, n);
            }
            else if (events_in[i].events & EPOLLRDHUP)
            {
                LOGE("tun_fd[%d] closed fd[%d] evn[%d]",
                    tun_fd, events_in[i].data.fd, events_in[i].events);
                return 0;
            }
        }
    }
}

sockaddr* GetServerAddr()
{
    sockaddr_in* ser_addr = new sockaddr_in;

    memset(ser_addr, 0, sizeof(sockaddr_in));
    ser_addr->sin_family = AF_INET;
    ser_addr->sin_addr.s_addr = inet_addr(SERVER_ADDR);
    //ser_addr.sin_addr.s_addr = htonl(INADDR_ANY);
    ser_addr->sin_port = htons(SERVER_PORT);

    return (sockaddr*)ser_addr;
}
