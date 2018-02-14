package com.example.fareed.lazeezo.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.fareed.lazeezo.Model.User;
import com.example.fareed.lazeezo.Remote.APIService;
import com.example.fareed.lazeezo.Remote.GoogleRetrofitClient;
import com.example.fareed.lazeezo.Remote.IGoogleServices;
import com.example.fareed.lazeezo.Remote.RetrofitClient;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;


public class Common {
    public  static User currentUser;

    public static final String INTENT_FOOD_ID="FoodId";
    private static final String BASE_URL="https://fcm.googleapis.com/";

    private static final String GOOGLE_API_URL="https://maps.googleapis.com/";

    public static APIService getFCMService(){
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }

    public static IGoogleServices getGoogleMapAPI(){
        return GoogleRetrofitClient.getGoogleClient(GOOGLE_API_URL).create(IGoogleServices.class);
    }
    public static final String DELETE="Delete";
    public static String convertCodeToStatus(String status) {
        if(status.equals("0"))
            return "Placed";
        else if(status.equals("1"))
            return "On My Way";
        else
            return "Shipped";
    }

    public static boolean isInternet(Context context){
        ConnectivityManager connectivityManager=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager!=null){
            NetworkInfo[] info=connectivityManager.getAllNetworkInfo();
            if(info!=null){
                for(int i=0; i<info.length; i++){
                    if (info[i].getState()==NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }

    public static BigDecimal formatCurreny(String amount, Locale locale){
        NumberFormat format=NumberFormat.getCurrencyInstance(locale);
        if(format instanceof DecimalFormat){
            ((DecimalFormat)format).setParseBigDecimal(true);
            try {
                return (BigDecimal)format.parse(amount.replace("[^\\d.,]",""));
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
