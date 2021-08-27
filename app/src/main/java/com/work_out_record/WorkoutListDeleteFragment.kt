package com.work_out_record

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
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

class WorkoutListDeleteFragment: Fragment() {

    private lateinit var recordRecyclerView: RecyclerView
    private var adapter: RecordAdapter? = RecordAdapter(emptyList())
    private var deleteID: MutableList<Record> = emptyList<Record>().toMutableList()
    private lateinit var deleteAll: List<Record>
    private val recordViewModel: RecordViewModel by lazy {
        ViewModelProvider(this).get(RecordViewModel::class.java)
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
                deleteAll = records
                records?.let {
                    updateUI(records)
                }
            }
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_delete_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_really -> {
                val alertDialogBuilder = AlertDialog.Builder(this.context)
                alertDialogBuilder.setTitle(R.string.delete_records)
                    .setMessage(R.string.delete_record_really)
                    .setPositiveButton(R.string.yes_button,
                    DialogInterface.OnClickListener { dialog, which ->
                        deleteID.forEach {
                            recordViewModel.deleteRecord(it)
                            fragmentManager?.popBackStack()
                        }
                    })
                    .setNegativeButton(R.string.no_button,
                    DialogInterface.OnClickListener { dialog, which ->
                        //유저가 취소함
                    })
                alertDialogBuilder.show()
                return true
            }

            R.id.delete_all -> {
                val alertDialogBuilder = AlertDialog.Builder(this.context)
                alertDialogBuilder.setTitle(R.string.delete_all)
                    .setMessage(R.string.delete_record_really)
                    .setPositiveButton(R.string.yes_button,
                        DialogInterface.OnClickListener { dialog, which ->
                            deleteAll.forEach {
                                recordViewModel.deleteRecord(it)
                                fragmentManager?.popBackStack()
                            }
                        })
                    .setNegativeButton(R.string.no_button,
                        DialogInterface.OnClickListener { dialog, which ->
                            //유저가 취소함
                        })
                alertDialogBuilder.show()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun updateUI(records: List<Record>) {
        adapter = RecordAdapter(records)
        recordRecyclerView.adapter = adapter
    }

    private inner class RecordHolder(view: View) : RecyclerView.ViewHolder(view) {

        private lateinit var record: Record

        private val dateTextView: TextView = itemView.findViewById(R.id.record_date)
        private val partTextView: TextView = itemView.findViewById(R.id.record_part)
        private val routineTextView: TextView = itemView.findViewById(R.id.record_routine)
        private val deleteCheckBox: CheckBox = itemView.findViewById(R.id.delete_checkBox)


        fun bind(record: Record) {
            this.record = record
            dateTextView.text = DateFormat.format("yyyy-MM-dd EEE HH:mm", record.date).toString()
            partTextView.text = record.part
            routineTextView.text = record.routine
            deleteCheckBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    deleteID.add(record)
                } else {
                    deleteID.remove(record)
                }
            }
        }

    }

    private inner class RecordAdapter(var records: List<Record>) : RecyclerView.Adapter<RecordHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordHolder {
            val view = layoutInflater.inflate(R.layout.list_record_delete, parent, false)
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
        fun newInstance() = WorkoutListDeleteFragment()
    }
}