package com.work_out_record

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import androidx.room.RoomDatabase
import com.work_out_record.database.RecordDatabase
import java.util.*
import java.util.concurrent.Executors

private const val DATABASE_NAME = "RecordDatabase"

class RecordRepository private constructor(context: Context){

    private val database : RecordDatabase = Room.databaseBuilder(
        context.applicationContext,
        RecordDatabase::class.java,
        DATABASE_NAME
    ).build()

    private val recordDAO = database.recordDAO()
    private val executor = Executors.newSingleThreadExecutor()

    fun getRecords(): LiveData<List<Record>> = recordDAO.getRecords()

    fun getRecord(id: UUID): LiveData<Record?> = recordDAO.getRecord(id)!!

    fun searchRecord(part: String): LiveData<List<Record>> = recordDAO.searchRecord("%$part%")

    fun updateRecord(record: Record) {
        executor.execute {
            recordDAO.updateRecord(record)
        }
    }

    fun addRecord(record: Record) {
        executor.execute {
            recordDAO.addRecord(record)
        }
    }

    fun deleteRecord(record: Record) {
        executor.execute {
            recordDAO.deleteRecord(record)
        }
    }

    companion object {
        private var INSTANCE: RecordRepository? = null

        fun init(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = RecordRepository(context)
            }
        }

        fun get(): RecordRepository {
            return INSTANCE ?:
            throw IllegalAccessException("Must be initialized")
        }
    }
}