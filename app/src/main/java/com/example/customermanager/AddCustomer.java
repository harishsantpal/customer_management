package com.example.customermanager;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddCustomer extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private boolean isInternetAvailable=true;

    final Calendar myCalender = Calendar.getInstance();
    Date date = new Date();

    EditText cusNameEt, cusMobNoEt, cusStartDateEt, cusEndDateEt;
    Button addCustomerBtn;
    Spinner selectNumMonth;
    //AutoCompleteTextView selectPackageATV;

    String[] num = {"1", "2", "3"};
    String packValue;
    int monthValue;

    DBHelper dbHelper;

    String selectedOption;

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
        setContentView(R.layout.activity_add_customer);

        ActionBar actionBar = getSupportActionBar();
        ColorDrawable colorDrawable
                = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            colorDrawable = new ColorDrawable(getColor(R.color.myBlue));
        }
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setTitle("Add Customers");

        dbHelper = new DBHelper(this);

        cusNameEt = findViewById(R.id.etCustomerName);
        cusMobNoEt = findViewById(R.id.etCustomerMobNo);
        cusStartDateEt = findViewById(R.id.etStartDate);
        cusEndDateEt = findViewById(R.id.etEndDate);

        // selectPackageATV=findViewById(R.id.autoSelectPack1);

        selectNumMonth = findViewById(R.id.spMonthSelector);

        addCustomerBtn = findViewById(R.id.btnAddCustomer);

        noInternetImgView=findViewById(R.id.ivNoInternetAddCustomer);

        List<String> options = new ArrayList<>();
        options.add("Select");
        options.add("1");
        options.add("2");
        options.add("3");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectNumMonth.setAdapter(adapter);

        // Set an item selection listener to handle selection events
        selectNumMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Handle the selected item here
                selectedOption = (String) parent.getItemAtPosition(position);
                if (!selectedOption.equals("Select")) {
                    packValue = selectedOption;
                    monthValue = Integer.parseInt(packValue);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the case where no item is selected
            }
        });




        cusStartDateEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedOption.equals("Select")){
                    Toast.makeText(AddCustomer.this, "Select monthly pack", Toast.LENGTH_SHORT).show();
                }
                else {
                    openDatePickerDialog(view);
                }
            }
        });

        addCustomerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCustomer();
            }
        });
    }





    public void openDatePickerDialog(final View v) {
        // Get Current Date
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.TimePickerTheme,
                (view, year, monthOfYear, dayOfMonth) -> {
                    Calendar myCalender = Calendar.getInstance(); // Create a new Calendar instance

                    String selectedDate;
                    String endDate;
                    myCalender.set(year, monthOfYear, dayOfMonth);
                    myCalender.add(Calendar.MONTH, 1 * monthValue);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    selectedDate = dateFormat.format(myCalender.getTime());
                    endDate = selectedDate;

                    if (dayOfMonth < 10 && monthOfYear < 9) {
                        selectedDate = "0" + dayOfMonth + "/0" + (monthOfYear + 1) + "/" + year;
                    } else if (dayOfMonth < 10) {
                        selectedDate = "0" + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                    } else if (monthOfYear < 9) {
                        selectedDate = dayOfMonth + "/0" + (monthOfYear + 1) + "/" + year;
                    } else {
                        selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                    }

                    switch (view.getId()) {
                        case R.id.etStartDate:
                            ((EditText) v).setText(selectedDate);
                            break;
                    }

                    Log.d("checkDate", "" + selectedDate);
                    cusStartDateEt.setText(selectedDate);
                    Log.d("CheckEndDate", "" + endDate + "  " + Integer.parseInt(packValue));
                    cusEndDateEt.setText(endDate);
                }, myCalender.get(Calendar.YEAR), myCalender.get(Calendar.MONTH), myCalender.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
    }


    private void addCustomer() {
        //String cName,cuMobNo,cuStartDate;
        String name = cusNameEt.getText().toString();
        String mobNum = cusMobNoEt.getText().toString();
        String pack = String.valueOf(packValue);
        String startDate = cusStartDateEt.getText().toString();
        String endDate = cusEndDateEt.getText().toString();
        String messageDate = null;

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());


        if (name.isEmpty()) {
            cusNameEt.setError("enter customer name!");
            cusNameEt.requestFocus();
        } else if (mobNum.isEmpty()) {
            cusMobNoEt.setError("enter customer mobile NO.");
            cusMobNoEt.requestFocus();
        }else if (startDate.isEmpty()) {
            cusStartDateEt.setError("select staring date!");
            cusStartDateEt.requestFocus();
        } else {
            switch (pack) {
                case "1":

                    try {
                        Date date2 = sdf.parse(endDate);

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date2);
                        calendar.add(Calendar.DAY_OF_MONTH, -2);

                        Date mDate = calendar.getTime();
                        messageDate = sdf.format(mDate);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    Boolean checkCustomerData = dbHelper.insertCustomerData(name, mobNum, pack, startDate, endDate, messageDate);
                    if (checkCustomerData == true) {
                        cusNameEt.requestFocus();
                        Toast.makeText(this, "Customer added Successfully!", Toast.LENGTH_SHORT).show();
                        cusNameEt.setText("");
                        cusMobNoEt.setText("");
                        cusStartDateEt.setText("");
                        cusEndDateEt.setText("");

                        // Set the initial selection to the first option ("Select")
                        selectNumMonth.setSelection(0);



                    } else {
                        Toast.makeText(this, "Customer not added", Toast.LENGTH_SHORT).show();
                    }
                    break;


                case "2":

                    try {
                        Date date2 = sdf.parse(endDate);

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date2);
                        calendar.add(Calendar.DAY_OF_MONTH, -6);

                        Date mDate = calendar.getTime();
                        messageDate = sdf.format(mDate);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    Boolean checkCustomerData2 = dbHelper.insertCustomerData(name, mobNum, pack, startDate, endDate, messageDate);
                    if (checkCustomerData2 == true) {
                        cusNameEt.requestFocus();
                        Toast.makeText(this, "Customer added Successfully!", Toast.LENGTH_SHORT).show();
                        cusNameEt.setText("");
                        cusMobNoEt.setText("");
                        cusStartDateEt.setText("");
                        cusEndDateEt.setText("");
                        selectNumMonth.setSelection(0);
                    } else {
                        Toast.makeText(this, "Customer not added", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case "3":
                    try {
                        Date date2 = sdf.parse(endDate);

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date2);
                        calendar.add(Calendar.DAY_OF_MONTH, -8);

                        Date mDate = calendar.getTime();
                        messageDate = sdf.format(mDate);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    Boolean checkCustomerData3 = dbHelper.insertCustomerData(name, mobNum, pack, startDate, endDate, messageDate);
                    if (checkCustomerData3 == true) {
                        cusNameEt.requestFocus();
                        Toast.makeText(this, "Customer added Successfully!", Toast.LENGTH_SHORT).show();
                        cusNameEt.setText("");
                        cusMobNoEt.setText("");
                        cusStartDateEt.setText("");
                        cusEndDateEt.setText("");
                        selectNumMonth.setSelection(0);
                    } else {
                        Toast.makeText(this, "Customer not added", Toast.LENGTH_SHORT).show();
                    }
                    break;

                default:


            }

        }

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

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