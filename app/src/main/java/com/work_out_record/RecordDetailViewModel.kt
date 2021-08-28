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

    var savedRoutineName: Array<String> = arrayOf("빈 루틴 1", "빈 루틴 2", "빈 루틴 3", "빈 루틴 4", "빈 루틴 5")
    init {
        for (i in 0..4) {
            if (QueryPreferences.getStoredQuery(app, i.toString()) != "") {
                savedRoutineName[i] = QueryPreferences.getStoredQuery(app, i.toString())
            }
        }
    }

    fun saveRoutineName(saveLocation: Int, routineName: String) {
        QueryPreferences.setStoredQuery(app, saveLocation.toString(), routineName)
        savedRoutineName[saveLocation] = QueryPreferences.getStoredQuery(app, saveLocation.toString())
    }

    fun saveRoutine(routineCode: String, routine: String) {
        QueryPreferences.setStoredQuery(app, routineCode, routine)
    }

    fun loadRoutine(routineCode: String): String{
        return QueryPreferences.getStoredQuery(app, routineCode)
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