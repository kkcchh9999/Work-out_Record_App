package com.work_out_record

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity //데이터베이스로 변환
data class Record(                                  //해당 앱에서 운동 일지를 하나의 Record 데이터 클래스에 저장
    @PrimaryKey val id: UUID = UUID.randomUUID(),   //고유 ID
    var date: Date? = null,                         //일지 작성 시간
    var part: String = "",                          //운동 부위
    var routine: String = "",                       //운동 루틴
    var repeat: String = ""                         //실행 횟수
)
