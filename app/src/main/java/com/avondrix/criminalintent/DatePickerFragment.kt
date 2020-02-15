package com.avondrix.criminalintent

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.util.*

private const val ARG_DATE = "date"
class DatePickerFragment : DialogFragment() {
    private val listener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
        val date = GregorianCalendar(year,month,dayOfMonth).time
        targetFragment?.let {
            (it as Callbacks).onDateSelect(date)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val date = arguments?.getSerializable(ARG_DATE) as Date
        val calendar = Calendar.getInstance()
        calendar.time = date
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        return DatePickerDialog(
            requireContext(),
            listener,
            year,
            month,
            day
        )
    }

    companion object{
        fun newInstance(date:Date): DatePickerFragment {
            val bundle = Bundle()
            bundle.putSerializable(ARG_DATE,date)
            return DatePickerFragment().apply {
                arguments = bundle
            }
        }
    }

    interface Callbacks{
        fun onDateSelect(date:Date)
    }
}