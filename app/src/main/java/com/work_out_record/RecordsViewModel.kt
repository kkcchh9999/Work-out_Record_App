package com.work_out_record

import androidx.lifecycle.ViewModel

class RecordsViewModel : ViewModel() {
    //Record 리스트를 관리하는 ViewModel

    private val recordRepository = RecordRepository.get()   //DB 선언되어있는 repository get 하기
    var recordLiveData = recordRepository.getRecords()  //DB 에서 Records 가져오기

    fun searchRecord(part: String) {    //Record 검색 함수
        recordLiveData = recordRepository.searchRecord(part)
    }

    fun rollbackRecord() {              //검색 후에 원래대로 되돌리는 함수
        recordLiveData = recordRepository.getRecords()
    }

    fun addRecord(record: Record) {     //Record 추가함수
        recordRepository.addRecord(record)
    }

    fun deleteRecord(record: Record) {  //Record 삭제 함수
        recordRepository.deleteRecord(record)
    }
}