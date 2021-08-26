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
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import java.util.*
import androidx.lifecycle.Observer

private const val RECORD_ID = "record_id"

class WorkoutDetailFragment : Fragment() {

    private lateinit var record: Record

    private lateinit var dateTextView: TextView
    private lateinit var partEditText: EditText
    private lateinit var routineEditText: EditText
    private lateinit var repeatEditText: EditText

    private val recordDetailViewModel: RecordDetailViewModel by lazy {
        ViewModelProvider(this).get(RecordDetailViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)

        record = Record()
        val recordId: UUID = arguments?.getSerializable(RECORD_ID) as UUID
        recordDetailViewModel.loadRecord(recordId)
    }

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recordDetailViewModel.recordLiveData.observe(
            viewLifecycleOwner,
            Observer { record ->
                record?.let {
                    this.record = record
                    updateUI()
                }
            }
        )
    }

    override fun onStart() {
        super.onStart()

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
        recordDetailViewModel.saveRecord(record)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_workout_detail, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_record -> {
                val alertDialogBuilder = AlertDialog.Builder(this.context)
                alertDialogBuilder.setTitle(R.string.delete_record)
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
                alertDialogBuilder.show()
                return true
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