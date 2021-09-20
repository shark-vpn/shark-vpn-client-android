package me.tt5397194.sharkcpp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.VpnService;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.LinkedHashMap;

import me.tt5397194.sharkcpp.preference.AppSetting;
import me.tt5397194.sharkcpp.preference.MyEditTextPreference;
import me.tt5397194.sharkcpp.preference.MyListPreference;
import me.tt5397194.sharkcpp.sqlite.FeedReaderDbHelper;
import me.tt5397194.sharkcpp.sqlite.VpnConfig;

public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("native-lib");
    }

    public static MainActivity self;
    public static LinkedHashMap<Long, VpnConfig> vpn_configs;
    public static FeedReaderDbHelper dbHelper;
    static FloatingActionButton fab;
    static MyListPreference lp;
    static ColorStateList color_bl;
    static ColorStateList color_ok;
    static MyEditTextPreference etp1;
    static MyEditTextPreference etp2;
    static MyEditTextPreference etp3;
    static EditTextPreference etp4;
    static MyEditTextPreference etp5;
    static MyEditTextPreference etp6;
    static SwitchPreference preApp;
    static Preference appLst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        self = this;
        super.onCreate(savedInstanceState);
        vpn_configs = new LinkedHashMap<>();
        dbHelper = new FeedReaderDbHelper(this);
        AppListActivity.initAdapter(null);
        setContentView(R.layout.activity_main);
        Log.d("zzz", "MainActivity onCreate");

        color_bl = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary));
        color_ok = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.OK));

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new MainActivity.SettingsFragment())
                .commit();

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyVpnService.mInterface == null) {
                    Intent intent = VpnService.prepare(MainActivity.this);
                    if (intent != null) startActivityForResult(intent, 0);
                    else onActivityResult(0, RESULT_OK, null);
                } else {
                    MyVpnService.stopMyVPN();
                    SetEnabled(true);
                    Log.e("zzz", "stopMyVPN = " + MyVpnService.mInterface);
                }
            }
        });
    }

    public static void UpdateVpn(VpnConfig cfg) {
        etp1.setText(cfg.vpn_name);
        etp2.setText(cfg.server_addr);
        etp3.setText(String.valueOf(cfg.server_port));
        etp4.setText(cfg.password);
        etp5.setText(cfg.local_ip);
        etp6.setText(cfg.dns_addr);
    }

    public static void SetEnabled(boolean enabled) {
        if (enabled) {
            fab.setBackgroundTintList(color_bl);
        } else {
            fab.setBackgroundTintList(color_ok);
        }
        lp.setEnabled(enabled);
        etp1.setEnabled(enabled);
        etp2.setEnabled(enabled);
        etp3.setEnabled(enabled);
        etp4.setEnabled(enabled);
        etp5.setEnabled(enabled);
        etp6.setEnabled(enabled);
        preApp.setEnabled(enabled);
        appLst.setEnabled(enabled);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);

            lp = findPreference("config_list");
            etp1 = findPreference("vpn_name");
            etp2 = findPreference("server_addr");
            etp3 = findPreference("server_port");
            etp4 = findPreference("password");
            etp5 = findPreference("local_ip");
            etp6 = findPreference("dns_addr");

            lp.initConfigs();
            long cfg_id = Long.parseLong(lp.getValue());
            VpnConfig cfg = vpn_configs.get(cfg_id);
            MainActivity.UpdateVpn(cfg);

            preApp = findPreference("pre_app");
            appLst = findPreference("pre_app_lst");
            assert appLst != null;
            appLst.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(MainActivity.self, AppListActivity.class);
                    startActivity(intent);
                    MainActivity.self.setResult(RESULT_OK,intent);
                    return true;
                }
            });

            if (MyVpnService.mInterface == null) {
                SetEnabled(true);
            } else {
                SetEnabled(false);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //MyVpnService.stopMyVPN();
    }

    @SuppressLint("ResourceAsColor")
    protected void onActivityResult(int request, int result, Intent data) {
        super.onActivityResult(request, result, data);
        Log.e("result", String.valueOf(result));
        if (result == RESULT_OK) {
            Intent intent = new Intent(this, MyVpnService.class);
            startService(intent);
            SetEnabled(false);

            VpnConfig cfg = AppSetting.getSetting(this).cfg;
            VpnConfig.Update(cfg);
            MainActivity.vpn_configs.put(cfg.id, cfg);
            lp.updateConfigs();
        }
    }

    public native String stringFromJNI();
}
