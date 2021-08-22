package com.work_out_record

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class WorkoutRecordFragment: Fragment() {

    private lateinit var recordRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_workout_record, container, false)

        recordRecyclerView = view.findViewById(R.id.record_recyclerview)
        recordRecyclerView.layoutManager = GridLayoutManager(context, 2)

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    companion object {
        fun newInstance() = WorkoutRecordFragment()
    }
}