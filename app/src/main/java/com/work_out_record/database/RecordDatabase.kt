package com.work_out_record.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.work_out_record.Record

@Database(entities = [Record::class], version = 1)  //데이터베이스 생성, Record 클래스를 기반으로, 버전은 첫 번째 버전
@TypeConverters(RecordTypeConverters::class)        //SQLite 에 없는 형식 (ex: date, UUID) 를 변환하여 제공하기 위한 컨버터
abstract class RecordDatabase : RoomDatabase() {
    abstract fun recordDAO(): RecordDAO             //DAO 를 선언, DAO 를 통하여 데이터베이스에 접근
}