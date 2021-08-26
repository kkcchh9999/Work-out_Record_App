package com.work_out_record

import android.app.Application
import androidx.lifecycle.*
import java.util.*

class RecordDetailViewModel(private val app: Application) : AndroidViewModel(app) {

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