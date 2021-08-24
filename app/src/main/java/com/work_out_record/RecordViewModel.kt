package com.work_out_record

import androidx.lifecycle.ViewModel

class RecordViewModel : ViewModel() {

    private val recordRepository = RecordRepository.get()
    val recordLiveData = recordRepository.getRecords()

}