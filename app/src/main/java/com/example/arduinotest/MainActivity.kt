package com.example.arduinotest

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.io.Serializable
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var btn: Button

    private lateinit var seekBar: SeekBar

    private lateinit var editText: EditText

    private lateinit var textView: TextView

    private lateinit var okHttpClient: OkHttpClient

    private lateinit var retrofitClient: Retrofit

    private lateinit var prefs: SharedPreferences

    private lateinit var api: ArduinoApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn = findViewById(R.id.applyButton)
        seekBar = findViewById<SeekBar?>(R.id.seekBar).apply {
            max = 180
        }
        editText = findViewById(R.id.editText)
        textView = findViewById(R.id.infoText)

        prefs = getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE)

        val def = "http://192.168.0.1:80"
        val baseURL = prefs.getString(APP_PREFS_URL_KEY, def) ?: def
        updateBaseUrl(baseURL)
        editText.text = SpannableStringBuilder(baseURL)

        btn.setOnClickListener {
            val url = "http://" + editText.text.toString()
            updateBaseUrl(url)
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                setAngle(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun updateBaseUrl(url: String) {
        okHttpClient = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .addNetworkInterceptor(HttpLoggingInterceptor().apply {
                setLevel(HttpLoggingInterceptor.Level.BODY)
            })
            .build()
        retrofitClient = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(url)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
        api = retrofitClient.create(ArduinoApi::class.java)
        prefs.edit().putString(APP_PREFS_URL_KEY, url).apply()
    }

    private fun setAngle(value: Int) {
        okHttpClient.connectionPool.evictAll()
        api.setAngle(value)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d("test", "OK")
            }, {
                Log.d("test", it.message ?: it.localizedMessage)
            })

        textView.text = value.toString()
    }

    companion object {
        private const val APP_PREFS = "MY_PREF"
        private const val APP_PREFS_URL_KEY = "APP_PREFS_URL_KEY"
    }
}

interface ArduinoApi {

    @GET("/")
    fun setAngle(
        @Query("sr1") value: Int
    ): Completable

}

data class AngleRequest(
    val value: Int
) : Serializable

