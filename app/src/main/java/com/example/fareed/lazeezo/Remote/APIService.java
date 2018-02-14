package com.example.fareed.lazeezo.Remote;

import com.example.fareed.lazeezo.Model.MyResponse;
import com.example.fareed.lazeezo.Model.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by fareed on 2/5/2018.
 */

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAl6dKgoI:APA91bEIEXRnq-r49_F2_EktS13WVaxUXA0N0EBrdCqfCUsqdrTzwifMCejEfy-ndaVBlVIdnYUuBZFgDCHMBUkvDWuaufFKhMRv6HrIaefOxiX--G0Vsx1YAao7Na7RYlkEVyXxtH4Z"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
