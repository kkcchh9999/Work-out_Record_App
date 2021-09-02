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

class WorkoutCalendarFragment : Fragment() {

    private lateinit var calendarView:  MaterialCalendarView
    private lateinit var workoutPart: TextView
    private lateinit var workoutRoutine: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_workout_calendar, container, false)

        calendarView = view.findViewById(R.id.calendarView)
        workoutPart = view.findViewById(R.id.part_textview)
        workoutRoutine = view.findViewById(R.id.routine_textview)

        calendarView.addDecorators(
            TodayDecorator(this.context),
            SundayDecorator(),
            SaturdayDecorator()
        )
        return view
    }

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

    companion object {
        fun newInstance() = WorkoutCalendarFragment()
    }
}