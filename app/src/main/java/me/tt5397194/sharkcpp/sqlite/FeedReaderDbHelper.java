package me.tt5397194.sharkcpp.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import me.tt5397194.sharkcpp.MainActivity;
import me.tt5397194.sharkcpp.R;

public class FeedReaderDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "shark.db";

    //private static final String DROP_DATABASE = "DROP DATABASE " + DATABASE_NAME;
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS ";
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS vpn_config";

    private static final String SQL_CREATE_VPN_CONFIG =
            "CREATE TABLE vpn_config(_id INTEGER PRIMARY KEY, name TEXT, server_addr TEXT, " +
            "server_port INTEGER, password TEXT, local_ip TEXT, dns_addr TEXT);";

    private static final String SQL_CREATE_APP_LIST =
            "CREATE TABLE app_list(pkg_name TEXT PRIMARY KEY, app_name BLOB, is_sys INTEGER);";

    private static final String SQL_CREATE_APP_PROXY =
            "CREATE TABLE app_proxy(pkg_name TEXT PRIMARY KEY, is_proxy INTEGER);";


    private static final String SQL_INSERT_BASE_DATA1 =
            "insert into vpn_config(name, server_addr, server_port, password, local_ip, dns_addr) " +
            "values('" + MainActivity.self.getString(R.string.add_config) + "', '', 0, '', '', '')," +
            "('" + MainActivity.self.getString(R.string.del_config) + "', '', 0, '', '', '')," +
            "('" + MainActivity.self.getString(R.string.default_config) + "', 'www.vpn.com', 7194, '123456', '192.168.194.1', '8.8.8.8');";

    private static final String SQL_INSERT_BASE_DATA2 =
            "insert into app_list(app_name, pkg_name, is_proxy, is_sys) " +
            "values('Chrome', 'com.android.chrome', 0, 1),('Via', 'mark.via.gp', 1, 0)";

    public static final String SQL_INSERT_NEW_DATA =
            "insert into vpn_config(name, server_addr, server_port, password, local_ip) " +
            "values('" + MainActivity.self.getString(R.string.new_config) + "', 'www.vpn.com', 7194, '123456', '192.168.194.1', '8.8.8.8')";

    public FeedReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        Log.e("zzz", "db onCreate()");
        db.execSQL(SQL_CREATE_VPN_CONFIG);
        db.execSQL(SQL_CREATE_APP_LIST);
        db.execSQL(SQL_CREATE_APP_PROXY);
        db.execSQL(SQL_INSERT_BASE_DATA1);
        //db.execSQL(SQL_INSERT_BASE_DATA2);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e("zzz", "db onUpgrade()");
        //db.execSQL(SQL_DELETE_ENTRIES);
        db.execSQL(DROP_TABLE + "vpn_config");
        db.execSQL(DROP_TABLE + "app_list");
        db.execSQL(DROP_TABLE + "app_proxy");
        this.onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e("zzz", "db onDowngrade()");
        this.onUpgrade(db, oldVersion, newVersion);
    }
}
