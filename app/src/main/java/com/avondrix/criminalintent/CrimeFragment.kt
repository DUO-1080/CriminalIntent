package com.avondrix.criminalintent

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import java.util.*

private const val ARG_CRIME_ID = "crime_id"
private const val DIALOG_DATE = "DialogDate"
private const val DATE_PICKER_REQUEST_CODE = 1
private const val CONTACT_PICK_REQUEST_CODE = 2
private const val CONTACT_PERMISSION_REQUEST_CODE = 3
private const val DATE_FORMAT = "EEE, MMM, dd"
private const val TAG = "CrimeFragment"
class CrimeFragment : Fragment(), DatePickerFragment.Callbacks {
    private lateinit var crime: Crime
    private lateinit var title: TextView
    private lateinit var dateButton: Button
    private lateinit var solvedCheckBox: CheckBox
    private lateinit var reportButton: Button
    private lateinit var suspectButton: Button
    private lateinit var callButton: Button
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
        reportButton = view.findViewById(R.id.crime_report)
        suspectButton = view.findViewById(R.id.crime_suspect)
        callButton = view.findViewById(R.id.crime_call)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when {
            resultCode != Activity.RESULT_OK -> return

            requestCode == CONTACT_PICK_REQUEST_CODE && data != null -> {
                val contactUri: Uri? = data.data
                val queryFile = arrayOf(ContactsContract.Contacts.DISPLAY_NAME,ContactsContract.Contacts._ID)
                val cursor = contactUri?.let {
                    requireActivity().contentResolver.query(
                        it,
                        queryFile,
                        null,
                        null,
                        null
                    )
                }
                cursor?.use {
                    if (it.count == 0) {
                        return
                    }
                    it.moveToFirst()
                    val suspect = it.getString(0)
                    val suspectId = it.getLong(1)
                    crime.suspect = suspect
                    viewModel.saveCrime(crime)
                    suspectButton.text = suspect
                    if (ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_CONTACTS),
                            CONTACT_PERMISSION_REQUEST_CODE)
                    }else{
                        getPhone(suspectId)
                    }
                }

            }
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

        reportButton.setOnClickListener {
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getCrimeReport())
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject))
            }.also {
                //                startActivity(it)
                val chooser = Intent.createChooser(it, getString(R.string.send_report))
                startActivity(chooser)
            }
        }
        suspectButton.apply {
            val contactIntent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
            setOnClickListener {
                startActivityForResult(contactIntent, CONTACT_PICK_REQUEST_CODE)
            }

            val packageManager = requireActivity().packageManager
            val info = packageManager.resolveActivity(contactIntent,PackageManager.MATCH_DEFAULT_ONLY)
            if (info == null) {
                suspectButton.isEnabled = false
            }
        }
        callButton.apply {
            setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL,Uri.parse("tel:${crime.phone}"))
                startActivity(intent)
            }
        }
    }

    private fun updateUI() {
        title.text = crime.title
        dateButton.text = crime.date.toString()
        solvedCheckBox.apply {
            isChecked = crime.isSolved
//            jumpDrawablesToCurrentState()
        }
        if (crime.suspect.isNotBlank()) {
            suspectButton.text = crime.suspect
        }
        if (crime.phone.isNotBlank()) {
            callButton.isEnabled = true
            callButton.text = getString(R.string.suspect_call,crime.suspect)
        }else{
            callButton.isEnabled = false
            callButton.text = getString(R.string.suspect_no_call)
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

    private fun getCrimeReport(): String {
        val solvedString = if (crime.isSolved) {
            getString(R.string.crime_report_solved)
        } else {
            getString(R.string.crime_report_unsolved)
        }
        val suspectString = if (crime.suspect.isBlank()) {
            getString(R.string.crime_report_no_suspect)
        } else {
            getString(R.string.crime_report_suspect, crime.suspect)
        }
        val dateString = DateFormat.format(DATE_FORMAT, crime.date)
        return getString(
            R.string.crime_report,
            crime.title,
            dateString,
            solvedString,
            suspectString
        )
    }

    private fun getPhone(id: Long) {
        val cursor = requireContext().contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
            "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID}=$id",
           null,
            null
        )
        cursor?.use {
            if (it.count == 0) {
                Log.d(TAG, "getPhone: null")
                return
            }
            it.moveToFirst()
            val phone = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            crime.phone = phone.toString()
            viewModel.saveCrime(crime)
            Log.d(TAG, "getPhone: $phone")
        }
    }
}