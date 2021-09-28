package me.tt5397194.sharkcpp.sqlite;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import me.tt5397194.sharkcpp.MainActivity;

public class AppInfo {
    public String app_name = "";
    public String pkg_name = "";
    public String icon_path = "";
    public int is_sys = 0;
    public int is_proxy = 0;

    public static List<AppInfo> GetAll() {
        SQLiteDatabase db = MainActivity.dbHelper.getWritableDatabase();
        String sql = "select app_list.*, is_proxy from app_list left join app_proxy on app_list.pkg_name = app_proxy.pkg_name " +
                "order by app_proxy.is_proxy desc, app_list.app_name";
        Cursor cursor = db.rawQuery(sql, null);

        List<AppInfo> list = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                AppInfo ifo = new AppInfo();
                ifo.app_name = new String(cursor.getBlob(cursor.getColumnIndexOrThrow("app_name")), "GBK");
                ifo.pkg_name = cursor.getString(cursor.getColumnIndexOrThrow("pkg_name"));
                ifo.is_proxy = cursor.getInt(cursor.getColumnIndexOrThrow("is_proxy"));
                list.add(ifo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        cursor.close();

        return list;
    }

    //删除所有再插入所有
    public static void RefreshAll(List<AppInfo> list) {
        SQLiteDatabase db = MainActivity.dbHelper.getWritableDatabase();
        db.execSQL("delete from app_list");
        try {
            for (AppInfo info : list) {
                String sql = "insert into app_list(pkg_name, app_name, is_sys) values(?, ?, ?)";
                Object[] pras = {info.pkg_name, info.app_name.getBytes("GBK"), info.is_sys};
                db.execSQL(sql, pras);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //设置代理就是往app_proxy表插入一条,取消代理则删除
    public static void SetProxy(String pkg_name, boolean proxy) {
        final String sql_set_proxy = "insert into app_proxy(pkg_name, is_proxy) values(?, ?)";
        final String sql_del_proxy = "delete from app_proxy where pkg_name=? and 1=?";
        final SQLiteDatabase db = MainActivity.dbHelper.getWritableDatabase();
        Object[] pras = new Object[]{pkg_name, 1};
        if (proxy) {
            db.execSQL(sql_set_proxy, pras);
        } else {
            db.execSQL(sql_del_proxy, pras);
        }
    }

}
