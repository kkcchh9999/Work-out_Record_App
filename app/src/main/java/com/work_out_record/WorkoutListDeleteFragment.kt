package com.work_out_record

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.format.DateFormat
import android.view.*
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class WorkoutListDeleteFragment: Fragment() {

    //리사이클러뷰 선언
    private lateinit var recordRecyclerView: RecyclerView
    private var adapter: RecordAdapter? = RecordAdapter(emptyList())
    //뷰모델 선언
    private val recordViewModel = RecordsViewModel.get()

    //레코드 삭제를 위한 리스트 선언
    private lateinit var deleteAll: List<Record>

    //FAB 선언
    private lateinit var floatingButton:FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //옵션 설정
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_workout_list, container, false)

        //레이아웃 요소들 선언
        recordRecyclerView = view.findViewById(R.id.record_recyclerview)
        recordRecyclerView.layoutManager = GridLayoutManager(context, 2)
        recordRecyclerView.adapter = adapter
        floatingButton = view.findViewById(R.id.floating_button)
        floatingButton.visibility = View.INVISIBLE

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recordViewModel.recordLiveData.observe( //record LiveData 의 변화를 관찰하는 Observer
            viewLifecycleOwner,
            Observer { records ->
                deleteAll = records
                records?.let {
                    updateUI(records)
                }
            }
        )
    }

    override fun onResume() {
        super.onResume()
        //액션바에 텍스트 변경
        if (activity != null) {
            (activity as WorkoutRecordActivity).setActionBarTitle(R.string.delete_records)
        }
    }

    override fun onStop() {
        super.onStop()
        //삭제하지않고 돌아갔을 때
        deleteID.clear()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        //옵션 메뉴 설정
        inflater.inflate(R.menu.fragment_delete_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_really -> { //삭제 버튼
                val builder = AlertDialog.Builder(this.context,R.style.AlertDialogTheme)
                builder.setTitle(R.string.delete_records)
                    .setMessage(R.string.delete_record_really)
                    .setPositiveButton(R.string.yes_button) { _, _ ->
                        if (deleteID.isEmpty()) {
                            Toast.makeText(
                                this.context,
                                R.string.select_item_first,
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            deleteID.forEach {
                                recordViewModel.deleteRecord(it)
                                fragmentManager?.popBackStack()
                            }
                        }
                    }
                    .setNegativeButton(R.string.no_button) { _, _ ->
                        //유저가 취소함
                    }
                val alertDialog = builder.create()
                alertDialog.show()
                alertDialog.window?.setBackgroundDrawableResource(R.drawable.alert_dialog_background)
                true
            }

            R.id.delete_all -> {    //전체삭제 버튼
                val builder = AlertDialog.Builder(this.context,R.style.AlertDialogTheme)
                builder.setTitle(R.string.delete_all)
                    .setMessage(R.string.delete_record_really)
                    .setPositiveButton(R.string.yes_button) { _, _ ->
                        if (deleteAll.isEmpty()) {
                            Toast.makeText(
                                this.context,
                                R.string.empty_records,
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            deleteAll.forEach {
                                recordViewModel.deleteRecord(it)
                            }
                        }
                        fragmentManager?.popBackStack()
                    }
                    .setNegativeButton(R.string.no_button) { _, _ ->
                        //유저가 취소함
                    }
                val alertDialog = builder.create()
                alertDialog.show()
                alertDialog.window?.setBackgroundDrawableResource(R.drawable.alert_dialog_background)
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    //UI 업데이트 함수
    private fun updateUI(records: List<Record>) {
        adapter = RecordAdapter(records)
        recordRecyclerView.adapter = adapter
    }

    //리사이클러뷰 홀더
    private inner class RecordHolder(view: View) : RecyclerView.ViewHolder(view) {

        private lateinit var record: Record

        //레이아웃 요소들
        private val dateTextView: TextView = itemView.findViewById(R.id.record_date)
        private val partTextView: TextView = itemView.findViewById(R.id.record_part)
        private val routineTextView: TextView = itemView.findViewById(R.id.record_routine)
        private val deleteCheckBox: CheckBox = itemView.findViewById(R.id.delete_checkBox)

        fun bind(record: Record) {  //데이터 바인딩 함수
            this.record = record
            dateTextView.text = DateFormat.format("yyyy-MM-dd EEE HH:mm", record.date).toString()
            partTextView.text = record.part
            routineTextView.text = record.routine
            deleteCheckBox.isChecked = deleteID.contains(record)
        }

        fun select(record: Record) { //checkBox 아이템이 선택되었을 때 함수
            this.record = record
            deleteCheckBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    deleteID.add(record)
                    deleteID = deleteID.distinct().toMutableList()
                } else {
                    deleteID.remove(record)
                }
            }
        }

    }

    //리사이클러뷰 어댑터
    private inner class RecordAdapter(var records: List<Record>) : RecyclerView.Adapter<RecordHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordHolder {
            val view = layoutInflater.inflate(R.layout.list_record_delete, parent, false)
            return RecordHolder(view)
        }

        override fun onBindViewHolder(holder: RecordHolder, position: Int) {
            val record = records[position]
            holder.select(record)
            holder.bind(record)
        }

        override fun getItemCount(): Int {
            return records.size
        }
    }

    companion object {
        //레코드 삭제를 위한 리스트 선언
        var deleteID: MutableList<Record> = emptyList<Record>().toMutableList()

        fun newInstance() = WorkoutListDeleteFragment()
    }
}