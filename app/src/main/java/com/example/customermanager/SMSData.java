package com.example.customermanager;

public class SMSData {
    private String mobileNumber,messageDate;
    private String monthValue;
    private String id;

    public SMSData(String mobileNumber, String monthValue, String id,String messageDate) {
        this.mobileNumber = mobileNumber;
        this.monthValue = monthValue;
        this.id = id;
        this.messageDate=messageDate;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getMonthValue() {
        return monthValue;
    }

    public String getId() {
        return id;
    }

    public String getMessageDate() {
        return messageDate;
    }
}
