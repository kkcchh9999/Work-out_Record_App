package com.work_out_record

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import androidx.room.RoomDatabase
import com.work_out_record.database.RecordDatabase
import java.util.*

private const val DATABASE_NAME = "RecordDatabase"

class RecordRepository private constructor(context: Context){

    private val database : RecordDatabase = Room.databaseBuilder(
        context.applicationContext,
        RecordDatabase::class.java,
        DATABASE_NAME
    ).build()

    private val recordDAO = database.recordDAO()

    fun getRecords(): LiveData<List<Record>> = recordDAO.getRecords()

    fun getRecord(id: UUID): LiveData<Record?> = recordDAO.getRecord(id)!!

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