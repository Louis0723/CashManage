package lursun.camera;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by admin on 2016/9/5.
 */
public class SQLite extends SQLiteOpenHelper {
    private final static int _DBVersion = 8;
    private final static String _DBName = "cash.db";
    public SQLite(Context context) {
        super(context, _DBName, null, _DBVersion);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL = "CREATE TABLE IF NOT EXISTS history( " +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "Category TEXT,"+
                "Target TEXT,"+
                "Amount Integer,"+
                "Picture TEXT,"+
                "signed Integer,"+
                "Publisher Text"+
                 ");";
        db.execSQL(SQL);
        SQL = "CREATE TABLE IF NOT EXISTS shift( " +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "time Integer"+
                ");";
        db.execSQL(SQL);
        db.execSQL("INSERT INTO  shift(time) values (0)");
        SQL = "CREATE TABLE IF NOT EXISTS setting( " +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "IP TEXT,"+
                "storeid TEXT"+
                ");";
        db.execSQL(SQL);
        db.execSQL("INSERT INTO  setting(IP,storeid) values ('192.168.123.100','123456789')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}
