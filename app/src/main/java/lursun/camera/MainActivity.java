package lursun.camera;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

public class MainActivity extends Activity  {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void toExpenditures (View view){
        Intent intent=new Intent();
        intent.setClass(MainActivity.this,Expenditues_Rep.class);
        startActivity(intent);
        finish();
    }
    public void toCheckout (View view){
        Intent intent=new Intent();
        intent.setClass(MainActivity.this,CheckoutRep.class);
        startActivity(intent);
        finish();
    }
    public void toSetting (View view){
        Intent intent=new Intent();
        intent.setClass(MainActivity.this,ToSetting.class);
        startActivity(intent);
        finish();
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {   //確定按下退出鍵
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}