package com.work_out_record

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.view.*
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import java.util.*
import androidx.lifecycle.Observer

private const val RECORD_ID = "record_id"

class WorkoutDetailFragment : Fragment() {  //운동일지를 작성하는부분의 Fragment

    //레이아웃 요소들 및 record 객체 선언
    private lateinit var record: Record
    private lateinit var dateTextView: TextView
    private lateinit var partEditText: EditText
    private lateinit var routineEditText: EditText
    private lateinit var repeatEditText: EditText

    //루틴 저장을 위한 선언 및 뷰모델 선언
    private lateinit var savedRoutineName: Array<String>
    private val routineCode: Array<String> = arrayOf("루틴0", "루틴1", "루틴2", "루틴3", "루틴4")
    private val recordDetailViewModel = RecordAndRoutineViewModel.get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true) //옵션 메뉴 추가

        record = Record()
        val recordId: UUID = arguments?.getSerializable(RECORD_ID) as UUID
        recordDetailViewModel.loadRecord(recordId)  //DB 에 저장된 Record 정보 가져오기
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_workout_detail, container, false)

        //레이아웃 요소 연동
        dateTextView = view.findViewById(R.id.date_textView) as TextView
        partEditText = view.findViewById(R.id.part_editText) as EditText
        routineEditText = view.findViewById(R.id.routine_editText) as EditText
        repeatEditText = view.findViewById(R.id.repeat_editText) as EditText
        //저장된 루틴 이름 가져오기
        savedRoutineName = recordDetailViewModel.savedRoutineName

        return view
    }

    override fun onResume() {
        super.onResume()
        //액션바의 이름 설정
        val fragmentActivity: FragmentActivity? = activity
        if (activity != null) {
            (activity as WorkoutRecordActivity).setActionBarTitle(R.string.add_record)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recordDetailViewModel.recordLiveData.observe(   //record 의 변경사항을 관찰하는 Observer
            viewLifecycleOwner,
            Observer { record ->
                record?.let {
                    this.record = record
                    updateUI()          //변경사항 UI 에 적용
                }
            }
        )
    }

    override fun onStart() {
        super.onStart()

        //EditText 의 텍스트 변경점을 확인, 적용하는 TextWatcher
        val partWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                record.part = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        partEditText.addTextChangedListener(partWatcher)

        //EditText 의 텍스트 변경점을 확인, 적용하는 TextWatcher
        val routineWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                record.routine = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        routineEditText.addTextChangedListener(routineWatcher)

        //EditText 의 텍스트 변경점을 확인, 적용하는 TextWatcher
        val repeatWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                record.repeat = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        repeatEditText.addTextChangedListener(repeatWatcher)
    }

    override fun onStop() {
        super.onStop()
        //해당 프래그먼트에서 나갈 때 일지를 저장
        recordDetailViewModel.saveRecord(record)

        if (record.part.isBlank()
            && record.repeat.isBlank()
            && record.routine.isBlank()) {
            Toast.makeText(
                this.context,
                R.string.empty_record_alert,
                Toast.LENGTH_SHORT)
                .show()
            recordDetailViewModel.deleteRecord(record)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_workout_detail, menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_record -> {
                val builder = AlertDialog.Builder(this.context,R.style.AlertDialogTheme)
                builder.setTitle(R.string.delete_record)
                    .setMessage(R.string.delete_record_really)
                    .setPositiveButton(R.string.yes_button,
                        DialogInterface.OnClickListener { _, _ ->
                            recordDetailViewModel.deleteRecord(record)
                            fragmentManager?.popBackStack()
                    })
                    .setNegativeButton(R.string.no_button,
                        DialogInterface.OnClickListener { dialog, which ->
                            //유저가 취소함
                    })
                val alertDialog = builder.create()
                alertDialog.show()
                alertDialog.window?.setBackgroundDrawableResource(R.drawable.alert_dialog_background)
                true
            }
            R.id.save_routine -> {
                var selectedItem = -1
                val builder = AlertDialog.Builder(this.context,R.style.AlertDialogTheme)
                builder.setTitle(R.string.select_routine_save)
                    .setSingleChoiceItems(savedRoutineName, -1,
                        DialogInterface.OnClickListener { _, which ->
                            selectedItem = which
                        })
                    .setPositiveButton(R.string.save_here,
                    DialogInterface.OnClickListener { _, _ ->
                        if (selectedItem == -1) {
                            Toast.makeText(this.context, R.string.select_first, Toast.LENGTH_SHORT).show()
                        } else {
                            val mText = record.part
                            recordDetailViewModel.saveRoutineName(
                                selectedItem,
                                mText
                            )
                            recordDetailViewModel.saveRoutine(
                                routineCode[selectedItem],
                                record.routine + "///" + record.part
                            )
                        }
                    })
                    .setNegativeButton(R.string.cancel,
                    DialogInterface.OnClickListener { _, _ ->
                        //유저가 취소함
                    })
                    .setNeutralButton(R.string.change_routine_name,
                        DialogInterface.OnClickListener { _, _ ->
                            if (selectedItem == -1) {
                                Toast.makeText(this.context, R.string.select_first, Toast.LENGTH_SHORT).show()
                            } else {
                                val inflater = requireActivity()
                                    .layoutInflater
                                    .inflate(R.layout.change_routine_name_dialog, null)
                                val builder: AlertDialog.Builder = AlertDialog.Builder(this.context, R.style.AlertDialogTheme)
                                builder.setTitle(R.string.save_routine)
                                    .setView(inflater)

                                val input = inflater.findViewById(R.id.change_routine_edittext) as EditText

                                builder.setPositiveButton(R.string.save_here,
                                    DialogInterface.OnClickListener { _, _ ->
                                        var mText = input.text.toString()
                                        if (mText == "") {
                                            Toast.makeText(this.context, R.string.type_first, Toast.LENGTH_SHORT).show()
                                        } else {
                                            recordDetailViewModel.saveRoutineName(
                                                selectedItem,
                                                mText
                                            )
                                            recordDetailViewModel.saveRoutine(
                                                routineCode[selectedItem],
                                                record.routine + "///" + record.part
                                            )
                                        }
                                    })
                                builder.setNegativeButton(R.string.cancel,
                                    DialogInterface.OnClickListener { _, _ ->
                                        //유저가 취소함
                                    })
                                val alertDialog = builder.create()
                                alertDialog.show()
                                alertDialog.window?.setBackgroundDrawableResource(R.drawable.alert_dialog_background)
                            }
                        })
                val alertDialog = builder.create()
                alertDialog.show()
                alertDialog.window?.setBackgroundDrawableResource(R.drawable.alert_dialog_background)

                true
            }
            R.id.load_routine -> {
                var selectedItem: Int = -1
                val builder = AlertDialog.Builder(this.context, R.style.AlertDialogTheme)

                builder.setTitle(R.string.select_routine_load)
                    .setSingleChoiceItems(savedRoutineName, -1,
                        DialogInterface.OnClickListener { _, which ->
                            selectedItem = which
                        })
                    .setPositiveButton(R.string.load_here,
                        DialogInterface.OnClickListener { _, _ ->
                            if (selectedItem == -1) {
                                Toast.makeText(this.context,
                                    R.string.select_routine_load,
                                    Toast.LENGTH_SHORT)
                                    .show()
                            } else {
                                val stringArr =
                                    recordDetailViewModel.loadRoutine(routineCode[selectedItem])
                                        .split("///")
                                if (stringArr.size == 1) {
                                    Toast.makeText(this.context,
                                        R.string.empty_routine,
                                        Toast.LENGTH_SHORT)
                                        .show()
                                } else {
                                    routineEditText.setText(stringArr[0])
                                    partEditText.setText(stringArr[1]!!)
                                }
                            }
                        })
                    .setNegativeButton(R.string.cancel,
                        DialogInterface.OnClickListener { _, _ ->
                            //유저가 취소함
                        })
                val alertDialog = builder.create()
                alertDialog.show()
                alertDialog.window?.setBackgroundDrawableResource(R.drawable.alert_dialog_background)

                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateUI() {
        partEditText.setText(record.part)
        repeatEditText.setText(record.repeat)
        routineEditText.setText(record.routine)
        dateTextView.text = DateFormat.format("yyyy-MM-dd EEE HH:mm", record.date).toString()
    }

    companion object {
        fun newInstance(recordId: UUID): WorkoutDetailFragment {
            val args = Bundle().apply {
                putSerializable(RECORD_ID, recordId)
            }
            return WorkoutDetailFragment().apply {
                arguments = args
            }
        }
    }
}

