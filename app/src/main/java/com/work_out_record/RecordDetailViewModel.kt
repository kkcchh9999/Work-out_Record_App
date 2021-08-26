package com.work_out_record

import android.app.Application
import androidx.lifecycle.*
import java.util.*

class RecordDetailViewModel(private val app: Application) : AndroidViewModel(app) {

    private val recordRepository = RecordRepository.get()
    private val recordIdLiveData = MutableLiveData<UUID>()

    var routine: Array<String> = arrayOf("루틴1", "루틴2", "루틴3", "루틴4", "루틴5")

    var recordLiveData: LiveData<Record?> =
        Transformations.switchMap(recordIdLiveData) { recordId ->
            recordRepository.getRecord(recordId)
        }

    fun saveRoutine(routineName: String, routine: String) {
        QueryPreferences.setStoredQuery(app, routineName, routine)
    }

    fun loadRoutine(routineName: String): String{
        return QueryPreferences.getStoredQuery(app, routineName)
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