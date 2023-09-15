package com.example.customermanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class SendSMS extends AppCompatActivity {

    private boolean isInternetAvailable=true;

    private static final String SMS_SENT_ACTION = "SMS_SENT";
    public static final String SMS_DELIVERED_ACTION = "SMS_DELIVERED_ACTION";

    RecyclerView recyclerView;
    public ArrayList<String> id, name, mobNo, pack, startDate, endDate, messageDate;
    //static ArrayList<Boolean> smsStatus;

    // ArrayList<String> myArrayList = new ArrayList<>();

    //ArrayList<String> myArrayList ;//= new ArrayList<>();
    //myArrayList.clear();

    DBHelper dbHelper;
    ViewCustomerAdapter viewCustomerAdapter;
    Button sendSMSBtn;
    SmsDeliveryReceiver smsDeliveryReceiver;
    ArrayList<String> myArrayList = new ArrayList<>();

    List<SubscriptionInfo> subscriptionInfoList;

    ImageView noInternetImgView;

    private NetworkChangeReceiver networkChangeReceiver;

    @Override
    protected void onResume() {
        super.onResume();
        registerChangeNetwork();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unRegisterChangeNetwork();
    }

    @SuppressLint("MissingInflatedId")
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_sms);

        ActionBar actionBar = getSupportActionBar();
        ColorDrawable colorDrawable
                = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            colorDrawable = new ColorDrawable(getColor(R.color.myBlue));
        }
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setTitle("Send SMS");

        checkPermission();
        myBroadCast();

        sendSMSBtn = findViewById(R.id.btnSendSMS);
        recyclerView = findViewById(R.id.recViewSmsSemd);

        noInternetImgView = findViewById(R.id.ivNoInternetSendSMS);

        dbHelper = new DBHelper(this);

        smsDeliveryReceiver = new SmsDeliveryReceiver();
        registerReceiver(smsDeliveryReceiver, new IntentFilter(SMS_SENT_ACTION));

        id = new ArrayList<>();
        name = new ArrayList<>();
        mobNo = new ArrayList<>();
        pack = new ArrayList<>();
        startDate = new ArrayList<>();
        endDate = new ArrayList<>();
        messageDate = new ArrayList<>();
        // smsStatus = new ArrayList<>();

        //myArrayList = new ArrayList<>();
        myArrayList.clear();

        subscriptionInfoList = new ArrayList<>();


        //myBroadCast();


        //myArrayList=new ArrayList<>();


        viewCustomerAdapter = new ViewCustomerAdapter(this, name, mobNo, pack, startDate, endDate);
        recyclerView.setAdapter(viewCustomerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getBeforeDates();

        viewCustomerAdapter.notifyDataSetChanged();


        sendSMSBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendSms();
            }
        });
    }


    private void sendSms() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 100);
            }
            return;
        }
        if (mobNo.size() == 0) {
            Toast.makeText(this, "No data Exist", Toast.LENGTH_SHORT).show();
        } else {

            Log.d("checkArrayIdSize", "id arraylist size : " + id.size() + "   " + id);

            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
            builder.setTitle("Select SIM");
            String[] simOptions = {"SIM 1", "SIM 2"};
            builder.setItems(simOptions, (dialog, which) -> {
                //sendSmsWithSim(which);
                if (which == 0) {
                    sendSmsWithSim(0); // Send SMS using SIM 1
                    Log.d("checkSimCard", "Sim1");
                } else if (which == 1) {
                    // Check if SIM 2 is available
                    SubscriptionManager subscriptionManager = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                        subscriptionManager = (SubscriptionManager) getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
                    }

                    // List<SubscriptionInfo> subscriptionInfoList = null;
                    subscriptionInfoList = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                        subscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
                    }
                    if (subscriptionInfoList != null && subscriptionInfoList.size() > 1) {
                        sendSmsWithSim(1); // Send SMS using SIM 2
                        Log.d("checkSimCard", "Sim2");
                    } else {
                        Toast.makeText(SendSMS.this, "SIM 2 not available", Toast.LENGTH_SHORT).show();
                    }
                }

            });
            builder.show();


        }
    }

    private void sendSmsWithSim(int simIndex) {
        for (int i = 0; i < mobNo.size(); i++) {

            String separateId = String.valueOf(id.get(i));
            String separateMobNo = String.valueOf(mobNo.get(i));
            String separateNames = String.valueOf(name.get(i));
            String separatePacks = String.valueOf(pack.get(i));
            String separateEndDate = String.valueOf(endDate.get(i));
            String separateMessageDate = String.valueOf(messageDate.get(i));
            String separateStartDate = String.valueOf(startDate.get(i));


            // ArrayList<String> myArrayList = new ArrayList<>();
            // myArrayList.clear();


            String no = separateMobNo;
            String message = "Hello " + separateNames + " your " + separatePacks + " months pack expires on " + separateEndDate;
            Log.d("checkSendMessage", "sending message : " + message);

            Intent dIntent = new Intent(SMS_DELIVERED_ACTION);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), Integer.parseInt(id.get(i)), new Intent(SMS_SENT_ACTION), PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent deliveredPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, dIntent, PendingIntent.FLAG_UPDATE_CURRENT);


            // Log.d("checkArrayIdSize","id arraylist size : "+id.size()+"   myarraylist size : "+myArrayList.size());


