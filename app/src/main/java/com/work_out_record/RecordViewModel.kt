package com.work_out_record

import androidx.lifecycle.ViewModel

class RecordViewModel : ViewModel() {

    private val recordRepository = RecordRepository.get()
    var recordLiveData = recordRepository.getRecords()

    fun searchRecord(part: String) {
        recordLiveData = recordRepository.searchRecord(part)
    }

    fun rollbackRecord() {
        recordLiveData = recordRepository.getRecords()
    }

    fun addRecord(record: Record) {
        recordRepository.addRecord(record)
    }

    fun deleteRecord(record: Record) {
        recordRepository.deleteRecord(record)
    }
}