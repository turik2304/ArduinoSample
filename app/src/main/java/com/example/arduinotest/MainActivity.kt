package com.example.arduinotest

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var btn: Button

    private lateinit var seekBar: SeekBar

    private lateinit var editText: EditText

    private lateinit var textView: TextView

    private lateinit var prefs: SharedPreferences

    private val presenter: Presenter = Presenter()

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
        presenter.updateUrl(url)
        prefs.edit().putString(APP_PREFS_URL_KEY, url).apply()
    }

    private fun setAngle(value: Int) {
        presenter.setAngle(value)
        textView.text = value.toString()
    }

    companion object {
        private const val APP_PREFS = "MY_PREF"
        private const val APP_PREFS_URL_KEY = "APP_PREFS_URL_KEY"
    }
}