package lursun.camera;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.hardware.Camera.AutoFocusCallback;
import android.view.accessibility.AccessibilityManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.hardware.Camera.PictureCallback;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by admin on 2016/10/29.
 */
public class AddExpenditues extends Activity {
    boolean lock = true;
    boolean picture= false;
    ImageView imageView;
    Bitmap bmp;
    int signed;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_expenditures);
        Intent intent=getIntent();
        signed =intent.getIntExtra("signed",1);
        if(signed==1) {
            ((TextView) findViewById(R.id.targetT)).setText("名         稱");
        }
        imageView = (ImageView) findViewById(R.id.imageView);
        View.OnClickListener ocl=new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File dirFile = new File("/sdcard/camera");
                if (!dirFile.exists()) dirFile.mkdir();
                Uri imageUri = Uri.fromFile(new File("/sdcard/camera/temp.jpg"));
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent,0);
            }
        };
        imageView.setOnClickListener(ocl);


        Spinner SpinnerS = (Spinner)findViewById(R.id.type);
        //設定功能表項目陣列，使用createFromResource()
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, signed==-1 ?R.array.type:R.array.type2,
                android.R.layout.simple_spinner_item);
        //設定選單
        adapter.setDropDownViewResource(R.layout.type);
        //設定adapter
        SpinnerS.setAdapter(adapter);

    }
    public void openDrawer(View view){
        new Thread() {
            @Override
            public void run() {
                SQLite sqLite=new SQLite(getApplicationContext());
                SQLiteDatabase db= sqLite.getReadableDatabase();
                try
                {
                    Cursor c=db.rawQuery("Select * From setting",null);
                    c.moveToFirst();
                    Socket socket = new Socket(c.getString(1),9100);
                    OutputStream os=socket.getOutputStream();
                    byte[] DLEDC4={0x10,0x14,0x01,0x00,0x01};
                    os.write(DLEDC4);
                    os.close();
                    socket.close();
                }
                catch(Exception e){
                    e=e;
                }
            }
        };
    }
    public void save(View view){
        if(!( ((EditText)findViewById(R.id.payTarget)).getText().toString().equals("") ||
        ((EditText)findViewById(R.id.amount)).getText().toString().equals("") )
                ) {


            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

            String date = sdf.format(new Date());
            if (picture) {
                FileOutputStream fop;
                try {
                    File file = new File("/sdcard/camera/temp.jpg");
                    file.renameTo(new File("/sdcard/camera/"+date+".jpg"));
                } catch (Exception e) {
                    System.out.println("IOException");
                }
            }
            SQLite sqLite = new SQLite(getApplication());
            SQLiteDatabase db = sqLite.getReadableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("Category", getResources().getStringArray(signed==-1 ?R.array.type:R.array.type2) [((Spinner) findViewById(R.id.type)).getSelectedItemPosition()]);




            cv.put("Target", ((EditText) findViewById(R.id.payTarget)).getText().toString());
            cv.put("Amount", Integer.parseInt(((EditText) findViewById(R.id.amount)).getText().toString()));
            cv.put("Picture", String.format("/sdcard/camera/%s.jpg", date));
            cv.put("signed",signed);
            cv.put("Publisher",((EditText) findViewById(R.id.Publisher)).getText().toString());
            db.insert("history", null, cv);
            Toast.makeText(getApplicationContext(), "儲存完畢", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent();
            intent.setClass(AddExpenditues.this,Expenditues_Rep.class);
            startActivity(intent);
            finish();
        }
        else
        Toast.makeText(getApplicationContext(), "儲存失敗，對象、金額不得為空", Toast.LENGTH_SHORT).show();
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
  //      camera.stopPreview();
  //      camera.release();
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {   //確定按下退出鍵
            Intent intent=new Intent();
            intent.setClass(AddExpenditues.this,Expenditues_Rep.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        try {
            bmp = BitmapFactory.decodeFile("/sdcard/camera/temp.jpg");
            ((ImageView)findViewById(R.id.imageView)).setImageBitmap(bmp);
            picture=true;
        }catch (Exception e){
        }
        //imageView.setImageBitmap(bmp);
        //Toast.makeText(AddExpenditues.this,"拍照成功",Toast.LENGTH_SHORT).show();
        //picture= true;
    }
}