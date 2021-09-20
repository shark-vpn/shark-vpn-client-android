package me.tt5397194.sharkcpp.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import me.tt5397194.sharkcpp.MainActivity;

public class VpnConfig {
    public long id = 0;
    public String vpn_name = "";
    public String server_addr = "";
    public int server_port = 0;
    public String password = "";
    public String local_ip = "";
    public String dns_addr = "";

    public static List<VpnConfig> GetAll() {
        SQLiteDatabase db = MainActivity.dbHelper.getWritableDatabase();
        String[] projection = {"_id", "name", "server_addr", "server_port", "password", "local_ip", "dns_addr"};
        String sortOrder = "_id";
        Cursor cursor = db.query("vpn_config", projection, null, null, null, null, sortOrder);

        List<VpnConfig> cfgLst = new ArrayList<>();
        while (cursor.moveToNext()) {
            VpnConfig cfg = new VpnConfig();
            cfg.id = cursor.getLong(cursor.getColumnIndexOrThrow("_id"));
            cfg.vpn_name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            cfg.server_addr = cursor.getString(cursor.getColumnIndexOrThrow("server_addr"));
            cfg.server_port = cursor.getInt(cursor.getColumnIndexOrThrow("server_port"));
            cfg.password = cursor.getString(cursor.getColumnIndexOrThrow("password"));
            cfg.local_ip = cursor.getString(cursor.getColumnIndexOrThrow("local_ip"));
            cfg.dns_addr = cursor.getString(cursor.getColumnIndexOrThrow("dns_addr"));
            cfgLst.add(cfg);
        }
        cursor.close();
        return cfgLst;
    }

    public static boolean Add(VpnConfig cfg) {
        SQLiteDatabase db = MainActivity.dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", cfg.vpn_name);
        values.put("server_addr", cfg.server_addr);
        values.put("server_port", cfg.server_port);
        values.put("password", cfg.password);
        values.put("local_ip", cfg.local_ip);
        cfg.id = db.insert("vpn_config", null, values);
        return cfg.id != 0;
    }

    public static boolean Del(Long id) {
        String sql = "delete from vpn_config where _id=" + id;
        SQLiteDatabase db = MainActivity.dbHelper.getWritableDatabase();
        db.execSQL(sql);
        Log.e("zzz del", sql);
        return true;
    }

    public static boolean Update(VpnConfig cfg) {
        String sql = "update vpn_config set name=?, server_addr=?, server_port=?, password=?, local_ip=?, dns_addr=? where _id=?";
        SQLiteDatabase db = MainActivity.dbHelper.getWritableDatabase();
        Object[] pras = new Object[]{cfg.vpn_name, cfg.server_addr, cfg.server_port, cfg.password, cfg.local_ip, cfg.dns_addr, cfg.id};
        db.execSQL(sql, pras);
        Log.e("zzz", sql);
        return true;
    }
}
