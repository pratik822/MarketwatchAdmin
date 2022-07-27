package com.pratik.marketwatchadmin.network

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient {
    fun getRetrofitInstance(): RetrofitService {
        val BASE_URL = "http://new247.in/marketwatch-server/stockmarket/"
        val gson = GsonBuilder()
            .setLenient()
            .create()
        val retrofit =
            Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(BASE_URL)
                .build();
        return retrofit.create(RetrofitService::class.java);
    }

}