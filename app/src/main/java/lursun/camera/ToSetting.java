package lursun.camera;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

/**
 * Created by admin on 2016/10/30.
 */
public class ToSetting extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
        SQLite sql=new SQLite(getApplicationContext());
        SQLiteDatabase db=sql.getReadableDatabase();
        Cursor c=db.rawQuery("Select * From setting",null);
        c.moveToFirst();

        ((EditText)findViewById(R.id.storeid)).setText(c.getString(2));
        ((EditText)findViewById(R.id.IP)).setText(c.getString(1));
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent intent=new Intent();
            intent.setClass(ToSetting.this,MainActivity.class);
            startActivity(intent);
            finish();

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    public void save (View view){
        ContentValues cv=new ContentValues();
        cv.put("IP",((EditText)findViewById(R.id.IP)).getText().toString());
        cv.put("storeid",((EditText)findViewById(R.id.storeid)).getText().toString());
        SQLite sql=new SQLite(getApplicationContext());
        SQLiteDatabase db=sql.getReadableDatabase();
        db.update("setting",cv,null,null);
        Intent intent=new Intent();
        intent.setClass(ToSetting.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}
