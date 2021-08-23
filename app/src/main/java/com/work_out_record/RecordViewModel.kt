package com.work_out_record

import androidx.lifecycle.ViewModel

class RecordViewModel : ViewModel() {

    val records = mutableListOf<Record>()
    init {
        for (i in 1 until 20) {
            val record = Record()
            record.part = "가슴"
            record.routine = "벤치프레스 60kg, 48"
            record.repeat = "12, 12, 12, 12"
            records.add(record)
        }
    }
}