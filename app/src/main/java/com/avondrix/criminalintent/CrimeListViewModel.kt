package com.avondrix.criminalintent

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class CrimeListViewModel : ViewModel() {
    private val crimeRepository = CrimeRepository.get()
    val crimesListLiveData = crimeRepository.getCrimes()
}