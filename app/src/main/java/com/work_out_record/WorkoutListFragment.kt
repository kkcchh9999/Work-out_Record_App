package com.work_out_record

import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class WorkoutListFragment: Fragment() {

    private lateinit var recordRecyclerView: RecyclerView
    private var adapter: RecordAdapter? = null

    private val recordViewModel: RecordViewModel by lazy {
        ViewModelProvider(this).get(RecordViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_workout_list, container, false)

        recordRecyclerView = view.findViewById(R.id.record_recyclerview)
        recordRecyclerView.layoutManager = GridLayoutManager(context, 2)

        updateUI()

        return view
    }

    private fun updateUI() {
        val records = recordViewModel.records
        adapter = RecordAdapter(records)
        recordRecyclerView.adapter = adapter
    }

    private inner class RecordHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dateTextView: TextView = itemView.findViewById(R.id.record_date)
        val partTextView: TextView = itemView.findViewById(R.id.record_part)
    }

    private inner class RecordAdapter(var records: List<Record>) : RecyclerView.Adapter<RecordHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordHolder {
            val view = layoutInflater.inflate(R.layout.list_record, parent, false)
            return RecordHolder(view)
        }

        override fun onBindViewHolder(holder: RecordHolder, position: Int) {
            val record = records[position]
            holder.apply {
                dateTextView.text = DateFormat.format("yyyy.EEEE.MMM.dd", record.date)
                partTextView.text = record.part
            }
        }

        override fun getItemCount(): Int {
            return records.size
        }
    }

    companion object {
        fun newInstance() = WorkoutListFragment()
    }
}