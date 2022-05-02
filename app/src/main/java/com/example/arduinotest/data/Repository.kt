package com.example.arduinotest.data

import android.util.Log
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class Repository(
    private val api: ArduinoApi = Client.api
) {

    private val subject: PublishSubject<Int> = PublishSubject.create()

    init {
        subject
            .distinctUntilChanged()
            .debounce(400, TimeUnit.MILLISECONDS, Schedulers.io())
            .switchMapCompletable(api::setAngle)
            .subscribe({
                Log.d("test", "Repository, setAngle(): OK")
            }, {
                Log.d("test", "Repository, setAngle(): ERROR $it")
            })
    }

    fun setAngle(value: Int) {
        subject.onNext(value)
    }

}