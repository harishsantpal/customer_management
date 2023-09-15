package com.example.customermanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DBHelper extends SQLiteOpenHelper {


    public DBHelper(Context context) {
        super(context, "MyDataBase.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create Table registeruser(USERNAME TEXT PRIMARY KEY NOT NULL, USERPASS TEXT NOT NULL)");
        // sqLiteDatabase.execSQL("create Table addcustomer(CUSTOMERNAME TEXT NOT NULL, CUSTOMERMOBNO TEXT NOT NULL, CUSTOMERPACK TEXT NOT NULL,CUSTOMERSTARTDATE TEXT NOT NULL,CUSTOMERENDDATE TEXT NOT NULL,CUSTOMERSENT INTEGER DEFAULT 0)");
        //sqLiteDatabase.execSQL("create Table date(LAST_DATE TEXT)");

        sqLiteDatabase.execSQL("create Table addcustomer(CUSTOMERID INTEGER PRIMARY KEY AUTOINCREMENT,CUSTOMERNAME TEXT NOT NULL, CUSTOMERMOBNO TEXT NOT NULL, CUSTOMERPACK TEXT NOT NULL,CUSTOMERSTARTDATE TEXT NOT NULL,CUSTOMERENDDATE TEXT NOT NULL, CUSTOMERMESSAGEDATE TEXT,CUSTOMERSENTSTATUS INTEGER DEFAULT 0)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop Table if exists registeruser");
        sqLiteDatabase.execSQL("drop Table if exists addcustomer");
        //sqLiteDatabase.execSQL("drop Table if exists date");
        onCreate(sqLiteDatabase);
    }

    public Boolean insertUserRegData(String name, String pass) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("USERNAME", name);
        contentValues.put("USERPASS", pass);
        long result = sqLiteDatabase.insert("registeruser", null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Boolean checkUserNamePass(String USERNAME, String USERPASS) {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("select * from registeruser where USERNAME=? and USERPASS=?", new String[]{USERNAME, USERPASS});
        if (cursor.getCount() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean insertCustomerData(String name, String mobNo, String pack, String startDate, String endDate, String messageDate) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("CUSTOMERNAME", name);
        contentValues.put("CUSTOMERMOBNO", mobNo);
        contentValues.put("CUSTOMERPACK", pack);
        contentValues.put("CUSTOMERSTARTDATE", startDate);
        contentValues.put("CUSTOMERENDDATE", endDate);
        contentValues.put("CUSTOMERMESSAGEDATE", messageDate);
        long result = sqLiteDatabase.insert("addcustomer", null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public void updateMessageDate(int id, String mDate) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("CUSTOMERMESSAGEDATE", mDate);
        sqLiteDatabase.update("addcustomer", contentValues, "CUSTOMERID=?", new String[]{String.valueOf(id)});
    }


    public Cursor getCustomerData() {
        String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select *from addcustomer", null);
        Log.d("checkCurrentDate", "" + date);
        return cursor;

    }

    public Cursor getFailedCustomersData() {
        String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select *from addcustomer where CUSTOMERSENTSTATUS=1", null);
        Log.d("checkCurrentDate", "" + date);
        return cursor;

    }

    public Cursor getOnMessageDate() {
        String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM addcustomer WHERE CUSTOMERSENTSTATUS=0 and CUSTOMERMESSAGEDATE  = '" + date + "'", null);
        return cursor;
    }

    public void markCustomerAsFailed(int status, String customerId) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("CUSTOMERSENTSTATUS", status);
        sqLiteDatabase.update("addcustomer", contentValues, "CUSTOMERID=?", new String[]{customerId});
    }


}
