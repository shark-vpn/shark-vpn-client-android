package me.tt5397194.sharkcpp.preference;

import android.content.Context;
import androidx.preference.ListPreference;
import android.util.AttributeSet;

import java.util.List;

import me.tt5397194.sharkcpp.MainActivity;
import me.tt5397194.sharkcpp.sqlite.VpnConfig;

public class MyListPreference extends ListPreference {

    public MyListPreference(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        this.setOnPreferenceChangeListener(new MyOnPreferenceChangeListener());
    }

    public void initConfigs() {
        MainActivity.vpn_configs.clear();
        List<VpnConfig> cfgLst = VpnConfig.GetAll();
        for (VpnConfig cfg : cfgLst) {
            MainActivity.vpn_configs.put(cfg.id, cfg);
        }
        this.updateConfigs();
        //this.setValue("1");
    }

    public VpnConfig insertNewConfig(String name) {
        VpnConfig cfg = new VpnConfig();
        cfg.vpn_name = name;
        cfg.server_addr = "www.vpn.com";
        cfg.server_port = 7194;
        cfg.password = "123456";
        cfg.local_ip = "192.168.194.1";
        cfg.dns_addr = "8.8.8.8";
        cfg.mtu = 1200;
        if (VpnConfig.Add(cfg)) {
            cfg.vpn_name = name + "-" + (cfg.id - 1);
            VpnConfig.Update(cfg);
        }
        MainActivity.vpn_configs.put(cfg.id, cfg);
        this.updateConfigs();
        return cfg;
    }

    public VpnConfig deleteConfig(Long id) {
        if (MainActivity.vpn_configs.size() <= 3) {
            return null;
        }
        VpnConfig.Del(id);
        MainActivity.vpn_configs.remove(id);
        this.updateConfigs();

        for (VpnConfig cfg : MainActivity.vpn_configs.values()) {
            if (cfg.id > 2) {
                return cfg;
            }
        }
        return null;
    }

    public void updateConfigs() {
        int len = MainActivity.vpn_configs.size();
        CharSequence[] names = new CharSequence[len];
        CharSequence[] ids = new CharSequence[len];
        int i = 0;
        for (VpnConfig cfg : MainActivity.vpn_configs.values()) {
            names[i] = cfg.vpn_name;
            ids[i] = String.valueOf(cfg.id);
            i++;
        }
        this.setEntries(names);
        this.setEntryValues(ids);
    }

    public void setSummaryFromValue() {
        String textValue = this.getValue();
        int index = this.findIndexOfValue(textValue);
        CharSequence[] entries = this.getEntries();
        if (index >= 0 && index < entries.length) {
            this.setSummary(entries[index].toString());
        }
    }
}
