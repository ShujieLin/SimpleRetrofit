package com.lsj.simpleretrofit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.lsj.simple_retrofit.SimpleRetrofit;
import com.lsj.simple_retrofit.api.WeatherAPI;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String URL = "https://restapi.amap.com";
    private WeatherAPI weatherApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SimpleRetrofit simpleRetrofit = new SimpleRetrofit.Builder().baseUrl(URL).build();
        weatherApi = simpleRetrofit.create(WeatherAPI.class);
    }

    public void getWheather(View view) {
        okhttp3.Call call = weatherApi.getWeather("110101", "ae6c53e2186f33bbf240a12d80672d1b");
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

            }
        });
    }
}