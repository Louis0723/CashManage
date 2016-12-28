package lursun.camera;

import android.app.Activity;
import android.app.Dialog;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by admin on 2016/10/29.
 */
public class Expenditues_Rep extends Activity {
    SQLiteDatabase db;
    android.app.AlertDialog.Builder builder;
    Dialog dialog;
    LinearLayout Limage;
    LayoutInflater inflater;
    int year,_year;
    int month,_month;
    int day,_day;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expenditures_details);
        inflater = (LayoutInflater) getLayoutInflater();

        SQLite sqLite=new SQLite(getApplicationContext());
        db=sqLite.getReadableDatabase();

        final SimpleDateFormat sdf=new SimpleDateFormat("MM月dd日支出明細");
        SimpleDateFormat y_sdf=new SimpleDateFormat("yyyy");
        SimpleDateFormat m_sdf=new SimpleDateFormat("MM");
        SimpleDateFormat d_sdf=new SimpleDateFormat("dd");
        Date date=new Date();
        _year=year=Integer.parseInt( y_sdf.format(date));
        _month=month=Integer.parseInt( m_sdf.format(date));
        _day=day=Integer.parseInt( d_sdf.format(date));


        Limage=(LinearLayout) inflater.inflate(R.layout.dialog,null);
        builder = new android.app.AlertDialog.Builder(Expenditues_Rep.this, R.style.dialog);
        builder.setView(Limage);
        builder.setPositiveButton("返回",null);
        builder.setNegativeButton("刪除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Cursor c=db.rawQuery("SELECT substr(Picture,16,14)  FROM history Where Picture Like '%"+((String) Limage.getTag())+"%'",null);
                c.moveToFirst();
                Cursor shift=db.rawQuery("Select time From shift",null);
                shift.moveToFirst();
                long time=Long.parseLong( shift.getString(0));
                if(Long.parseLong( c.getString(0))>time) {
                    db.execSQL("Delete From history Where Picture Like '%" + ((String) Limage.getTag()) + "%'");


                    File file = new File((String) Limage.getTag());
                    if (file.exists()) {
                        file.delete();
                    }
                    makeView();
                }else {
                    Toast.makeText(getApplicationContext(),"已交班，刪除失敗",Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog=builder.create();

        makeView();

    }
    public void makeView(){
        ((LinearLayout) findViewById(R.id.tableScroll)).removeAllViews();
        EditText et_month=(EditText)findViewById(R.id.month);
        et_month.setText(month+"");
        EditText et_day=(EditText)findViewById(R.id.day);
        et_day.setText(day+"");
        //((TextView)findViewById(R.id.title)).setText(s);
        View.OnClickListener ocl=new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String path=((String)view.getTag());
                try{
                    File file=new File(path);
                    Limage.setTag(path);
                    if(file.exists()) {

                        Bitmap bitmap = BitmapFactory.decodeFile(path);
                        ((ImageView)Limage.findViewById(R.id.image)).setImageBitmap(bitmap);
                        Limage.setVisibility(View.VISIBLE);
                        dialog.setTitle(path);
                        dialog.show();
                    }
                    else {
                        Limage.setVisibility(View.GONE);
                        dialog.setTitle("");
                        dialog.show();
                    }
                }catch (Exception e){
                    e=e;
                }
            }
        };


        Cursor sum=db.rawQuery("SELECT  SUM(CAST(Amount as Integer))  FROM history Where signed=-1 And Picture Like '%"+year+String.format("%02d",month)+String.format("%02d",day)+"%'",null);
        sum.moveToFirst();
        ((TextView)findViewById(R.id.neg_total)).setText("支出總和:"+sum.getInt(0));
        sum=db.rawQuery("SELECT  SUM(CAST(Amount as Integer))  FROM history Where signed=1 And Picture Like '%"+year+String.format("%02d",month)+String.format("%02d",day)+"%'",null);
        sum.moveToFirst();
        ((TextView)findViewById(R.id.pos_total)).setText("存放總和:"+sum.getInt(0));
        Cursor c=db.rawQuery("SELECT Category,Target,Amount,Picture, signed,Publisher  FROM history Where Picture Like '%"+year+String.format("%02d",month)+String.format("%02d",day) +"%'",null);

        if(c.moveToFirst()) {
            if(c.getInt(2)>0){
                do {
                    LinearLayout ll=(LinearLayout)inflater.inflate(R.layout.table_template,null);
                    ((TextView) ll.findViewById(R.id.category)).setText(c.getString(0));
                    ((TextView) ll.findViewById(R.id.payTarget)).setText(c.getString(1));
                    ((TextView) ll.findViewById(R.id.amount)).setText(Integer.parseInt(c.getString(2))*c.getInt(4)+"");
                    ((TextView) ll.findViewById(R.id.Publisher)).setText(c.getString(5));
                    ((LinearLayout)findViewById(R.id.tableScroll)).addView(ll);
                    ll.findViewById(R.id.watch).setTag(c.getString(3));
                    ll.setTag(c.getString(3));

                    ll.setOnClickListener(ocl);

                    if(!(new File(c.getString(3)).exists())) {

                            ll.findViewById(R.id.watch).setVisibility(View.GONE);
                    }else {
                        ll.findViewById(R.id.watch).setOnClickListener(ocl);
                    }
                } while (c.moveToNext());
            }
        }
    }
    public void toAdd(View view){
        Intent intent=new Intent();
        intent.putExtra("signed",1);
        intent.setClass(Expenditues_Rep.this,AddExpenditues.class);
        startActivity(intent);
        finish();
    }
    public void toCli(View view){
        Intent intent=new Intent();
        intent.putExtra("signed",-1);
        intent.setClass(Expenditues_Rep.this,AddExpenditues.class);
        startActivity(intent);
        finish();
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ((keyCode == KeyEvent.KEYCODE_BACK)) {   //確定按下退出鍵

            Intent intent=new Intent();
            intent.setClass(Expenditues_Rep.this,MainActivity.class);
            startActivity(intent);
            finish();
            return true;

        }

        return super.onKeyDown(keyCode, event);

    }
    public void dayClick(View view){
        if(((TextView)view).getText().toString().equals("▶")){

            _day=(day+1)==32?1:day+1;
            if(_day==1)monthClick(view);
            else check();
        }else {

            _day=day-1==0?31:day-1;
            if(_day==31)monthClick(view);
            else check();
        }
    }
    public void monthClick(View view){
        if(((TextView)view).getText().toString().equals("▶")){
            if(month+1==32)_year++;
            _month=(month+1)==13?1:month+1;
            _day=1;
        }else {
            if(month-1==0)_year--;
            _month=month-1==0?12:month-1;
            _day=31;
        }
        check();
    }
    public void check(){
        Cursor c=db.rawQuery("Select substr(max(Picture),16,8),substr(min(Picture),16,8) From  history",null);
        c.moveToFirst();
        int ch=Integer.parseInt(_year+String.format("%02d",_month)+String.format("%02d",_day));
        if(Integer.parseInt(c.getString(1))<=ch && Integer.parseInt(c.getString(0))>=ch )
        {
            year=_year;
            month=_month;
            day=_day;
            makeView();
        }
        else {
            _year=year;
            _month=month;
            _day=day;
        }
    }
}
