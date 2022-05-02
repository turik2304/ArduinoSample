package com.example.arduinotest.data

import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import java.util.concurrent.TimeUnit


object Client {

    private const val defaultUrl: String = "http://192.168.0.1:80"

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .addNetworkInterceptor(HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        })
        .build()

    private val retrofitClient: Retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(defaultUrl)
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .build()

    val api: ArduinoApi = retrofitClient.create(ArduinoApi::class.java)

    fun updateUrl(url: String) {
        val field = Retrofit::class.java.getDeclaredField("baseUrl")
        field.isAccessible = true
        val newHttpUrl = url.toHttpUrlOrNull()
        field.set(retrofitClient, newHttpUrl)
    }

}