package com.example.customermanager;

import static com.example.customermanager.SendSMS.SMS_DELIVERED_ACTION;
//import static com.example.customermanager.SendSMS.smsStatus;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class SmsDeliveryReceiver extends BroadcastReceiver {

    DBHelper dbHelper;


    @Override
    public void onReceive(Context context, Intent intent) {
        dbHelper = new DBHelper(context);

        String action = intent.getAction();

        Log.d("checkRec", "Service is started!");

      //  if (action.equals(SMS_DELIVERED_ACTION)) {


            try {
                switch (getResultCode()) {

                    case Activity.RESULT_OK:
                        //int status=-1;
                        // Message sent successfully
                        Toast.makeText(context, "SMS sent", Toast.LENGTH_SHORT).show();
                        Log.d("checkSmsSend", "SMS Send" + Activity.RESULT_OK);

                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:

                        Toast.makeText(context, "SMS sending failed", Toast.LENGTH_SHORT).show();
                        Log.d("checkSmsSend", "SMS sending failed" + SmsManager.RESULT_ERROR_GENERIC_FAILURE);
                        //Log.d("checkSendData",""+name+"  "+mobNo+"  "+pack+"  "+startDate+"  "+endDate+"  "+messageDate);


                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        // No service
                        Toast.makeText(context, "No service available", Toast.LENGTH_SHORT).show();
                        Log.d("checkSmsSend", "SMS service not available");


                        break;

                    default:
                }
            } catch (Exception e) {
                Log.d("exceptionCheck", "" + e);
            }


        }


   // }
}



