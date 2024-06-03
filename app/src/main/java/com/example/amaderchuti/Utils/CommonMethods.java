package com.example.amaderchuti.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonMethods {
    public static boolean isNotEmpty(String text){
        if(text!=null && text.equals(""))return false;
        return true;
    }
    public static String getFormatChangedDate(String mDate, String oldDateFormat, String newDateFormat) {
        final SimpleDateFormat inputFormat = new SimpleDateFormat(oldDateFormat);
        final SimpleDateFormat outputFormat = new SimpleDateFormat(newDateFormat);
        try {
            Date date = inputFormat.parse(mDate);
            return outputFormat.format(date);
        } catch (Exception e) {
            //e.printStackTrace();
            return mDate;
        }
    }
}
