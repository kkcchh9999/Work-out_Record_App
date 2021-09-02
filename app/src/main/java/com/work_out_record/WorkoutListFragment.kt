package com.work_out_record

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.format.DateFormat
import android.view.*
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class WorkoutListFragment: Fragment() {

    /**
     *호스팅 액티비티에서 구현할 인터페이스 Callbacks
     * 이를 구현하여 프래그먼트간 화면전환
     */
    interface Callbacks {
        fun onRecordSelected(recordId: UUID)
        fun onDeleteSelected()
        fun onCalendarSelected()
    }
    private var callbacks: Callbacks? = null

    //리사이클러 뷰 선언
    private lateinit var recordRecyclerView: RecyclerView
    private var adapter: RecordAdapter? = RecordAdapter(emptyList())
    //뷰모델 선언
    private val recordAndRoutineViewModel = RecordAndRoutineViewModel.get()
    private val recordViewModel: RecordsViewModel by lazy {
        ViewModelProvider(this).get(RecordsViewModel::class.java)
    }

    //레이아웃 아이템 선언
    private lateinit var noRecordSearchTextView: TextView
    private lateinit var noRecordTextView: TextView
    private lateinit var noRecordButton: ImageButton
    private lateinit var savedRoutineName: Array<String>
    private lateinit var floatingButton: FloatingActionButton
    private val routineCode: Array<String> = arrayOf("루틴0", "루틴1", "루틴2", "루틴3", "루틴4")

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
        noRecordTextView = view.findViewById(R.id.no_record_text)
        noRecordButton = view.findViewById(R.id.no_record_button)
        noRecordSearchTextView = view.findViewById(R.id.no_record_search)
        floatingButton = view.findViewById(R.id.floating_button)
        floatingButton.visibility = View.VISIBLE

        floatingButton.setOnClickListener {
            var selectedItem: Int = -1
            val builder = AlertDialog.Builder(this.context, R.style.AlertDialogTheme)

            builder.setTitle(R.string.select_routine_load)
                .setSingleChoiceItems(savedRoutineName, -1
                ) { _, which ->
                    selectedItem = which
                }
                .setPositiveButton(R.string.add_record
                ) { _, _ ->
                    if (selectedItem == -1) {
                        Toast.makeText(
                            this.context,
                            R.string.select_routine_load,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    } else {
                        val stringArr =
                            recordAndRoutineViewModel.loadRoutine(routineCode[selectedItem])
                                .split("///")
                        if (stringArr.size == 1) {
                            Toast.makeText(
                                this.context,
                                R.string.empty_routine,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        } else {
                            val record = Record()
                            record.date = Date(System.currentTimeMillis())
                            record.routine = stringArr[0]
                            record.part = stringArr[1]
                            recordViewModel.addRecord(record)
                            callbacks?.onRecordSelected(record.id)
                        }
                    }
                }
                .setNegativeButton(R.string.cancel
                ) { _, _ ->
                    //유저가 취소함
                }
                .setNeutralButton(R.string.new_record
                ) { _, _ ->
                    val record = Record()
                    record.date = Date(System.currentTimeMillis())
                    recordViewModel.addRecord(record)
                    callbacks?.onRecordSelected(record.id)
                }
            val alertDialog = builder.create()
            alertDialog.show()
            alertDialog.window?.setBackgroundDrawableResource(R.drawable.alert_dialog_background)
        }

        savedRoutineName = recordAndRoutineViewModel.savedRoutineName

        return view
    }

    override fun onResume() {
        super.onResume()
        activity
        if (activity != null) {
            (activity as WorkoutRecordActivity).setActionBarTitle(R.string.wor)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recordViewModel.recordLiveData.observe(
            viewLifecycleOwner,
            { records ->
                records?.let {
                    updateUI(records, 0)
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

        val searchRecord: MenuItem = menu.findItem(R.id.search_record)
        val searchView = searchRecord.actionView as SearchView

        searchView.apply {
            setOnCloseListener {
                recordViewModel.rollbackRecord()
                recordViewModel.recordLiveData.observe(
                    viewLifecycleOwner,
                    { records ->
                        records?.let {
                            updateUI(records, 0)
                        }
                    }
                )
                false
            }
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                   return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    recordViewModel.searchRecord(newText!!)
                    recordViewModel.recordLiveData.observe(
                        viewLifecycleOwner,
                        { records ->
                            records?.let {
                                updateUI(records, 1)
                            }
                        }
                    )
                    return true
                }
            })
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_records -> {
                callbacks?.onDeleteSelected()
                true
            }
            R.id.view_calender -> {
                callbacks?.onCalendarSelected()
                true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun updateUI(records: List<Record>, mod: Int) {
        adapter = RecordAdapter(records)
        recordRecyclerView.adapter = adapter
        if (records.isEmpty() && mod == 0) {
            noRecordSearchTextView.visibility = View.INVISIBLE
            noRecordTextView.visibility = View.VISIBLE
            noRecordButton.visibility = View.VISIBLE
            noRecordButton.setOnClickListener {
                val record = Record()
                record.date = Date(System.currentTimeMillis())
                recordViewModel.addRecord(record)
                callbacks?.onRecordSelected(record.id)
            }
        } else if (records.isNotEmpty() && mod == 0) {
            noRecordTextView.visibility = View.INVISIBLE
            noRecordButton.visibility = View.INVISIBLE
        } else if (records.isEmpty() && mod == 1) {
            noRecordSearchTextView.visibility = View.VISIBLE
            noRecordTextView.visibility = View.INVISIBLE
            noRecordButton.visibility = View.INVISIBLE
        } else {
            noRecordSearchTextView.visibility = View.INVISIBLE
        }
    }

    private inner class RecordHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener, View.OnLongClickListener {

        private lateinit var record: Record

        private val dateTextView: TextView = itemView.findViewById(R.id.record_date)
        private val partTextView: TextView = itemView.findViewById(R.id.record_part)
        private val routineTextView: TextView = itemView.findViewById(R.id.record_routine)

        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }
        fun bind(record: Record) {
            this.record = record
            dateTextView.text = DateFormat.format("yyyy-MM-dd EEE HH:mm", record.date).toString()
            partTextView.text = record.part
            routineTextView.text = record.routine
        }

        override fun onClick(v: View?) {
            callbacks?.onRecordSelected(record.id)
        }

        override fun onLongClick(v: View?): Boolean {
            callbacks?.onDeleteSelected()
            WorkoutListDeleteFragment.deleteID.add(record)
            return true
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