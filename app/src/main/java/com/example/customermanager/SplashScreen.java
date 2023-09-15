package com.example.customermanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

public class SplashScreen extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Window window = getWindow();

// Set the status bar color
        window.setStatusBarColor(getResources().getColor(R.color.myBlue));

        sharedPreferences = getSharedPreferences("LoginDetails", MODE_PRIVATE);

        chckUserLoginOrNot();
    }

    private void chckUserLoginOrNot() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sharedPreferences = getSharedPreferences("LoginDetails", MODE_PRIVATE);
                Boolean check = sharedPreferences.getBoolean("flag", false);
                if (check) {
                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Intent intent=new Intent(getApplicationContext(),UserLogin.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 3000);
    }
}