//            SmsManager smsManager = SmsManager.getDefault();
//            smsManager.sendTextMessage(no, null, message, pendingIntent, null);

            if (simIndex == 0) {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(no, null, message, pendingIntent, null);
                Log.d("checkSimInfo", " SimIndexValue : " + simIndex);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                if (subscriptionInfoList != null && subscriptionInfoList.size() > simIndex) {
                    int simId = subscriptionInfoList.get(simIndex).getSubscriptionId();
                    SmsManager smsManager = SmsManager.getSmsManagerForSubscriptionId(simId);
                    smsManager.sendTextMessage(no, null, message, pendingIntent, deliveredPendingIntent);
                    Log.d("checkSimInfo", "SimId : " + simId + " SimIndexValue : " + simIndex);

                    Log.d("checkSimInfo", " SimIndexValue : " + simIndex + "   " + simId);
                }
            } else {
                // SmsManager.getDefault().sendTextMessage(no, null, message, pendingIntent, null);

            }

        }

    }

    private void myBroadCast() {
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // myArrayList=new ArrayList<>();

                int resultCode = getResultCode();
                Log.d("CheckResultCode", "Result code : " + resultCode);


                if (resultCode == Activity.RESULT_OK) {
                    myArrayList.add(String.valueOf(Activity.RESULT_OK));
                    Log.d("checkSmsSendOrFailed", "sms sent : " + myArrayList);
                } else {
                    myArrayList.add(String.valueOf(Activity.RESULT_CANCELED));
                    Log.d("checkSmsSendOrFailed", "sms failed : " + myArrayList);
                }

                Log.d("checkArrayIdSize", "id arraylist size : " + id.size() + "  id arraylist : " + id + "   myarraylist size : " + myArrayList.size() + "  myarraylist : " + myArrayList);

                if (id.size() == myArrayList.size()) {
                    Log.d("checkEqualCondition", "Size  equal");

                    try {
                        for (int i = 0; i < myArrayList.size(); i++) {
                            Log.d("checkArrayData", "myArrayList position value : " + myArrayList.get(i) + "   id position value : " + id.get(i) + "  value of i : " + i);
                            Log.d("checkIValue", "myArrayList : " + myArrayList.get(i) + "  value of i : " + i);
                            Log.d("checkIValue", "id : " + id.get(i) + "  value of i : " + i);
                            Log.d("checkMyData", "without using to string : " + "  value of i : " + i + " array data" + id.get(i) + "  " + name.get(i) + "  " + pack.get(i) + "  " + startDate.get(i) + "  " + endDate.get(i) + "  " + messageDate.get(i));

                            //  if (myArrayList.get(i).equals("-1")) {
                            if ("-1".equals(myArrayList.get(i))) {
                                // int currentI = i;
                                Log.d("checkILoop", "if condition : " + myArrayList.get(i) + "  value of i : " + i);
                                updateData(Integer.parseInt(id.get(i)), name.get(i), mobNo.get(i), pack.get(i), startDate.get(i), endDate.get(i), messageDate.get(i));
                                // updateStatus(Integer.parseInt(id.get(i)),true);
                                Log.d("checkUpdateData", "Loop update data : " + Integer.parseInt(id.get(i)) + "  " + name.get(i) + "  " + mobNo.get(i) + "  " + pack.get(i) + "  " + startDate.get(i) + "  " + endDate.get(i) + "  " + messageDate.get(i));
                            } else {
                                Log.d("checkILoop", "else condition : " + myArrayList.get(i) + "  value of i : " + i);
                                //updateStatus(Integer.parseInt(id.get(i)),true);
                                updateFailedCustomer(1, Integer.parseInt(id.get(i)));
                            }
                        }
                        Log.d("checkMyListArray", "inside try-catch : " + myArrayList + "   " + myArrayList.size());
                        //myArrayList.clear();
                    } catch (Exception e) {
                        Log.d("checkException", "" + e);
                    }


                    Log.d("checkMyListArray", " out side try catch : " + myArrayList + "   " + myArrayList.size());
                    // recreate();
                    Intent refresh = new Intent(SendSMS.this, SendSMS.class);
                    refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    finish();
                    startActivity(refresh);

                } else {
                    Log.d("checkEqualCondition", "Size not equal");
                }
                //myArrayList.clear();


            }
        }, new IntentFilter(SMS_SENT_ACTION));

    }

    private void updateFailedCustomer(int status, int cId) {
        dbHelper.markCustomerAsFailed(status, String.valueOf(cId));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(smsDeliveryReceiver);
    }


    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_PHONE_STATE}, 100);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_PHONE_STATE}, 100);
                }
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_PHONE_STATE}, 100);
            }
        }
    }

    public void getBeforeDates() {
        String dateCurrent = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        Cursor cursor = dbHelper.getOnMessageDate();
        if (cursor.getCount() == 0) {
            // Toast.makeText(this, "No data Exist", Toast.LENGTH_SHORT).show();
            return;
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date currentDate = null;

            try {
                currentDate = dateFormat.parse(dateCurrent);
            } catch (ParseException e) {
                e.printStackTrace();
                return;
            }

            while (cursor.moveToNext()) {
                String messageDateStr = cursor.getString(6);
                String endDateStr = cursor.getString(5);

                try {
                    Date messageDate1 = dateFormat.parse(messageDateStr);
                    Date endDate1 = dateFormat.parse(endDateStr);

                    if (messageDate1.compareTo(endDate1) <= 0) {
                        id.add(cursor.getString(0));
                        name.add(cursor.getString(1));
                        mobNo.add(cursor.getString(2));
                        pack.add(cursor.getString(3));
                        startDate.add(cursor.getString(4));
                        endDate.add(endDateStr);
                        messageDate.add(messageDateStr);
                        //smsStatus.add(false);

                        viewCustomerAdapter.notifyDataSetChanged();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            Log.d("checkBeforeTenDays", "" + name + "\t" + mobNo);
        }
    }

    public void updateData(int idC, String nameC, String mobNoC, String packC, String startDateC, String endDateC, String messDateC) {

        //Log.d("checkArrayList",""+myArrayList);

        Log.d("checkUpdateData", "Method update data : " + idC + "  " + nameC + "  " + mobNoC + "  " + packC + "  " + startDateC + "  " + endDateC + "  " + messDateC);

        switch (packC) {

            case "1":
                String newMessageDate = null;

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Log.d("checkstep1", "it will run..!");

                Date date2 = null;
                try {
                    date2 = sdf.parse(String.valueOf(messDateC));
                    Log.d("checkSpeDate10", "" + date2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date2);
                calendar.add(Calendar.DAY_OF_MONTH, 1);

                Date mDate = calendar.getTime();
                newMessageDate = sdf.format(mDate);


                dbHelper.updateMessageDate(idC, newMessageDate);
                Log.d("checkName10", "" + idC);
                Log.d("checkNewDate1", "" + newMessageDate);

//                id.remove(idC);
//                mobNo.remove(mobNoC);
//                name.remove(nameC);
//                pack.remove(packC);
//                startDate.remove(startDateC);
//                endDate.remove(endDateC);
//                messageDate.remove(messDateC);
                viewCustomerAdapter.notifyDataSetChanged();

                break;

            case "2":
                String newMessageDate2 = null;

                SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Log.d("checkstep1", "it will run..!");

                Date date22 = null;
                try {
                    date22 = sdf2.parse(String.valueOf(messDateC));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Calendar calendar2 = Calendar.getInstance();
                calendar2.setTime(date22);
                calendar2.add(Calendar.DAY_OF_MONTH, 3);

                Date mDate2 = calendar2.getTime();
                newMessageDate2 = sdf2.format(mDate2);


                dbHelper.updateMessageDate(idC, newMessageDate2);
                Log.d("checkNewDate1", "" + newMessageDate2);
//                id.remove(idC);
//                mobNo.remove(mobNoC);
//                name.remove(nameC);
//                pack.remove(packC);
//                startDate.remove(startDateC);
//                endDate.remove(endDateC);
//                messageDate.remove(messDateC);
                viewCustomerAdapter.notifyDataSetChanged();
                break;

            case "3":
                String newMessageDate3 = null;

                SimpleDateFormat sdf3 = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Log.d("checkstep1", "it will run..!");

                Date date23 = null;
                try {
                    date23 = sdf3.parse(String.valueOf(messDateC));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Calendar calendar3 = Calendar.getInstance();
                calendar3.setTime(date23);
                calendar3.add(Calendar.DAY_OF_MONTH, 4);

                Date mDate3 = calendar3.getTime();
                newMessageDate3 = sdf3.format(mDate3);


                dbHelper.updateMessageDate(idC, newMessageDate3);
                Log.d("checkNewDate1", "" + newMessageDate3);
//                id.remove(idC);
//                mobNo.remove(mobNoC);
//                name.remove(nameC);
//                pack.remove(packC);
//                startDate.remove(startDateC);
//                endDate.remove(endDateC);
//                messageDate.remove(messDateC);
                viewCustomerAdapter.notifyDataSetChanged();
                break;

            default:

        }
    }

    private void registerChangeNetwork(){
        if(networkChangeReceiver==null){
            networkChangeReceiver=new NetworkChangeReceiver();
            IntentFilter intentFilter=new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(networkChangeReceiver,intentFilter);
        }
    }

    private void unRegisterChangeNetwork(){
        if(networkChangeReceiver!=null){
            unregisterReceiver(networkChangeReceiver);
            networkChangeReceiver=null;
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkCapabilities networkCapabilities = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                return networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
            } else {
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                return activeNetworkInfo != null && activeNetworkInfo.isConnected();
            }
        }

        return false;
    }

    private class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isNetworkAvailable()) {
                isInternetAvailable=true;
                noInternetImgView.setVisibility(View.GONE);
            } else {
                isInternetAvailable=false;
                noInternetImgView.setVisibility(View.VISIBLE);

            }
        }
    }

    @Override
    public void onBackPressed() {
        if(!isInternetAvailable){
            finishAffinity();
        }
        else {
            super.onBackPressed();
        }
    }
}