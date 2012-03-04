/*
 * Copyright 2007 Yodlee, Inc.  All Rights Reserved.  Your use of this code 
 * requires a license from Yodlee.  Any such license to this code is 
 * restricted to evaluation/illustrative purposes only. It is not intended 
 * for use in a production environment, and Yodlee disclaims all warranties 
 * and/or support obligations concerning this code, regardless of the terms 
 * of any other agreements between Yodlee and you."
 */
package YodleeSrc;


import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.NumberFormat;
import java.text.DecimalFormat;

import com.yodlee.soap.core.dataservice.YMoney;


public class Formatter {
    public static final String DATE_SHORT_FORMAT = "MM-dd-yyyy";
    public static final String DATE_LONG_FORMAT = "MM-dd-yyyy hh:mm:ss";

    
    /**
     * Format date using the format specified in the parameter.
     * @param date
     * @param dateFormat
     * @return
     */
    public static String formatDate(Calendar date, String dateFormat){
        if(date == null ){
            return "";
        }
        Date dt = date.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        return formatter.format(dt);
     }
    
    /**
     * Format date using the format specified in the parameter.
     * @param date
     * @param dateFormat
     * @return
     */
    public static String formatDate(Date date, String dateFormat){
        if(date == null ){
            return "";
        }
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        return formatter.format(date);
     }

    /**
     * Format a number but adding commas
     *
     * @param number
     * @return number with commas
     */
    public static String formatNumber(long number){
        NumberFormat  nf = new DecimalFormat("#,###,###");
        return nf.format(number);
    }

    /**
     * Format a number but adding commas
     *
     * @param number
     * @return number with commas
     */
    public static String formatNumber(Long number){
        return formatNumber(number.longValue()) ;
    }

    /**
     * Format a number but adding commas
     *
     * @param number
     * @return number with commas
     */
    public static String formatNumber(Double number){
        return formatNumber(number.longValue()) ;
    }


    /**
     * Format a number but adding commas
     *
     * @param money
     * @return number with commas and currenct code
     */
    public static String formatMoney(YMoney money){
        if(money == null){
            return "null";
        }
        if(money.getAmount() == null){
            return "null";
        }
        NumberFormat  nf = new DecimalFormat("#,###,###");
        String m = nf.format(money.getAmount());
        if(money.getCurrencyCode() != null && money.getCurrencyCode().equalsIgnoreCase("USD")){
            m = "$"+m;
        } else{
            m = m + " " + (money.getCurrencyCode() != null ? money.getCurrencyCode() : "") ;
        }
        return m;
    }
}
