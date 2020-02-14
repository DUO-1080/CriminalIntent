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

class CrimeFragment : Fragment() {
    private lateinit var crime: Crime
    private lateinit var title: TextView
    private lateinit var dateButton: Button
    private lateinit var solvedCheckBox: CheckBox
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
        dateButton.apply {
            text = crime.date.toString()
            isEnabled = false
        }
        solvedCheckBox = view.findViewById(R.id.crime_solved)
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
        dateButton.text = crime.date.toString()
        solvedCheckBox.apply {
            isChecked = crime.isSolved
            jumpDrawablesToCurrentState()
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

}