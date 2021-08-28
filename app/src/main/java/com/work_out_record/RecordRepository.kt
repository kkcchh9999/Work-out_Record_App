package com.work_out_record

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import androidx.room.RoomDatabase
import com.work_out_record.database.RecordDatabase
import java.util.*
import java.util.concurrent.Executors

private const val DATABASE_NAME = "RecordDatabase"

class RecordRepository private constructor(context: Context){   //데이터베이스를 선언하고 활용하는 저장소

    private val database : RecordDatabase = Room.databaseBuilder(   //데이터베이스 선언
        context.applicationContext,
        RecordDatabase::class.java,
        DATABASE_NAME
    ).build()

    private val recordDAO = database.recordDAO()                    //DAO 선언
    private val executor = Executors.newSingleThreadExecutor()      //백그라운드 스레드에서 작동하기 위한 executor

    fun getRecords(): LiveData<List<Record>> = recordDAO.getRecords()       //모든 데이터 가져오기

    fun getRecord(id: UUID): LiveData<Record?> = recordDAO.getRecord(id)!!  //특정 데이터 가져오기

    fun searchRecord(part: String): LiveData<List<Record>>
        = recordDAO.searchRecord("%$part%")                             //데이터 검색해서 가져오기

    fun updateRecord(record: Record) {              //데이터 업데이트하기
        executor.execute {
            recordDAO.updateRecord(record)
        }
    }

    fun addRecord(record: Record) {                 //데이터 추가하기
        executor.execute {
            recordDAO.addRecord(record)
        }
    }

    fun deleteRecord(record: Record) {              //데이터 삭제하기
        executor.execute {
            recordDAO.deleteRecord(record)
        }
    }

    companion object {                              //한 앱에서 하나의 RecordRepository 만 존재하기 위한 장치

        private var INSTANCE: RecordRepository? = null

        fun init(context: Context) {                //최초 선언시 init 으로 생성하고
            if (INSTANCE == null) {
                INSTANCE = RecordRepository(context)
            }
        }

        fun get(): RecordRepository {               //이후 get 으로 보존된 내용 가져가기
            return INSTANCE ?:
            throw IllegalAccessException("Must be initialized")
        }
    }
}