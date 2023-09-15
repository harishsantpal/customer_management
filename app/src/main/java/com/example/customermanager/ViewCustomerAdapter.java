package com.example.customermanager;

//import static com.example.customermanager.SendSMS.smsStatus;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;

public class ViewCustomerAdapter extends RecyclerView.Adapter<ViewCustomerAdapter.ViewHolder> {

    private Context context;
    private ArrayList customerName, customerMobNo, customerPack, customerStartDate, customerEndDate;
  //  private ArrayList<Boolean> SmsStatus;
    DBHelper dbHelper;


    public ViewCustomerAdapter(Context context, ArrayList customerName, ArrayList customerMobNo, ArrayList customerPack, ArrayList customerStartDate, ArrayList customerEndDate) {


        this.context = context;
        this.customerName = customerName;
        this.customerMobNo = customerMobNo;
        this.customerPack = customerPack;
        this.customerStartDate = customerStartDate;
        this.customerEndDate = customerEndDate;
        //this.SmsStatus = SmsStatus;


        if (this.customerName == null) {
            this.customerName = new ArrayList<>();
        }
        if (this.customerMobNo == null) {
            this.customerMobNo = new ArrayList<>();
        }
        if (this.customerPack == null) {
            this.customerPack = new ArrayList<>();
        }
        if (this.customerStartDate == null) {
            this.customerStartDate = new ArrayList<>();
        }
        if (this.customerEndDate == null) {
            this.customerEndDate = new ArrayList<>();
        }
        /*if (this.SmsStatus == null) {
            this.SmsStatus = new ArrayList<>();
        }*/

    }

    @NonNull
    @Override
    public ViewCustomerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.customer_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewCustomerAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        if (customerName != null && position < customerName.size()) {
            holder.cName.setText(String.valueOf(customerName.get(position)));
        }
        if (customerMobNo != null && position < customerMobNo.size()) {
            holder.cMobNo.setText(String.valueOf(customerMobNo.get(position)));
        }
        if (customerPack != null && position < customerPack.size()) {
            holder.cPack.setText(String.valueOf(customerPack.get(position)));
        }
        if (customerStartDate != null && position < customerStartDate.size()) {
            holder.cStartDate.setText(String.valueOf(customerStartDate.get(position)));
        }
        if (customerEndDate != null && position < customerEndDate.size()) {
            holder.cEndDate.setText(String.valueOf(customerEndDate.get(position)));
        }
      /*  if (SmsStatus != null && position < SmsStatus.size()) {
            boolean status = SmsStatus.get(position);
            if (status) {
                holder.cSmsStatus.setText("Sent");
            } else {
                holder.cSmsStatus.setText("Failed");
            }
        }*/




    }

    @Override
    public int getItemCount() {
        //return customerName.size();
        if (customerName != null) {
            return customerName.size();
        } else {
            return 0;
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView cName, cMobNo, cPack, cStartDate, cEndDate;//, cSmsStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cName = itemView.findViewById(R.id.tvRecCusName);
            cMobNo = itemView.findViewById(R.id.tvRecCusMobNo);
            cPack = itemView.findViewById(R.id.tvRecCusPack);
            cStartDate = itemView.findViewById(R.id.tvRecCusStartDate);
            cEndDate = itemView.findViewById(R.id.tvRecCusEndDate);
           // cSmsStatus = itemView.findViewById(R.id.tvRecCusSmsStatus);
        }
    }
}
