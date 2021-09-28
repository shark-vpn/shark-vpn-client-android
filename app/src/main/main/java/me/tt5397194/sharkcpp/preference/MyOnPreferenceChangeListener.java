package me.tt5397194.sharkcpp.preference;

import android.widget.Toast;
import androidx.preference.Preference;

import me.tt5397194.sharkcpp.MainActivity;
import me.tt5397194.sharkcpp.R;
import me.tt5397194.sharkcpp.sqlite.VpnConfig;

public class MyOnPreferenceChangeListener implements Preference.OnPreferenceChangeListener {
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        MyListPreference listPreference = (MyListPreference) preference;
        String textValue = newValue.toString();
        boolean b = true;

        VpnConfig cfg;
        if (textValue.equals("1")) {
            //添加配置
            //long time = System.currentTimeMillis() / 1000 % 1000;
            cfg = listPreference.insertNewConfig(MainActivity.self.getString(R.string.new_config));
            textValue = String.valueOf(cfg.id);
            listPreference.setValue(textValue);
            MainActivity.UpdateVpn(cfg);
            b = false;
        } else if (textValue.equals("2")) {
            //删除选中配置
            Long currId = Long.parseLong(listPreference.getValue());
            cfg = listPreference.deleteConfig(currId);
            if (cfg == null) {
                String mess = MainActivity.self.getString(R.string.at_least_one_config);
                Toast.makeText(MainActivity.self, mess, Toast.LENGTH_SHORT).show();
                return false;
            }
            textValue = String.valueOf(cfg.id);
            listPreference.setValue(textValue);
            MainActivity.UpdateVpn(cfg);
            b = false;
        } else {
            cfg = MainActivity.vpn_configs.get(Long.parseLong(textValue));
            MainActivity.UpdateVpn(cfg);
        }
        //listPreference.getEntry(); 获取到的是没有改变前的
        int index = listPreference.findIndexOfValue(textValue);
        CharSequence[] entries = listPreference.getEntries();

        if (index >= 0 && index < entries.length) {
            //preference.setSummary(entries[index].toString());
        }

        return b;
    }
}
