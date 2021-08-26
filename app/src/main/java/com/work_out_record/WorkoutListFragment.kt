package com.work_out_record

import android.content.Context
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.*
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class WorkoutListFragment: Fragment() {

    /**
     *호스팅 액티비티에서 구현할 인터페이스 Callbacks
     * 이를 구현하여 프래그먼트간 화면전환
     */
    interface Callbacks {
        fun onRecordSelected(recordId: UUID)
    }
    private var callbacks: Callbacks? = null

    private lateinit var recordRecyclerView: RecyclerView
    private var adapter: RecordAdapter? = RecordAdapter(emptyList())

    private val recordViewModel: RecordViewModel by lazy {
        ViewModelProvider(this).get(RecordViewModel::class.java)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_workout_list, container, false)

        recordRecyclerView = view.findViewById(R.id.record_recyclerview)
        recordRecyclerView.layoutManager = GridLayoutManager(context, 2)
        recordRecyclerView.adapter = adapter


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recordViewModel.recordLiveData.observe(
            viewLifecycleOwner,
            Observer { records ->
                records?.let {
                    updateUI(records)
                }
            }
        )
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_workout_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.add_record -> {
                val record = Record()
                record.date = Date(System.currentTimeMillis())
                recordViewModel.addRecord(record)
                callbacks?.onRecordSelected(record.id)
                true
            }
            R.id.delete_records -> {

                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun updateUI(records: List<Record>) {
        adapter = RecordAdapter(records)
        recordRecyclerView.adapter = adapter
    }

    private inner class RecordHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        private lateinit var record: Record

        private val dateTextView: TextView = itemView.findViewById(R.id.record_date)
        private val partTextView: TextView = itemView.findViewById(R.id.record_part)

        init {
            itemView.setOnClickListener(this)
        }
        fun bind(record: Record) {
            this.record = record
            dateTextView.text = DateFormat.format("yyyy-MM-dd EEE HH:mm", record.date).toString()
            partTextView.text = record.part
        }

        override fun onClick(v: View?) {
            callbacks?.onRecordSelected(record.id)
        }
    }

    private inner class RecordAdapter(var records: List<Record>) : RecyclerView.Adapter<RecordHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordHolder {
            val view = layoutInflater.inflate(R.layout.list_record, parent, false)
            return RecordHolder(view)
        }

        override fun onBindViewHolder(holder: RecordHolder, position: Int) {
            val record = records[position]
            holder.bind(record)
        }

        override fun getItemCount(): Int {
            return records.size
        }
    }

    companion object {
        fun newInstance() = WorkoutListFragment()
    }
}