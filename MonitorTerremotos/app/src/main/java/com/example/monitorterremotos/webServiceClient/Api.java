package com.example.monitorterremotos.webServiceClient;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Api {

    private static HttpLoggingInterceptor loggingInterceptor;
    private static OkHttpClient.Builder httpClientBuilder;
    private static Retrofit retrofit = null;

    public static WebServiceClient getClient() {
        loggingInterceptor = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClientBuilder = new OkHttpClient.Builder().addInterceptor(loggingInterceptor);
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://earthquake.usgs.gov/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClientBuilder.build())
                    .build();
        }
        WebServiceClient api = retrofit.create(WebServiceClient.class);
        return api;
    }
}
