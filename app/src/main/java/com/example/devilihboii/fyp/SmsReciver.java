package com.example.devilihboii.fyp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

/**
 * Created by Devilih Boii on 5/10/2015.
 */
public class SmsReciver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // get SMS map from intent
        Bundle extras = intent.getExtras();
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        String number = sharedPrefs.getString("pick", "number");
number=number.replace(" ","");
        String Mess = sharedPrefs.getString("mess", "checked");
      //  Toast.makeText(context, "pref" + number + "mese" + Mess, Toast.LENGTH_LONG).show();

        // a notification message
        String messages = "";
        if (extras != null) {
            // get array data from SMS
            Object[] smsExtra = (Object[]) extras.get("pdus"); // "pdus" is the key

            for (int i = 0; i < smsExtra.length; ++i) {
                // get sms message
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) smsExtra[i]);
                // get content and number
                String body = sms.getMessageBody();
                String address = sms.getOriginatingAddress();
                // create display message
                messages += "SMS from " + address + " :\n";
                messages += body + "\n";
                PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                String my = null,my2=null;

                try {
                    Phonenumber.PhoneNumber chngnum = phoneUtil.parse(address, "CH");
                    my = phoneUtil.format(chngnum, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
                    my = my.replace(" ", "");
                    Phonenumber.PhoneNumber chngnumpref = phoneUtil.parse(number, "CH");
                    my2 = phoneUtil.format(chngnumpref, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
                    my2 = my.replace(" ", "");
                   // Toast.makeText(context,my2 +"  \t "+ "chnged" + my , Toast.LENGTH_LONG).show();
                } catch (NumberParseException e) {
                    e.printStackTrace();
                }
               Mess = Mess.replaceAll(" ","");
                body=body.replaceAll(" ","");
                if (body.equals(Mess) && my2.equals(my)) {
                  //  Toast.makeText(context, messages + "num " + my2, Toast.LENGTH_SHORT).show();
                    try {
                        Intent data = new Intent(context, MyService.class);
                        data.putExtra("number", my2);
                        context.startService(data);

                    } catch (Exception e) {

                        Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();

                    }
                } else {

                   // Toast.makeText(context, messages, Toast.LENGTH_SHORT).show();

                }
            }


        }
    }

}