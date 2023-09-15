package com.example.customermanager;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class UserRegistration extends AppCompatActivity {

    private boolean isInternetAvailable=true;

    EditText regNameEt,regPassEt;
    Button cancleRegBtn,registerBtn;
    DBHelper dbHelper;

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
        setContentView(R.layout.activity_user_registration);

        ActionBar actionBar=getSupportActionBar();
        ColorDrawable colorDrawable
                = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            colorDrawable = new ColorDrawable(getColor(R.color.myBlue));
        }
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setTitle("User Registration");

        dbHelper=new DBHelper(this);

        regNameEt=findViewById(R.id.etRegisterName);
        regPassEt=findViewById(R.id.etRegisterPass);

        cancleRegBtn=findViewById(R.id.btnRegisterCancle);
        registerBtn=findViewById(R.id.btnRegister);

        noInternetImgView=findViewById(R.id.ivNoInternetUserRegistration);

        cancleRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                regNameEt.setText("");
                regPassEt.setText("");
                regNameEt.requestFocus();
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doRegistration();
            }
        });
    }

    private void doRegistration() {
        String nameReg=regNameEt.getText().toString();
        String passReg=regPassEt.getText().toString();

        if(nameReg.isEmpty()){
            regNameEt.setError("Enter user name!");
            regNameEt.requestFocus();
        }
        else if(passReg.isEmpty()){
            regPassEt.setError("Enter password");
            regPassEt.requestFocus();
        }
        else{
            Boolean checkUserData=dbHelper.insertUserRegData(nameReg,passReg);
            if(checkUserData==true){
                Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                regNameEt.setText("");
                regPassEt.setText("");
                Intent intent=new Intent(getApplicationContext(),UserLogin.class);
                startActivity(intent);
                finish();
            }
            else{
                Toast.makeText(this, "Registration Un-Successful!", Toast.LENGTH_SHORT).show();
            }
        }
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

    private class NetworkChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(isNetworkAvailable()){
                isInternetAvailable=true;
                noInternetImgView.setVisibility(View.GONE);
            }
            else {
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