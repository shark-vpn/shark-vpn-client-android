package me.tt5397194.sharkcpp;

import android.content.Intent;
import android.net.VpnService;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import me.tt5397194.sharkcpp.preference.AppSetting;

public class MyVpnService extends VpnService {
    static {
        System.loadLibrary("native-lib");
    }

    public static ParcelFileDescriptor mInterface;
    public static Thread mThread;
    public static MyVpnService me;
    public static boolean IsFirst = true;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        me = this;

        final AppSetting setting = AppSetting.getSetting(this);

        Builder builder = new Builder();
        builder.setSession(setting.cfg.vpn_name);
        builder.addAddress(setting.cfg.local_ip, 24);
        builder.addRoute("0.0.0.0", 0);
        builder.addDnsServer(setting.cfg.dns_addr);
        builder.setMtu(1200);
        if (setting.pre_app) {
            try {
                int i = -1;
                for (String pkg_name : AppListActivity.adapter.list_value) {
                    i++;
                    if (!AppListActivity.adapter.list_check.get(i)) continue;
                    builder.addAllowedApplication(pkg_name);
                    Log.e("zzz", "pre_app[" + i + "][" + pkg_name + "]");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        mInterface = builder.establish();
        final int fd = mInterface.getFd();
        //int fd = mInterface.detachFd();

        setServer(setting.cfg.server_addr, setting.cfg.server_port, setting.cfg.password);
        int clientFd = getClientFd();
        protect(clientFd);

        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e("zzz", "sendFd1 = " + fd);
                int re = sendFd(fd);
                Log.e("zzz", "sendFd2 = " + re);
            }
        });
        mThread.start();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.e("zzz", "onDestroy");
        MyVpnService.stopMyVPN();
        MainActivity.SetEnabled(true);
        mThread.interrupt();
        super.onDestroy();
    }

    public static void stopMyVPN() {
        try {
            if (mInterface != null) {
                mInterface.close();
                mInterface = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        me.stopSelf();
    }

    public native int getClientFd();

    public native int setServer(String server_adr, int server_port, String password);

    public native int sendFd(int fd);
}
