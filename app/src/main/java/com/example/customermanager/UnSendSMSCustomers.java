package com.example.customermanager;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

public class UnSendSMSCustomers extends AppCompatActivity {
    private boolean isInternetAvailable=true;

    RecyclerView recyclerView;
    ArrayList<String> id,name, mobNo, pack, startDate, endDate;
    ArrayList<Boolean> smsStatus;
    DBHelper dbHelper;
    ViewCustomerAdapter viewCustomerAdapter;

    ImageView noInternetImgView;

    private NetworkChangeReceiver networkChangeReceiver;


    @Override
    protected void onResume() {
        super.onResume();
        registerNetworkChange();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unRegisterNetworkChange();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_un_send_smscustomers);

        ActionBar actionBar = getSupportActionBar();
        ColorDrawable colorDrawable
                = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            colorDrawable = new ColorDrawable(getColor(R.color.myBlue));
        }
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setTitle("View Un-Send Customer's");

        recyclerView = findViewById(R.id.unSendRecView);

        noInternetImgView=findViewById(R.id.ivNoInternetUnSendCustomers);

        dbHelper = new DBHelper(this);
        id=new ArrayList<>();
        name = new ArrayList<>();
        mobNo = new ArrayList<>();
        pack = new ArrayList<>();
        startDate = new ArrayList<>();
        endDate = new ArrayList<>();

        smsStatus = new ArrayList<>();


        viewCustomerAdapter = new ViewCustomerAdapter(this, name, mobNo, pack, startDate, endDate);
        recyclerView.setAdapter(viewCustomerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        displayCustomers();

    }

    private void displayCustomers() {
        Cursor cursor = dbHelper.getFailedCustomersData();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No data Exist", Toast.LENGTH_SHORT).show();
            return;
        } else {
            while (cursor.moveToNext()) {
                id.add(cursor.getString(0));
                name.add(cursor.getString(1));
                mobNo.add(cursor.getString(2));
                pack.add(cursor.getString(3));
                startDate.add(cursor.getString(4));
                endDate.add(cursor.getString(5));
                //smsStatus.add(false);

            }
            viewCustomerAdapter.notifyDataSetChanged();
        }
    }

    private void registerNetworkChange(){
        if(networkChangeReceiver==null){
            networkChangeReceiver=new NetworkChangeReceiver();
            IntentFilter intentFilter=new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(networkChangeReceiver,intentFilter);
        }
    }

    private void unRegisterNetworkChange(){
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

    private class NetworkChangeReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if(isNetworkAvailable()){
                isInternetAvailable=true;
                noInternetImgView.setVisibility(View.GONE);
            }
            else{
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