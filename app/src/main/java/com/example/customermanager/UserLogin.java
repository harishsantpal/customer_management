package com.example.customermanager;

import androidx.annotation.NonNull;
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
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class UserLogin extends AppCompatActivity {

    private boolean isInternetAvailable=true;

    EditText userNameEt, userPassEt;
    Button cancleBtn, loginBtn;
    TextView goRegisterTv;

    DBHelper dbHelper;
    SharedPreferences sharedPreferences;

    ImageView noInternetImgView;

    // for internet use
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
        setContentView(R.layout.activity_user_login);

        ActionBar actionBar = getSupportActionBar();
        ColorDrawable colorDrawable
                = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            colorDrawable = new ColorDrawable(getColor(R.color.myBlue));
        }
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setTitle("User Login");

        dbHelper = new DBHelper(this);
        sharedPreferences = getSharedPreferences("LoginDetails", MODE_PRIVATE);

        userNameEt = findViewById(R.id.etLoginName);
        userPassEt = findViewById(R.id.etLoginPass);

        cancleBtn = findViewById(R.id.btnLoginCancle);
        loginBtn = findViewById(R.id.btnLogin);

        goRegisterTv = findViewById(R.id.tvLinkUserRegister);

        noInternetImgView = findViewById(R.id.ivNoInternetUserLogin);


        chckUserLoginOrNot();


        cancleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userNameEt.setText("");
                userPassEt.setText("");
                userNameEt.requestFocus();
            }
        });

        //code for go to registration page
        goRegisterTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), UserRegistration.class);
                startActivity(intent);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkLogin();
            }
        });


    }

    private void chckUserLoginOrNot() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sharedPreferences = getSharedPreferences("LoginDetails", MODE_PRIVATE);
                Boolean check = sharedPreferences.getBoolean("flag", false);
                if (check) {
                    Intent intent = new Intent(UserLogin.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 0);
    }

    private void checkLogin() {

        String uName = userNameEt.getText().toString();
        String uPass = userPassEt.getText().toString();

        if (uName.isEmpty()) {
            userNameEt.setError("Enter user name!");
            userNameEt.requestFocus();
        } else if (uPass.isEmpty()) {
            userPassEt.setError("Enter password!");
            userPassEt.requestFocus();
        } else {
            Boolean checkNamePassword = dbHelper.checkUserNamePass(uName, uPass);
            if (checkNamePassword == true) {

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("UserName", uName);
                editor.putString("UserPass", uPass);
                editor.putBoolean("flag", true);
                editor.commit();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();
                userNameEt.setText("");
                userPassEt.setText("");
                finish();


            } else {
                Toast.makeText(this, "Invalid User Name or Password!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void registerNetworkReceiver() {
        if (networkChangeReceiver == null) {
            networkChangeReceiver = new NetworkChangeReceiver();
            IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(networkChangeReceiver, intentFilter);
        }
    }

    private void unRegisterNetworkReceiver() {
        if (networkChangeReceiver != null) {
            unregisterReceiver(networkChangeReceiver);
            networkChangeReceiver = null;
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

