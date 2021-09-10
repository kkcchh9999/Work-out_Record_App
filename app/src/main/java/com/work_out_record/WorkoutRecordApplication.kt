package com.work_out_record

import android.app.Application

class WorkoutRecordApplication : Application() {    //앱의 서브클래스

    override fun onCreate() {
        super.onCreate()
        //데이터베이스와 뷰모델들을 선언
        RecordRepository.init(this)
        RecordAndRoutineViewModel.init(this)
        RecordsViewModel.init()
    }
}