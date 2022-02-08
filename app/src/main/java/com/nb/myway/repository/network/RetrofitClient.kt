package com.nb.myway.repository.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient {

    private const val BASE_URL = "https://maps.googleapis.com/"
    var gson: Gson = GsonBuilder()
        .setLenient()
        .create()

    fun buildApi() : ApiInterface {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiInterface::class.java)
    }


}