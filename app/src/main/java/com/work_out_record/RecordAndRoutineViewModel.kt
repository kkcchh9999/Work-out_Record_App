package com.work_out_record

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import java.util.*

//Record 하나의 세부내용과 루틴에 사용되는 ViewModel
class RecordAndRoutineViewModel(private val app: Application) : AndroidViewModel(app) {

    private val recordRepository = RecordRepository.get()       //DB가 선언되어있는 Repository
    private val recordIdLiveData = MutableLiveData<UUID>()      //UUID

    var recordLiveData: LiveData<Record?> =                     //switchMap 에 첫 인자 = UUID
        Transformations.switchMap(recordIdLiveData) { recordId ->   //두 번째 인자 = 람다식 -> 데이터베이스에서 Record 를 ID로 가져오는 식
            recordRepository.getRecord(recordId)                //switchMap 에 의해 recordLiveData 가 UUID 를 갖는 Record 객체로 준비된다.
        }

    var savedRoutineName: Array<String> =
        arrayOf("빈 루틴 1", "빈 루틴 2", "빈 루틴 3", "빈 루틴 4", "빈 루틴 5")   //저장된 루틴의 이름
    init {
        for (i in 0..4) {
            if (QueryPreferences.getStoredQuery(app, i.toString()) != "") {     //루틴이 저장되어있으면 공유 프리퍼런스에서 get 하기
                savedRoutineName[i] = QueryPreferences.getStoredQuery(app, i.toString())    //null 이 아니라 "" 로 설정해야 오류가 안나더라.
            }
        }
    }

    fun saveRoutineName(saveLocation: Int, routineName: String) {       //공유 프리퍼런스에 루틴 이름을 저장하는 함수
        QueryPreferences.setStoredQuery(app, saveLocation.toString(), routineName)
        savedRoutineName[saveLocation] = QueryPreferences.getStoredQuery(app, saveLocation.toString())
    }

    fun saveRoutine(routineCode: String, routine: String) {             //루틴을 저장하는 함수
        QueryPreferences.setStoredQuery(app, routineCode, routine)
    }

    fun loadRoutine(routineCode: String): String{                       //루틴을 불러오는 함수
        return QueryPreferences.getStoredQuery(app, routineCode)
    }

    fun loadRecord(recordId: UUID) {                //Record 를 불러오는 함수
        recordIdLiveData.value = recordId
    }

    fun saveRecord(record: Record) {                //Record 를 저장하는 함수
        recordRepository.updateRecord(record)
    }

    fun deleteRecord(record: Record) {              //Record 를 삭제하는 함수
        recordRepository.deleteRecord(record)
    }

    companion object {                              //한 앱에서 하나의 RecordAndRoutineViewModel 만 존재하기 위한 장치

        private var INSTANCE: RecordAndRoutineViewModel? = null

        fun init(app: Application) {                //최초 선언시 init 으로 생성하고
            if (INSTANCE == null) {
                INSTANCE = RecordAndRoutineViewModel(app)
            }
        }

        fun get(): RecordAndRoutineViewModel {               //이후 get 으로 보존된 내용 가져가기
            return INSTANCE ?:
            throw IllegalAccessException("Must be initialized")
        }
    }
}