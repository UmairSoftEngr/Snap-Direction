package com.example.devilihboii.fyp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;


public class Splash extends Activity {
ImageView s;

    @Override
    protected void onStop() {
        super.onStop();
       Splash.this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Splash.this.finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


       s= (ImageView) findViewById(R.id.snapi);
        s.setBackgroundResource(R.drawable.logof);
     YoYo.with(Techniques.BounceInDown).duration(5000).playOn(s);






        Thread timerThread = new Thread(){
            public void run(){
                try{

                    sleep(6000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    Intent i = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.upi, R.anim.up);

                    Splash.this.finish();
                }
            }
        };
        timerThread.start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        Splash.this.finish();

    }

}