package com.example.devilihboii.fyp;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

public class SmsService extends Service {
    private LocationManager locMan;
    LatLng lastLatLng = null, lasti = null;
    TelephonyManager Tel;

    MyPhoneStateListener MyListener;
    public String signal;
    final String b[] = new String[1];


    Address address = null;
    String city = null;
    boolean isGPSEnabled = false;

    Location mylocation = new Location("");
    Location dest_location = new Location("");
    // flag for network status
    boolean isNetworkEnabled = false;

    Location lastLoc = null;
    double lat, lng;

    String smsNumberToSend, addi;


    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        //  getBatteryPercentage();

    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        Toast.makeText(this, "MyAlarmService.onBind()", Toast.LENGTH_LONG).show();
        return null;
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Toast.makeText(this, "MyAlarmService.onDestroy()", Toast.LENGTH_LONG).show();
    }


    private void sendSMS(String phoneNumber, String message) {
        String SENT = "SMS_SENT";
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
                new Intent(SENT), 0);
      //  Toast.makeText(this, "Service sms2", Toast.LENGTH_LONG).show();


        //---when the SMS has been sent---
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS sent" + location().toString(),
                                Toast.LENGTH_SHORT).show();
                        DataConnection(false);

                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic failure" + location().toString(),
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU" + location().toString(),
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off" + location().toString(),
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));


        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, null);
        Toast.makeText(this, "Service battery" + SENT, Toast.LENGTH_LONG).show();


    }

    @Override
    public boolean onUnbind(Intent intent) {
        // TODO Auto-generated method stub
        Toast.makeText(this, "MyAlarmService.onUnbind()", Toast.LENGTH_LONG).show();
        return super.onUnbind(intent);
    }

    public String location() {
        try {
            locMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locMan.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locMan.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                Toast.makeText(getApplicationContext(), "Check Network Connectivity", Toast.LENGTH_LONG).show();
            } else {

                if (isNetworkEnabled) {
                    lastLoc = locMan.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                } else if (isGPSEnabled) {
                    if (lastLoc == null) {
                        lastLoc = locMan.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                }
                if (lastLoc == null) {
                    Log.d("error", "empty");

                } else {

                    lat = lastLoc.getLatitude();
                    lng = lastLoc.getLongitude();
                    lastLatLng = new LatLng(lat, lng);
                    Geocoder geocoder;
                    List<Address> addresses = null;
                    geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                    addresses = geocoder.getFromLocation(lat, lng, 1);

                    if (addresses != null && addresses.size() > 0) {
                        address = addresses.get(0);
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                            sb.append(address.getAddressLine(i)).append("\n");
                        }
                        sb.append(address.getSubLocality());
                        sb.append("," + address.getLocality());
                        sb.append("," + address.getSubAdminArea());
                        sb.append("," + address.getAdminArea());
                        sb.append("," + address.getCountryName());
                        city = sb.toString();

                    }
                }


            }
        } catch (Exception sd) {
            Toast.makeText(getApplicationContext(), "Error \n" + sd, Toast.LENGTH_LONG).show();
        }
        return city;
    }
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public boolean DataConnection(boolean ON)
    {
        Toast.makeText(getApplicationContext(),"ko",Toast.LENGTH_LONG).show();

        try {
            //create instance of connectivity manager and get system connectivity service
            final ConnectivityManager conman = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            //create instance of class and get name of connectivity manager system service class
            final Class conmanClass  = Class.forName(conman.getClass().getName());
            //create instance of field and get mService Declared field
            final Field iConnectivityManagerField= conmanClass.getDeclaredField("mService");
            //Attempt to set the value of the accessible flag to true
            iConnectivityManagerField.setAccessible(true);
            //create instance of object and get the value of field conman
            final Object iConnectivityManager = iConnectivityManagerField.get(conman);
            //create instance of class and get the name of iConnectivityManager field
            final Class iConnectivityManagerClass=  Class.forName(iConnectivityManager.getClass().getName());
            //create instance of method and get declared method and type
            final Method setMobileDataEnabledMethod= iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled",Boolean.TYPE);
            //Attempt to set the value of the accessible flag to true
            setMobileDataEnabledMethod.setAccessible(true);
            //dynamically invoke the iConnectivityManager object according to your need (true/false)
            setMobileDataEnabledMethod.invoke(iConnectivityManager, ON);
        } catch (Exception e){
        }
        return true;
    }
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        Tel = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        /* Update the listener, and start it */
        MyListener = new MyPhoneStateListener();
        Tel.listen(MyListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        String loca = "";
        StringBuilder pl = new StringBuilder();
        if (isNetworkAvailable()) {

            try {
                loca = location();
                loca = loca.replace("null,", "");
                //loca=loca.replaceAll("\\s+","");
                if (loca.equals(null)) {
                    loca = "Latitude = " + lat + "," + "Longitude = " + lng;
                }
                pl.append(loca);
                Double lati, langi;
                Bundle extras = intent.getExtras();
                lati = extras.getDouble("lat");
                langi = extras.getDouble("lang");
                addi = extras.getString("add");
                getBatteryPercentage();
                mylocation.setLatitude(lati);
                mylocation.setLongitude(langi);
                dest_location.setLatitude(lat);
                dest_location.setLongitude(lng);
                //Toast.makeText(getApplicationContext(), "SMS service toast" + signal + "" + lasti + "" + lastLatLng, Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "" + e, Toast.LENGTH_LONG).show();

            } finally {
                Tel.listen(MyListener, PhoneStateListener.LISTEN_NONE);
            }
        }else{
            getBatteryPercentage();
            DataConnection(true);

            try {
                loca = location();
                loca = loca.replace("null,", "");
                //loca=loca.replaceAll("\\s+","");
                if (loca.equals(null)) {
                    loca = "Latitude = " + lat + "," + "Longitude = " + lng;
                }
                pl.append(loca);
                Double lati, langi;
                Bundle extras = intent.getExtras();
                lati = extras.getDouble("lat");
                langi = extras.getDouble("lang");
                addi = extras.getString("add");
                getBatteryPercentage();
                mylocation.setLatitude(lati);
                mylocation.setLongitude(langi);
                dest_location.setLatitude(lat);
                dest_location.setLongitude(lng);
               // Toast.makeText(getApplicationContext(), "SMS service toast" + signal + "" + lasti + "" + lastLatLng, Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "" + e, Toast.LENGTH_LONG).show();

            } finally {
                Tel.listen(MyListener, PhoneStateListener.LISTEN_NONE);

            }
        }
        double distance = mylocation.distanceTo(dest_location);//in meters


        Log.d("lat", lasti + "" + lastLatLng);
        if (distance < 400) {

           // Toast.makeText(getApplicationContext(), "Reached", Toast.LENGTH_LONG).show();

            pl.append("reached");
            Intent myIntent = new Intent(getBaseContext(),
                    MyScheduledReceiver.class);

            PendingIntent pendingIntent
                    = PendingIntent.getBroadcast(getBaseContext(),
                    0, myIntent, 0);

            AlarmManager alarmManager
                    = (AlarmManager) getSystemService(ALARM_SERVICE);

            alarmManager.cancel(pendingIntent);


        }

        smsNumberToSend = intent.getStringExtra("num");
        StringTokenizer st = new StringTokenizer(smsNumberToSend, ",");
        while (st.hasMoreElements()) {
            String tempMobileNumber = (String) st.nextElement();
            if (b[0] != null) {

                sendSMS(tempMobileNumber, "Location = " + pl + " \n Battery Percentage = " + b[0] + "\nSignal Strength = " + signal);
               // Toast.makeText(this, "Service Started" + "\t" + tempMobileNumber, Toast.LENGTH_LONG).show();

            } else {

                sendSMS(tempMobileNumber, "I am going to " + addi + " while driving i'll text my location and i'll call back when i'll reach there ");
            }


        }

        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();

        return START_NOT_STICKY;
    }

    private void getBatteryPercentage() {
       // Toast.makeText(this, "Service battery", Toast.LENGTH_LONG).show();
        BroadcastReceiver batteryLevel = new BroadcastReceiver() {

            public void onReceive(Context context, Intent intent) {
                context.unregisterReceiver(this);
                int currentLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                int level = -1;
                if (currentLevel >= 0 && scale > 0) {
                    level = (currentLevel * 100) / scale;
                }

                b[0] = level + "%";

            }


        };

        IntentFilter batteryLevelFilter = new IntentFilter(
                Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryLevel, batteryLevelFilter);
        //return bat[0];
    }


    private class MyPhoneStateListener extends PhoneStateListener {
        /* Get the Signal strength from the provider, each tiome there is an update */
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            if (signalStrength.getGsmSignalStrength() == 0 || signalStrength.getGsmSignalStrength() == 99) {

              //  Toast.makeText(getApplicationContext(), "NO signal ", Toast.LENGTH_SHORT).show();
                signal = "NO signal ";
            } else if (signalStrength.getGsmSignalStrength() >= 12) {


                //Toast.makeText(getApplicationContext(), "very good ", Toast.LENGTH_SHORT).show();
                signal = "Very Good";

            } else if (signalStrength.getGsmSignalStrength() >= 8) {
               // Toast.makeText(getApplicationContext(), " good ", Toast.LENGTH_SHORT).show();
                signal = "Good";


            } else if (signalStrength.getGsmSignalStrength() >= 5) {
               // Toast.makeText(getApplicationContext(), " poor ", Toast.LENGTH_SHORT).show();
                signal = "Poor";


            } else if (signalStrength.getGsmSignalStrength() < 5) {
               // Toast.makeText(getApplicationContext(), " very poorr ", Toast.LENGTH_SHORT).show();
                signal = "Very Poor";


            }


        }
    }

    ;/* End of private Class */


}