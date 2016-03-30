package com.example.devilihboii.fyp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;


public class Stopping extends ActionBarActivity {








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopping);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "KGColdCoffee.ttf");
        Sms obj=new Sms();
        String address=obj.add;
        String home=obj.homei;
       TextView tv= (TextView) findViewById(R.id.mesg);
        tv.setTypeface(custom_font);
        LinearLayout l= (LinearLayout) findViewById(R.id.lin);
        YoYo.with(Techniques.BounceInDown).duration(3000).playOn(l);
        tv.setText("You already started trip from "+ home +" to "+address+".You'll have to cancel it before starting another one.");
        findViewById(R.id.stoper).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getBaseContext(),
                        MyScheduledReceiver.class);
                myIntent.setAction(MyScheduledReceiver.ACTION_ALARM_RECEIVER);
                PendingIntent pendingIntent
                        = PendingIntent.getBroadcast(getBaseContext(),
                        1001, myIntent, 0);

                AlarmManager alarmManager
                        = (AlarmManager) getSystemService(ALARM_SERVICE);

                alarmManager.cancel(pendingIntent);//important
                pendingIntent.cancel();//important
               // Toast.makeText(getApplicationContext(), "cancel", Toast.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                stopService(new Intent(getApplicationContext(), SmsService.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                overridePendingTransition(R.anim.downi, R.anim.down);

                finish();

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_stopping, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
