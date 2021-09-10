package com.work_out_record

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.format.DateFormat
import android.view.*
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*
import kotlin.collections.ArrayList

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
    private val recordViewModel = RecordsViewModel.get()

    //레이아웃 아이템 선언
    private lateinit var noRecordSearchTextView: TextView
    private lateinit var noRecordTextView: TextView
    private lateinit var noRecordButton: ImageButton
    private lateinit var savedRoutineName: Array<String>
    private lateinit var floatingButton: FloatingActionButton
    private val routineCode: Array<String> = arrayOf("루틴0", "루틴1", "루틴2", "루틴3", "루틴4")

    //앱이 실행되면 callbacks 객체 선언
    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true) //메뉴 설정
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_workout_list, container, false)

        //레이아웃 객체 연결
        recordRecyclerView = view.findViewById(R.id.record_recyclerview)
        recordRecyclerView.layoutManager = GridLayoutManager(context, 2)
        recordRecyclerView.adapter = adapter
        noRecordTextView = view.findViewById(R.id.no_record_text)
        noRecordButton = view.findViewById(R.id.no_record_button)
        noRecordSearchTextView = view.findViewById(R.id.no_record_search)
        floatingButton = view.findViewById(R.id.floating_button)
        floatingButton.visibility = View.VISIBLE

        //fab 메모추가기능
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

    //프래그먼트가 켜졌을 때 앱바 이름 설정
    override fun onResume() {
        super.onResume()
        activity
        if (activity != null) {
            (activity as WorkoutRecordActivity).setActionBarTitle(R.string.wor)
        }
    }

    //UI 업데이트, 데이터베이스의 값 읽어와서 보여주기
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recordViewModel.rollbackRecord()
        recordViewModel.recordLiveData.observe(
            viewLifecycleOwner,
            { records ->
                records?.let {
                    updateUI(records, 0)
                }
            }
        )
    }

    //종료시에 callbacks 객체 삭제
    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    //옵션 메뉴
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_workout_list, menu)

        val searchRecord: MenuItem = menu.findItem(R.id.search_record)
        val searchView = searchRecord.actionView as SearchView

        //SearchView, 검색기능
        searchView.apply {
            setOnCloseListener {    //검색 취소시
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
                //확인 버튼을 눌렀을 때
                override fun onQueryTextSubmit(query: String?): Boolean {
                   return true
                }
                //글자가 바뀌었을 때
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
    //옵션이 선택되었을 때
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            //삭제 옵션 -> 프래그먼트 이동
            R.id.delete_records -> {
                callbacks?.onDeleteSelected()
                true
            }
            //캘린더 옵션 -> 프래그먼트 이동
            R.id.view_calender -> {
                callbacks?.onCalendarSelected()
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
    //UI 업데이트 함수
    private fun updateUI(records: List<Record>, mod: Int) {
        adapter = RecordAdapter(records)
        //입력받은 list 로 리사이클러 어댑터에 정보 전달
        recordRecyclerView.adapter = adapter
        //mod 값으로 검색했을 때 레코드가 없는것과 아무 데이터 없는 것의 구분
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

    //리사이클러뷰의 홀더
    private inner class RecordHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener, View.OnLongClickListener {

        private lateinit var record: Record

        private val dateTextView: TextView = itemView.findViewById(R.id.record_date)
        private val partTextView: TextView = itemView.findViewById(R.id.record_part)
        private val routineTextView: TextView = itemView.findViewById(R.id.record_routine)

        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }
        //데이터 값 바인딩 함수
        fun bind(record: Record) {
            this.record = record
            dateTextView.text = DateFormat.format("yyyy-MM-dd EEE HH:mm", record.date).toString()
            partTextView.text = record.part
            routineTextView.text = record.routine
        }
        //클릭 리스너
        override fun onClick(v: View?) {
            callbacks?.onRecordSelected(record.id)
        }

        override fun onLongClick(v: View?): Boolean {
            callbacks?.onDeleteSelected()
            WorkoutListDeleteFragment.deleteID.add(record)
            return true
        }
    }

    //리사이클러 뷰 어댑터
    private inner class RecordAdapter(var records: List<Record>) : RecyclerView.Adapter<RecordHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordHolder {
            val view = layoutInflater.inflate(R.layout.list_record, parent, false)
            return RecordHolder(view)
        }

        override fun onBindViewHolder(holder: RecordHolder, position: Int) {
            //데이터 값 바인딩
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