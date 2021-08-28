package com.work_out_record

import android.app.Application

class WorkoutRecordApplication : Application() {    //앱의 서브클래스

    override fun onCreate() {
        super.onCreate()
        RecordRepository.init(this)
        RecordAndRoutineViewModel.init(this)
    }
}