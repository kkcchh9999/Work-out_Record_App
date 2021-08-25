package com.work_out_record

import androidx.lifecycle.ViewModel

class RecordViewModel : ViewModel() {

    private val recordRepository = RecordRepository.get()
    val recordLiveData = recordRepository.getRecords()

    fun addRecord(record: Record) {
        recordRepository.addRecord(record)
    }

    fun deleteRecord(record: Record) {
        recordRepository.deleteRecord(record)
    }
}