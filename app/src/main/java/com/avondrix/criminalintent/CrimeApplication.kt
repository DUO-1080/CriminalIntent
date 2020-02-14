package com.avondrix.criminalintent

import android.app.Application

class CrimeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        CrimeRepository.init(this)
    }
}