package com.work_out_record.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.work_out_record.Record

@Database(entities = [Record::class], version = 1)
@TypeConverters(RecordTypeConverters::class)
abstract class RecordDatabase : RoomDatabase() {

    abstract fun recordDAO(): RecordDAO
}