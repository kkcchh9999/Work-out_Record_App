package com.work_out_record.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.work_out_record.Record
import java.util.*

@Dao    //Database Access Object
interface RecordDAO {

    @Query("SELECT * FROM record ORDER BY date DESC")   //데이터베이스에 모든 기록을 가져오는 쿼리
    fun getRecords(): LiveData<List<Record>>

    @Query ("SELECT * FROM record WHERE id=(:id)")      //id를 통하여 특정 데이터 가져오는 쿼리
    fun getRecord(id: UUID): LiveData<Record?>

    @Query ("SELECT * FROM record WHERE part LIKE :part")   //키워드를 통해 검색하여 데이터를 가져오는 쿼리
    fun searchRecord(part: String): LiveData<List<Record>>

    @Update
    fun updateRecord(record: Record)        //데이터를 업데이트 하는 쿼리

    @Insert
    fun addRecord(record: Record)           //새로 데이터를 추가하는 쿼리

    @Delete
    fun deleteRecord(record: Record)        //데이터를 삭제하는 쿼리
}