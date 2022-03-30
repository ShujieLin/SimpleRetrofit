package com.lsj.simple_retrofit.api;

import androidx.annotation.Nullable;

import com.lsj.simple_retrofit.annotation.GET;
import com.lsj.simple_retrofit.annotation.Query;

import okhttp3.Call;

/**
 * @description:
 * @date: 2022/3/30
 * @author: linshujie
 */
public interface WeatherAPI {
    @GET("/v3/weather/weatherInfo")
    Call getWeather(@Query("city") @Nullable String city, @Query("key") String key);
}
