package com.example.customermanager;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    TextView addCustomerTv,sendSMSTv,viewCustomerTv,unSendSMSCustomers;
    Button logoutBtn;

    SharedPreferences sharedPreferences;

    ImageView noInternetImgView;

    private NetworkChangeReceiver networkChangeReceiver;

    @Override
    protected void onResume() {
        super.onResume();
        registerNetworkReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unRegisterNetworkReceiver();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar=getSupportActionBar();
        ColorDrawable colorDrawable
                = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            colorDrawable = new ColorDrawable(getColor(R.color.myBlue));
        }
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setTitle("Home");

        addCustomerTv=findViewById(R.id.tvAddCustomer);
        sendSMSTv=findViewById(R.id.tvSendSMS);
        viewCustomerTv=findViewById(R.id.tvViewCustomers);
        unSendSMSCustomers=findViewById(R.id.tvViewCustomersFailed2);

        logoutBtn=findViewById(R.id.btnLogout);

        noInternetImgView=findViewById(R.id.ivNoInternetMainActivity);

        addCustomerTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),AddCustomer.class);
                startActivity(intent);
            }
        });

        sendSMSTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),SendSMS.class);
                startActivity(intent);
            }
        });

        viewCustomerTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),ViewCustomer.class);
                startActivity(intent);
            }
        });


        unSendSMSCustomers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),UnSendSMSCustomers.class);
                startActivity(intent);
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logingOut();
            }
        });
    }

    private void logingOut() {
        sharedPreferences=getSharedPreferences("LoginDetails",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean("flag",false);
        editor.commit();

        Intent intent=new Intent(getApplicationContext(),UserLogin.class);
        startActivity(intent);
        finish();
        Toast.makeText(this, "Logout successfully", Toast.LENGTH_SHORT).show();
    }

    private void registerNetworkReceiver(){
        if(networkChangeReceiver==null){
            networkChangeReceiver=new NetworkChangeReceiver();
            IntentFilter intentFilter=new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(networkChangeReceiver,intentFilter);
        }
    }

    private void unRegisterNetworkReceiver(){
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
                noInternetImgView.setVisibility(View.GONE);
            }
            else{
                noInternetImgView.setVisibility(View.VISIBLE);

            }
        }
    }


}