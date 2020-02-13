package com.avondrix.criminalintent

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment

class CrimeFragment : Fragment() {
    private lateinit var crime: Crime
    private lateinit var title: TextView
    private lateinit var dateButton: Button
    private lateinit var solvedCheckBox: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime = Crime()
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


}