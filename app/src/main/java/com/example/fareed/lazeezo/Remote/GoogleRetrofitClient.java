package com.example.fareed.lazeezo.Remote;

import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by fareed on 2/11/2018.
 */

public class GoogleRetrofitClient {

    private static Retrofit retrofit=null;

    public static Retrofit getGoogleClient(String baseURl){
        if(retrofit==null){
            retrofit=new Retrofit.Builder()
                    .baseUrl(baseURl)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
