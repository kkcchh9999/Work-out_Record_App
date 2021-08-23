package com.work_out_record

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment

class WorkoutDetailFragment : Fragment() {

    private lateinit var dateTextView: TextView
    private lateinit var partEditText: EditText
    private lateinit var routineEditText: EditText
    private lateinit var repeatEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_workout_detail, container, false)

        dateTextView = view.findViewById(R.id.date_textView) as TextView
        partEditText = view.findViewById(R.id.part_editText) as EditText
        routineEditText = view.findViewById(R.id.routine_editText) as EditText
        repeatEditText = view.findViewById(R.id.repeat_editText) as EditText

        return view
    }

    companion object {
        fun newInstance() = WorkoutDetailFragment()
    }
}