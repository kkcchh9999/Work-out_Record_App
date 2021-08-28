package com.work_out_record

import android.content.Context
import android.os.Bundle
import android.text.format.DateFormat
import android.view.*
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
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
        fun onDeleteSelected()
    }
    private var callbacks: Callbacks? = null

    private lateinit var recordRecyclerView: RecyclerView
    private var adapter: RecordAdapter? = RecordAdapter(emptyList())

    private lateinit var noRecordSearchTextView: TextView
    private lateinit var noRecordTextView: TextView
    private lateinit var noRecordButton: ImageButton

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
        noRecordTextView = view.findViewById(R.id.no_record_text)
        noRecordButton = view.findViewById(R.id.no_record_button)
        noRecordSearchTextView = view.findViewById(R.id.no_record_search)

        return view
    }

    override fun onResume() {
        super.onResume()
        val fragmentActivity: FragmentActivity? = activity
        if (activity != null) {
            (activity as WorkoutRecordActivity).setActionBarTitle(R.string.app_name)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recordViewModel.recordLiveData.observe(
            viewLifecycleOwner,
            Observer { records ->
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
                    Observer { records ->
                        records?.let {
                            updateUI(records, 0)
                        }
                    }
                )
                false
            }
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    recordViewModel.searchRecord(query!!)
                    recordViewModel.recordLiveData.observe(
                        viewLifecycleOwner,
                        Observer { records ->
                            records?.let {
                                updateUI(records, 1)
                            }
                        }
                    )
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    recordViewModel.searchRecord(newText!!)
                    recordViewModel.recordLiveData.observe(
                        viewLifecycleOwner,
                        Observer { records ->
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
            R.id.add_record -> {
                val record = Record()
                record.date = Date(System.currentTimeMillis())
                recordViewModel.addRecord(record)
                callbacks?.onRecordSelected(record.id)
                true
            }
            R.id.delete_records -> {
                callbacks?.onDeleteSelected()
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun updateUI(records: List<Record>, mod: Int) {
        adapter = RecordAdapter(records)
        recordRecyclerView.adapter = adapter
        if (records.isEmpty() && mod == 0) {
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
        } else {
            noRecordSearchTextView.visibility = View.INVISIBLE
        }
    }

    private inner class RecordHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        private lateinit var record: Record

        private val dateTextView: TextView = itemView.findViewById(R.id.record_date)
        private val partTextView: TextView = itemView.findViewById(R.id.record_part)
        private val routineTextView: TextView = itemView.findViewById(R.id.record_routine)

        init {
            itemView.setOnClickListener(this)
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