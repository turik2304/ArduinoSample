package com.example.arduinotest.data

import io.reactivex.rxjava3.core.Completable
import retrofit2.http.GET
import retrofit2.http.Query

interface ArduinoApi {

    @GET("/")
    fun setAngle(
        @Query("sr1") value: Int
    ): Completable

}