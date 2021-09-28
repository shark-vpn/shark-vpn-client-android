package me.tt5397194.sharkcpp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.List;

import me.tt5397194.sharkcpp.preference.SwitchAdapter;
import me.tt5397194.sharkcpp.sqlite.AppInfo;

public class AppListActivity extends AppCompatActivity {

    private ProgressDialog pd = null;
    public static SwitchAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("zzz", "AppListActivity onCreate");
        this.pd = ProgressDialog.show(this, getString(R.string.app_list),
                getString(R.string.loading), true, false);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        new IAmABackgroundTask().execute();
    }

    class IAmABackgroundTask extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected void onPreExecute() {
            //运行后台运行任务之前
        }
        @Override
        protected void onPostExecute(Boolean result) {
            if (AppListActivity.this.pd != null) {
                AppListActivity.this.pd.dismiss();
            }
            setContentView(R.layout.activity_app_list);
            showListView();
        }
        @Override
        protected Boolean doInBackground(String... params) {
            //后台运行任务,注意不能动UI,UI部分放onPostExecute()里面
            initAppList();
            initAdapter(AppListActivity.this);
            return true;
        }
    }

    private void initAppList() {
        List<AppInfo> list = getAppList();
        AppInfo.RefreshAll(list);
    }

    private List<AppInfo> getAppList() {
        ArrayList<AppInfo> list = new ArrayList<>();
        PackageManager pm = getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        for (PackageInfo pinfo : packages) {
            AppInfo info = new AppInfo();
            info.pkg_name = pinfo.packageName;
            char[] cs = pinfo.applicationInfo.loadLabel(pm).toString().toCharArray();
            if (cs[0] >= 'a' && cs[0] <= 'z') cs[0] -= 32; //首字母大写
            info.app_name = String.valueOf(cs);
            info.is_sys = pinfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM;
            list.add(info);
            Log.e("zzz", "getAppList1 [" + info.pkg_name + "] [" + info.app_name + "]");
        }
        return list;
    }

    public static void initAdapter(Context context) {
        adapter = new SwitchAdapter(context);
        List<AppInfo> list = AppInfo.GetAll();
        for (AppInfo ifo : list) {
            adapter.addItem(ifo.app_name, ifo.pkg_name, ifo.is_proxy == 1);
            //Log.e("zzz", "pkg_name " + ifo.pkg_name + " " + ifo.is_proxy);
        }
    }

    public void showListView() {
        ListView lv = findViewById(R.id.app_list);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Switch sv = adapter.list_switch[i];
                Log.e("zzz", adapter.list_value.get(i));

                sv.setChecked(!sv.isChecked());
                adapter.list_check.set(i, sv.isChecked());
                AppInfo.SetProxy(adapter.list_value.get(i), sv.isChecked());
            }
        });

        //String[] names = new String[len];
        //lv.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.d("zzz", "onOptionsItemSelected id=" + id);
        if (id == R.id.select_all) {
            return true;
        } else if (id == R.id.select_invert) {
            return true;
        } else if (id == android.R.id.home) {
            //左上角返回按钮
            Log.d("zzz", "android.R.id.home");
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
