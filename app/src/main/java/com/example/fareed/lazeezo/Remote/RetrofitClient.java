package com.example.fareed.lazeezo.Remote;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by fareed on 2/5/2018.
 */

public class RetrofitClient {

    private static Retrofit retrofit=null;

    public static Retrofit getClient(String baseURl){
        if(retrofit==null){
            retrofit=new Retrofit.Builder()
                    .baseUrl(baseURl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }


}
