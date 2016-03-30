package com.example.devilihboii.fyp;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Sms extends Activity {

    private static ArrayList<Map<String, String>> mPeopleList;
    private static SimpleAdapter mAdapter;
    private AutoCompleteTextView mTxtPhoneNo;
    ProgressDialog pd;
    Button  button;

   static String add,name,number="";

    LatLng la;


    private LocationManager locMan;
    LatLng lastLatLng = null;
    Address address = null;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    Location lastLoc = null;
    double lat, lng;
    int hour;
    long sec;
    int min;
    static String home,homei;

    Typeface custom_font ;
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.downi, R.anim.down);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);
        custom_font = Typeface.createFromAsset(getAssets(), "KGColdCoffee.ttf");
        final NumberPicker np = (NumberPicker) findViewById(R.id.numberPicker);
        final NumberPicker np1 = (NumberPicker) findViewById(R.id.numberPicker2);
        button = (Button) findViewById(R.id.start);
        mTxtPhoneNo = (AutoCompleteTextView) findViewById(R.id.ph);

        button.setTypeface(custom_font);
        mTxtPhoneNo.setTypeface(custom_font);
        mPeopleList = new ArrayList<Map<String, String>>();

mTxtPhoneNo.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {


        if (mPeopleList.size() <= 0) {
            Log.d("eror", "nodata");
            PopulatePeopleList();
        } else {
            Log.d("eror", "data" + mPeopleList.size());


        }
        mAdapter = new SimpleAdapter(getApplicationContext(), mPeopleList, R.layout.contactlist,
                new String[]{"Name", "Phone", "Type"}, new int[]{
                R.id.ccontName, R.id.ccontNo, R.id.ccontType});
        mTxtPhoneNo.setAdapter(mAdapter);


    }
});

        mTxtPhoneNo.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> av, View arg1, int index,
                                    long arg3) {
                Map<String, String> map = (Map<String, String>) av.getItemAtPosition(index);

                name = map.get("Name");
                number = map.get("Phone");
                mTxtPhoneNo.setText("" + name + "<" + number + ">,");


            }


        });


        np.setMaxValue(24);
        np.setMinValue(0);
        TextView t1, t2, t3;
        t1 = (TextView) findViewById(R.id.Lat);
        t2 = (TextView) findViewById(R.id.lng);
        t3 = (TextView) findViewById(R.id.add);
        t1.setTypeface(custom_font);
        t2.setTypeface(custom_font);
        t3.setTypeface(custom_font);
        np1.setMaxValue(60);
        np1.setMinValue(1);
        add = getIntent().getStringExtra("add");
        Bundle extras = getIntent().getExtras();
      final   double lt = extras.getDouble("lan");
        final double longitude = extras.getDouble("long");
        la = new LatLng(lt, longitude);

        t1.setText("Latitude=" + lt);
        t2.setText("Longitude=" + longitude);
        t3.setText(add);



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homei=location();
                hour = np.getValue();
                min = np1.getValue();
                long a, b;
                a = TimeUnit.HOURS.toMillis(hour);
                b = TimeUnit.MINUTES.toMillis(min);
                if(hour==24 && min>0){

                    Toast.makeText(getApplicationContext(),"Time exceded from 24 hours",Toast.LENGTH_LONG).show();

                }else{
                sec = a + b;


               // Toast.makeText(getApplicationContext(), "" + sec, Toast.LENGTH_LONG).show();
                if (number.equals("")) {
                    if(!mTxtPhoneNo.getText().toString().isEmpty()){

                        number=mTxtPhoneNo.getText().toString();
                    }else{
                    Toast.makeText(getApplicationContext(), "Please input inpuer", Toast.LENGTH_LONG).show();}

                } else {

                    Toast.makeText(getApplicationContext(), "number  2 \t" +
sec                +            "\t" + number , Toast.LENGTH_LONG).show();
                    Intent myIntent = new Intent(getBaseContext(),
                            MyScheduledReceiver.class);

                    myIntent.putExtra("num", number );
                    myIntent.putExtra("lat", lt);
                    myIntent.putExtra("lang", longitude);
                    myIntent.putExtra("add",add);
                    myIntent.putExtra("home",homei);
                    myIntent.setAction(MyScheduledReceiver.ACTION_ALARM_RECEIVER);
                    PendingIntent pendingIntent
                            = PendingIntent.getBroadcast(getBaseContext(),
                           1001, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);

                    AlarmManager alarmManager
                            = (AlarmManager) getSystemService(ALARM_SERVICE);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.add(Calendar.SECOND, 10);
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                            calendar.getTimeInMillis(), sec, pendingIntent);

                    Sms.this.finish();


                }
            }}
        });}




    public void PopulatePeopleList() {

        // mPeopleList.clear();
        Cursor people = getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        while (people.moveToNext()) {
            String contactName = people.getString(people
                    .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            String contactId = people.getString(people
                    .getColumnIndex(ContactsContract.Contacts._ID));
            String hasPhone = people
                    .getString(people
                            .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

            if ((Integer.parseInt(hasPhone) > 0)){
                // You know have the number so now query it like this
                Cursor phones = getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId,
                        null, null);
                while (phones.moveToNext()){
                    //store numbers and display a dialog letting the user select which.
                    String phoneNumber = phones.getString(
                            phones.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER));
                    String numberType = phones.getString(phones.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.TYPE));
                    Map<String, String> NamePhoneType = new HashMap<String, String>();
                    NamePhoneType.put("Name", contactName);
                    NamePhoneType.put("Phone", phoneNumber);
                    if(numberType.equals("0"))
                        NamePhoneType.put("Type", "Work");
                    else
                    if(numberType.equals("1"))
                        NamePhoneType.put("Type", "Home");
                    else if(numberType.equals("2"))
                        NamePhoneType.put("Type",  "Mobile");
                    else
                        NamePhoneType.put("Type", "Other");
                    //Then add this map to the list.
                    mPeopleList.add(NamePhoneType);
                }
                phones.close();
            }
        }
        people.close();
        // startManagingCursor(people);

    }




    public String location() {
        try {
            locMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locMan.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locMan.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
                Toast.makeText(getApplicationContext(),"Check Network Connectivity",Toast.LENGTH_LONG).show();
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
                        sb.append(" , "+address.getLocality());
                        sb.append(" , "+address.getSubAdminArea());
                        sb.append(" , "+address.getAdminArea());
                        sb.append(" , "+address.getCountryName());
                        home = sb.toString();
                        home=home.replace("null","").replaceAll("[\n\r]","");
                    }
                }


            }
        } catch (Exception sd) {
            Toast.makeText(getApplicationContext(),"Error \n"+sd,Toast.LENGTH_LONG).show();
        }
        return home;
    }




}
