package com.work_out_record

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Record (
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    var date: Date = Date(),
    var part: String = "",
    var routine: String = "",
    var repeat: String = ""
)
