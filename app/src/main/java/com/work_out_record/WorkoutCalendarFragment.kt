package com.work_out_record

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import java.util.*
import kotlin.collections.ArrayList

private const val CALENDAR_VIEW = "calendar_view"

class WorkoutCalendarFragment : Fragment() {

    private lateinit var calendarView:  MaterialCalendarView
    private lateinit var countRecords: TextView
    private lateinit var workoutRoutine: TextView

    private var calendarDays: MutableList<CalendarDay> = emptyList<CalendarDay>().toMutableList()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_workout_calendar, container, false)

        calendarView = view.findViewById(R.id.calendarView)
        countRecords = view.findViewById(R.id.count_records)
        workoutRoutine = view.findViewById(R.id.routine_textview)

        calendarView.addDecorators(
            NormalDayDecorator(),
            TodayDecorator(),
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
    class TodayDecorator() : DayViewDecorator {
        private var date = CalendarDay.today()

        override fun shouldDecorate(day: CalendarDay?): Boolean {
            return day?.equals(date)!!
        }

        override fun decorate(view: DayViewFacade?) {
            view?.addSpan(object : ForegroundColorSpan(Color.argb(100, 14, 220, 14)){})
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
            view?.addSpan(object : ForegroundColorSpan(Color.argb(100, 220, 14, 14)){})
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
            view?.addSpan(object : ForegroundColorSpan(Color.argb(100, 14, 14, 220)){})
        }
    }

    class NormalDayDecorator() : DayViewDecorator {
        private val calendar = Calendar.getInstance()
        override fun shouldDecorate(day: CalendarDay?): Boolean {
            day?.copyTo(calendar)
            val weekDay = calendar.get(Calendar.DAY_OF_WEEK)
            return weekDay != Calendar.SATURDAY && weekDay != Calendar.SUNDAY
        }

        override fun decorate(view: DayViewFacade?) {
            view?.addSpan(object : ForegroundColorSpan(Color.argb(100, 124, 124, 124)){})
        }
    }

    class CheckedDecorator(context: Context?, private val calendarDays: MutableList<CalendarDay>) : DayViewDecorator {
        private val calendar = Calendar.getInstance()
        @SuppressLint("UseCompatLoadingForDrawables")
        private val drawable = context?.getDrawable(R.drawable.ic_checked)

        override fun shouldDecorate(day: CalendarDay?): Boolean {
            return calendarDays.contains(day)
        }

        override fun decorate(view: DayViewFacade?) {
            view?.setBackgroundDrawable(drawable!!)
        }
    }

    companion object {
        fun newInstance() = WorkoutCalendarFragment()
    }
}
