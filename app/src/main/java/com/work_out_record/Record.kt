package com.work_out_record

import java.text.DateFormat
import java.util.*

data class Record (
    var date: Date = Date(),
    var part: String = "",
    var routine: String = "",
    var repeat: String = ""
)
