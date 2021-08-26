package com.work_out_record.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.work_out_record.Record
import java.util.*

@Dao
interface RecordDAO {

    @Query("SELECT * FROM record ORDER BY date DESC")
    fun getRecords(): LiveData<List<Record>>

    @Query ("SELECT * FROM record WHERE id=(:id)")
    fun getRecord(id: UUID): LiveData<Record?>

    @Query ("SELECT * FROM record WHERE part LIKE :part")
    fun searchRecord(part: String): LiveData<List<Record>>

    @Update
    fun updateRecord(record: Record)

    @Insert
    fun addRecord(record: Record)

    @Delete
    fun deleteRecord(record: Record)
}