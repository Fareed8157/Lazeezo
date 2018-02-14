package com.example.fareed.lazeezo.Remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by fareed on 2/11/2018.
 */

public interface IGoogleServices {
    @GET
    Call<String> getAddressName(@Url String url);


}
