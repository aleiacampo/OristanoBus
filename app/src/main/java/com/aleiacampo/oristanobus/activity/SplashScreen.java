package com.aleiacampo.oristanobus.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

import com.aleiacampo.oristanobus.R;


public class SplashScreen extends Activity {

    private TextView textViewOristanoBus;
    private Typeface oasisFont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        textViewOristanoBus = (TextView) findViewById(R.id.textView_OB);

        oasisFont = Typeface.createFromAsset(getAssets(), "fonts/oasis.ttf");

        textViewOristanoBus.setTypeface(oasisFont);

        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(900);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    Intent intent = new Intent(SplashScreen.this, BaseActivity.class);
                    startActivity(intent);
                }
            }
        };
        timerThread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

}