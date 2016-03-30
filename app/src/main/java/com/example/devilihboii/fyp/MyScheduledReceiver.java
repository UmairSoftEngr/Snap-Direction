package com.example.devilihboii.fyp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Devilih Boii on 5/13/2015.
 */ public class MyScheduledReceiver extends BroadcastReceiver {




    public static final String ACTION_ALARM_RECEIVER = "ACTION_ALARM_RECEIVER";
    String number;
    double lat,lang;
    String add;
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub

        number = intent.getStringExtra("num");
        Bundle extras = intent.getExtras();
        lat=extras.getDouble("lat");
        lang=extras.getDouble("lang");
        add=extras.getString("add");






        Intent scheduledIntent = new Intent(context, SmsService.class);
        scheduledIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        scheduledIntent.putExtra("num", number);
        scheduledIntent.putExtra("lang", lang);
        scheduledIntent.putExtra("add",add);
        scheduledIntent.putExtra("lat", lat);
        context.startService(scheduledIntent);

    }
}