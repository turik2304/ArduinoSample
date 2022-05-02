package com.example.arduinotest

import com.example.arduinotest.data.Client
import com.example.arduinotest.data.Repository

class Presenter(
    private val repository: Repository = Repository()
) {

    fun setAngle(value: Int) {
        repository.setAngle(value)
    }

    fun updateUrl(url: String) {
        Client.updateUrl(url)
    }
}