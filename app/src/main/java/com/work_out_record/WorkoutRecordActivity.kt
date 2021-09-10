package com.work_out_record

import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import kotlin.collections.ArrayList

//앱의 메인 액티비티
class WorkoutRecordActivity : AppCompatActivity(), WorkoutListFragment.Callbacks {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_record)
        //최초 프래그먼트 설정
        val isFragmentContainerEmpty = savedInstanceState == null
        if (isFragmentContainerEmpty) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, WorkoutListFragment.newInstance())
                .commit()
        }
    }

    //레코드가 선택되었을 때 화면전환
    override fun onRecordSelected(recordId: UUID) {
        val fragment = WorkoutDetailFragment.newInstance(recordId)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    //삭제가 선택되었을 때 화면전환
    override fun onDeleteSelected() {
        val fragment = WorkoutListDeleteFragment.newInstance()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    //캘린더가 선택되었을 때 화면전환
    override fun onCalendarSelected() {
        val fragment = WorkoutCalendarFragment.newInstance()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    //액션바 이름 설정
    fun setActionBarTitle(title: Int) {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setTitle(title)
        actionBar?.elevation = 0F
    }
}