package com.avondrix.criminalintent

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.util.*

private const val ARG_TIME = "time"

class TimePickerFragment : DialogFragment() {
    private  var calendar = Calendar.getInstance()
    private val listener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay)
        calendar.set(Calendar.MINUTE,minute)
        (targetFragment as TimeSelectCallbacks).onTimeSelect(calendar.time)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val date = arguments?.getSerializable(ARG_TIME) as Date
        calendar.time = date
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        return TimePickerDialog(requireContext(), listener, hour, minute, true)
    }

    companion object {
        fun newInstance(date: Date): TimePickerFragment {
            val bundle = Bundle()
            bundle.putSerializable(ARG_TIME, date)
            return TimePickerFragment().apply {
                arguments = bundle
            }
        }
    }

    interface TimeSelectCallbacks{
        fun onTimeSelect(date: Date)
    }
}