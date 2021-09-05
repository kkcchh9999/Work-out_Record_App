package com.work_out_record

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.Month
import java.util.*

private const val CALENDAR_VIEW = "calendar_view"

class WorkoutCalendarFragment : Fragment() {

    private lateinit var calendarView:  MaterialCalendarView
    private lateinit var workoutPart: TextView
    private lateinit var workoutRoutine: TextView
    private var dates: MutableList<Date> = emptyList<Date>().toMutableList()
    private var calendarDays: MutableList<CalendarDay> = emptyList<CalendarDay>().toMutableList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_workout_calendar, container, false)

        calendarView = view.findViewById(R.id.calendarView)
        workoutPart = view.findViewById(R.id.part_textview)
        workoutRoutine = view.findViewById(R.id.routine_textview)

        for (i in 0 until dates.size) {
            Log.d("이거 찍냐?", "$i")
            calendarDays.add(CalendarDay(dates[i]))
        }

        calendarView.addDecorators(
            TodayDecorator(this.context),
            SundayDecorator(),
            SaturdayDecorator(),
            CheckedDecorator(this.context, calendarDays)
        )

        return view
    }

    override fun onResume() {
        super.onResume()
        //액션바에 텍스트 변경
        if (activity != null) {
            (activity as WorkoutRecordActivity).setActionBarTitle(R.string.calendar)
        }


    }

    //오늘 날짜에 흰색 체크
    class TodayDecorator(context: Context?) : DayViewDecorator {
        private var date = CalendarDay.today()
        @SuppressLint("UseCompatLoadingForDrawables")
        val drawable = context?.resources?.getDrawable(R.drawable.style_radius)

        override fun shouldDecorate(day: CalendarDay?): Boolean {
            return day?.equals(date)!!
        }

        override fun decorate(view: DayViewFacade?) {
            view?.setBackgroundDrawable(drawable!!)
        }
    }

    //일요일을 붉은색으로
    class SundayDecorator() : DayViewDecorator {
        private val calendar = Calendar.getInstance()
        override fun shouldDecorate(day: CalendarDay?): Boolean {
            day?.copyTo(calendar)
            val weekDay = calendar.get(Calendar.DAY_OF_WEEK)
            return weekDay == Calendar.SUNDAY
        }

        override fun decorate(view: DayViewFacade?) {
            view?.addSpan(object : ForegroundColorSpan(Color.RED){})
        }
    }

    //토요일을 파란색으로
    class SaturdayDecorator() : DayViewDecorator {
        private val calendar = Calendar.getInstance()
        override fun shouldDecorate(day: CalendarDay?): Boolean {
            day?.copyTo(calendar)
            val weekDay = calendar.get(Calendar.DAY_OF_WEEK)
            return weekDay == Calendar.SATURDAY
        }

        override fun decorate(view: DayViewFacade?) {
            view?.addSpan(object : ForegroundColorSpan(Color.BLUE){})
        }
    }

    class CheckedDecorator(context: Context?, private val calendarDays: MutableList<CalendarDay>) : DayViewDecorator {
        private val calendar = Calendar.getInstance()
        @SuppressLint("UseCompatLoadingForDrawables")
        private val drawable = context?.getDrawable(R.drawable.style_checked)

        override fun shouldDecorate(day: CalendarDay?): Boolean {
            val ret: Boolean = calendarDays.contains(day)
            return ret
        }

        override fun decorate(view: DayViewFacade?) {
            view?.setBackgroundDrawable(drawable!!)
        }
    }

    companion object {
        fun newInstance(dates: List<Date>): WorkoutCalendarFragment {
            val args = Bundle().apply{
              //  putSerializable(CALENDAR_VIEW, dates)
            }

            return WorkoutCalendarFragment().apply {
                arguments = args
            }
        }
    }
}