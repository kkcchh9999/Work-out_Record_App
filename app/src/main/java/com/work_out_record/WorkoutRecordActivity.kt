package com.work_out_record

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class WorkoutRecordActivity : AppCompatActivity() {
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
    }
}