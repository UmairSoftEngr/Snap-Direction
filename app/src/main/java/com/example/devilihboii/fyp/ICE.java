package com.example.devilihboii.fyp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class ICE extends Activity {
    Typeface custom_font ;
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ice);

        custom_font = Typeface.createFromAsset(getAssets(), "KGColdCoffee.ttf");

        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        String number = sharedPrefs.getString("pick", "");
        Boolean sw = sharedPrefs.getBoolean("ES", false);
        findViewById(R.id.settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getApplicationContext(), Preferences.class), 1);
                overridePendingTransition(R.anim.upi, R.anim.up);

            }
        });

        if (number.equals("")) {

          //  Toast.makeText(getApplicationContext(), "if", Toast.LENGTH_LONG).show();
            AlertDialog.Builder db = new AlertDialog.Builder(ICE.this);
            db.setTitle("ICE number not set");
            db.setMessage("Please go to settings and set ICE number");
            db.setCancelable(false);
            db.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivityForResult(new Intent(getApplicationContext(), Preferences.class), 1);

                }
            });


            db.show();
        } else {

            displayUserSettings();

        }


        if (sw == true) {


            PackageManager pm = ICE.this.getPackageManager();

            ComponentName componentName = new ComponentName(getApplication(), SmsReciver.class);
            pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
          //  Toast.makeText(getApplicationContext(), "Service Activated", Toast.LENGTH_LONG).show();

        } else {
            ComponentName receiver = new ComponentName(getApplication(), SmsReciver.class);
            PackageManager pm = ICE.this.getPackageManager();

            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_DEFAULT,
                    PackageManager.DONT_KILL_APP);
          //  Toast.makeText(getApplicationContext(), "Service Deactivated", Toast.LENGTH_SHORT).show();

        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            displayUserSettings();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ice, menu);
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
            startActivityForResult(new Intent(getApplicationContext(), Preferences.class), 1);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void displayUserSettings() {
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);


        String settings, sm = "";
        Boolean sw = sharedPrefs.getBoolean("ES", false);
        if (sw == true) {


            PackageManager pm = ICE.this.getPackageManager();

            ComponentName componentName = new ComponentName(getApplication(), SmsReciver.class);
            pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
          //  Toast.makeText(getApplicationContext(), "Service activated", Toast.LENGTH_LONG).show();

        } else {
            ComponentName receiver = new ComponentName(getApplication(), SmsReciver.class);
            PackageManager pm = ICE.this.getPackageManager();

            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_DEFAULT,
                    PackageManager.DONT_KILL_APP);
         //   Toast.makeText(getApplicationContext(), "Service Deactivated", Toast.LENGTH_SHORT).show();

        }

        settings = " Number: "
                + sharedPrefs.getString("pick", "");
if(sharedPrefs.getString("pick", "").equals("")){

    AlertDialog.Builder db = new AlertDialog.Builder(ICE.this);
    db.setTitle("ICE number not set");
    db.setMessage("Please go to settings and set ICE number");
    db.setCancelable(false);
    db.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            startActivityForResult(new Intent(getApplicationContext(), Preferences.class), 1);

        }
    });
    db.show();
} else {

        sm = " ICE TEXT: " + sharedPrefs.getString("mess", "NOPASSWORD");

        TextView textViewSetting = (TextView) findViewById(R.id.textView);
        TextView sms = (TextView) findViewById(R.id.sms);
        TextView ic = (TextView) findViewById(R.id.ic);
        textViewSetting.setTypeface(custom_font);
        sms.setTypeface(custom_font);
        ic.setTypeface(custom_font);
        textViewSetting.setText(settings);
        sms.setText(sm);

        if (sw == true) {
            ic.setText(" Service is activated");
        } else {
            ic.setText(" Service is not activated");
        }
    }}
}
