package com.work_out_record

import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class WorkoutRecordActivity : AppCompatActivity(), WorkoutListFragment.Callbacks {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_record)

        val isFragmentContainerEmpty = savedInstanceState == null
        if (isFragmentContainerEmpty) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, WorkoutListFragment.newInstance())
                .commit()
        }
        val actionBar: ActionBar? = supportActionBar
    }

    override fun onRecordSelected(recordId: UUID) {
        val fragment = WorkoutDetailFragment.newInstance(recordId)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDeleteSelected() {
        val fragment = WorkoutListDeleteFragment.newInstance()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

//    override fun onCalendarSelected(dateList: MutableList<Date>) {
//        val fragment = WorkoutCalendarFragment.newInstance()
//        supportFragmentManager
//            .beginTransaction()
//            .replace(R.id.fragment_container, fragment)
//            .addToBackStack(null)
//            .commit()
//    }

    fun setActionBarTitle(title: Int) {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setTitle(title)
        actionBar?.elevation = 0F
    }
}