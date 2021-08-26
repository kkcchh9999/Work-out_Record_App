package com.work_out_record

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.util.*

class RecordDetailViewModel() : ViewModel() {

    private val recordRepository = RecordRepository.get()
    private val recordIdLiveData = MutableLiveData<UUID>()

    var recordLiveData: LiveData<Record?> =
        Transformations.switchMap(recordIdLiveData) { recordId ->
            recordRepository.getRecord(recordId)
        }

    fun loadRecord(recordId: UUID) {
        recordIdLiveData.value = recordId
    }

    fun saveRecord(record: Record) {
        recordRepository.updateRecord(record)
    }

    fun deleteRecord(record: Record) {
        recordRepository.deleteRecord(record)
    }
}