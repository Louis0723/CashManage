package lursun.camera;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by admin on 2016/10/29.
 */
public class CheckoutRep extends Activity {
    SQLiteDatabase db;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout);

        SimpleDateFormat sdf=new SimpleDateFormat("MM月dd日結帳");

        ((TextView)findViewById(R.id.title)).setText(sdf.format(new Date()));
        SQLite sqLite=new SQLite(getApplicationContext());
        db=sqLite.getReadableDatabase();
        SimpleDateFormat sdf2=new SimpleDateFormat("yyyyMMdd");

        Cursor sum=db.rawQuery("SELECT  SUM(CAST(Amount as Integer))  FROM history Where signed=-1 And Picture Like '%"+sdf2.format(new Date())+"%'",null);
        sum.moveToFirst();
        ((TextView)findViewById(R.id.payAmount)).setText(sum.getInt(0)+"");
        sum=db.rawQuery("SELECT  SUM(CAST(Amount as Integer))  FROM history Where signed=1 And Picture Like '%"+sdf2.format(new Date())+"%'",null);
        sum.moveToFirst();
        final int pos=sum.getInt(0);

        final Handler h=new Handler(){
            @Override
            public void handleMessage(Message msg) {

                ((TextView)findViewById(R.id.getAmount)).setText(pos+Integer.parseInt((String)msg.obj)+"");

                ((TextView) findViewById(R.id.Amount)).setText(Integer.parseInt(((TextView) findViewById(R.id.getAmount)).getText().toString())- Integer.parseInt(((TextView) findViewById(R.id.payAmount)).getText().toString())+"");


            }
        };
        new Thread(){
            @Override
            public void run() {
                try {
                    Cursor c=db.rawQuery("Select * From setting",null);
                    c.moveToFirst();
                    SimpleDateFormat sdf3=new SimpleDateFormat("yyyy-MM-dd");

                    String s=String.format("http://admin.joyspots.net/posapi/GetCash.aspx?shopid=%s&date=%s",c.getString(2),sdf3.format(new Date()));
                    URL obj = new URL(s);
                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                    if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        InputStreamReader isr=new InputStreamReader(con.getInputStream());
                        BufferedReader br=new BufferedReader(isr);
                        s=br.readLine();
                        Pattern p=Pattern.compile("\\d+(?=\\.00)");
                        Matcher m=p.matcher(s);
                        m.find();
                        s=m.group();
                        Message msg=new Message();
                        msg.obj=s;
                        h.sendMessage(msg);

                    }
                }catch (Exception e){
                    e=e;
                }
            }
        }.start();



    }
    public void toBack(View view){
        Intent intent=new Intent();
        intent.setClass(CheckoutRep.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
    public void toShift(View view){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
        db.execSQL("Update shift Set time="+sdf.format(new Date()));
        toBack(view);
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent intent=new Intent();
            intent.setClass(CheckoutRep.this,MainActivity.class);
            startActivity(intent);
            finish();

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
