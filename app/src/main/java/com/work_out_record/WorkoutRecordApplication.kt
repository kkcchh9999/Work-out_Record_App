package com.work_out_record

import android.app.Application

class WorkoutRecordApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        RecordRepository.init(this)
    }
}