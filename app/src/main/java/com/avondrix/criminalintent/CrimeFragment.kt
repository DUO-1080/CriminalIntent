package com.avondrix.criminalintent

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import java.util.*
import androidx.lifecycle.Observer

private const val ARG_CRIME_ID = "crime_id"
private const val DIALOG_DATE = "DialogDate"
private const val DIALOG_TIME = "DialogTime"
private const val DATE_PICKER_REQUEST_CODE = 1
private const val TIME_PICKER_REQUEST_CODE = 2

class CrimeFragment : Fragment(),DatePickerFragment.Callbacks, TimePickerFragment.TimeSelectCallbacks {
    private lateinit var crime: Crime
    private lateinit var title: TextView
    private lateinit var dateButton: Button
    private lateinit var solvedCheckBox: CheckBox
    private lateinit var timeButton: Button
    private val viewModel: CrimeDetailViewModel by lazy {
        ViewModelProvider(this).get(CrimeDetailViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val crimeId = arguments?.getSerializable(ARG_CRIME_ID) as UUID
        crime = Crime()
        viewModel.loadCrime(crimeId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime, container, false)
        title = view.findViewById(R.id.crime_title)
        dateButton = view.findViewById(R.id.crime_date)
        dateButton.text = crime.date.toString()
        solvedCheckBox = view.findViewById(R.id.crime_solved)
        timeButton = view.findViewById(R.id.crime_time)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.crimeLiveData.observe(
            viewLifecycleOwner,
            Observer {
                it?.let {
                    this.crime = it
                    updateUI()
                }
            }
        )
    }

    private fun updateUI() {
        title.text = crime.title
        val dateTime = android.text.format.DateFormat.format("yyyy-MM-dd HH:mm:ss",crime.date)
        val dateAndTime = dateTime.split(" ")
        dateButton.text = dateAndTime[0]
        timeButton.text = dateAndTime[1]
        solvedCheckBox.apply {
            isChecked = crime.isSolved
//            jumpDrawablesToCurrentState()
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.saveCrime(crime)
    }

    override fun onStart() {
        super.onStart()
        title.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                crime.title = s.toString()
            }

        })
        solvedCheckBox.apply {
            setOnCheckedChangeListener { _, isChecked ->
                crime.isSolved = isChecked
            }
        }
        dateButton.setOnClickListener {
            DatePickerFragment.newInstance(crime.date).apply {
                show(this@CrimeFragment.parentFragmentManager, DIALOG_DATE)
                setTargetFragment(this@CrimeFragment, DATE_PICKER_REQUEST_CODE)
            }
        }
        timeButton.setOnClickListener {
            TimePickerFragment.newInstance(crime.date).apply {
                show(this@CrimeFragment.parentFragmentManager, DIALOG_TIME)
                setTargetFragment(this@CrimeFragment, TIME_PICKER_REQUEST_CODE)
            }
        }
    }

    companion object {
        fun newInstance(id: UUID): CrimeFragment {
            val arg = Bundle().apply {
                putSerializable(ARG_CRIME_ID, id)
            }
            return CrimeFragment().apply {
                arguments = arg
            }
        }
    }

    override fun onDateSelect(date: Date) {
        crime.date = date
        updateUI()
    }

    override fun onTimeSelect(date: Date) {
        crime.date = date
        updateUI()
    }

}