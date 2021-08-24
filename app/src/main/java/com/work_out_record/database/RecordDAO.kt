package com.work_out_record.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.work_out_record.Record
import java.util.*

@Dao
interface RecordDAO {

    @Query("SELECT * FROM record")
    fun getRecords(): LiveData<List<Record>>

    @Query ("SELECT * FROM record WHERE id=(:id)")
    fun getRecord(id: UUID): LiveData<Record?>
}