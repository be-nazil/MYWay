package com.nb.myway.repository.network

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {

    @GET("maps/api/directions/json")
    suspend fun getDirection(
            @Query("origin") origin: String,
            @Query("destination") destination: String,
            @Query("sensor") sensor: String,
            @Query("mode") mode: String,
            @Query("key") key: String
    ): ResponseBody


}