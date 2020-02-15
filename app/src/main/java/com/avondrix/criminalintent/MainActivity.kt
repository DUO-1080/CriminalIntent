package com.avondrix.criminalintent

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity(), CrimeListFragment.Callbacks {
    private lateinit var btnAddCrime:Button
    private lateinit var emptyView:FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (currentFragment == null) {
            val fragment = CrimeListFragment.newInstance()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
        }
        emptyView = findViewById(R.id.empty_view_placeholder)
        btnAddCrime = findViewById(R.id.add_crime_btn)
        btnAddCrime.setOnClickListener {
            val crimeListFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
            if (crimeListFragment is CrimeListFragment) {
                crimeListFragment.addCrime()
                hideEmptyView()
            }
        }
    }

    override fun displayEmptyView() {
        if (emptyView.visibility == View.GONE) {
            emptyView.visibility = View.VISIBLE
        }
    }

    override fun hideEmptyView() {
        if (emptyView.visibility == View.VISIBLE)
            emptyView.visibility = View.GONE
    }

    override fun onCrimeSelected(crimeId: UUID) {
        val fragment = CrimeFragment.newInstance(crimeId)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }


}

