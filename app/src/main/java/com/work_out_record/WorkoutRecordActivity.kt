package com.work_out_record

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
    }

    override fun onRecordSelected(recordId: UUID) {
        val fragment = WorkoutDetailFragment.newInstance(recordId)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}