package me.tt5397194.sharkcpp.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import me.tt5397194.sharkcpp.sqlite.VpnConfig;

public class AppSetting {
    public static AppSetting me = null;
    public VpnConfig cfg = new VpnConfig();
    public boolean pre_app = false;

    private AppSetting(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        cfg.id = Integer.parseInt(settings.getString("config_list", "1"));
        cfg.vpn_name = settings.getString("vpn_name", "");
        cfg.server_addr = settings.getString("server_addr", "");
        //cfg.server_port = settings.getInt("server_port", 7194);
        cfg.server_port = Integer.parseInt(settings.getString("server_port", "7194"));
        cfg.password = settings.getString("password", "");
        cfg.local_ip = settings.getString("local_ip", "");
        cfg.dns_addr = settings.getString("dns_addr", "8.8.8.8");
        pre_app = settings.getBoolean("pre_app", false);
    }

    public static AppSetting getSetting(Context context) {
        if (me != null) {
            return me;
        }
        return new AppSetting(context);
    }
}
