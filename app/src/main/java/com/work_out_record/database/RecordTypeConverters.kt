package com.work_out_record.database

import androidx.room.TypeConverter
import java.util.*

class RecordTypeConverters {    //SQLite 에 없는 형식 (ex: date, UUID) 를 변환하여 제공하기 위한 컨버터
    @TypeConverter
    fun fromDate(date: Date?): Long? {  //Date 를 Long 으로
        return date?.time
    }

    @TypeConverter
    fun toDate(long: Long?): Date? {    //Long 형식을 다시 Date 로
        return Date(long!!)
    }

    @TypeConverter
    fun fromUUID(uuid: UUID?): String? {    //UUID 형식을 String 으로
        return uuid.toString()
    }

    @TypeConverter
    fun toUUID(uuid: String?): UUID? {  //String 형식을 다시 UUID 로
        return UUID.fromString(uuid)
    }
}