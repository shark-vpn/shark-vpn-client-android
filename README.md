# Shark VPN 安卓客户端

## 使用方法

### 1. 部署Linux服务端

```sh
ip tuntap add tun0 mode tun
tunctl -n -t tun0 -u root
ip link set dev tun0 up
ifconfig tun0 192.168.194.224 netmask 255.255.255.0 promisc
iptables -t nat -A POSTROUTING -o eth0 -j MASQUERADE

git clone https://github.com/shark-vpn/shark-server
cd shark-server

gcc -std=gnu99 shark.c chacha20.c -g -o shark && ./shark 7194 "密码"

```

详见 [shark-vpn.github.io](https://shark-vpn.github.io)

### 2. 客户端使用

```sh
git https://github.com/shark-vpn/shark-client-android

使用 Android Studio 3.6.1 构建app
安装 app 后点击右下角的按钮即可.

```

![shark](shark.jpg)

