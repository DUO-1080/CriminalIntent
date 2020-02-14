package com.avondrix.criminalintent

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.avondrix.criminalintent.database.CrimeDatabase
import java.lang.IllegalStateException
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors

private const val DATABASE_NAME = "crime-database"

class CrimeRepository private constructor(context: Context){

    private val database:CrimeDatabase = Room.databaseBuilder(
        context.applicationContext,
        CrimeDatabase::class.java,
        DATABASE_NAME
    ).build()

    private val crimeDao = database.crimeDao()

    private val executor = Executors.newSingleThreadExecutor()

    fun getCrimes(): LiveData<List<Crime>> = crimeDao.getCrimes()

    fun getCrime(id:UUID): LiveData<Crime> = crimeDao.getCrime(id)

    fun updateCrime(crime: Crime) {
        executor.execute {
            crimeDao.updateCrime(crime)
        }
    }

    fun addCrime(crime: Crime) {
        executor.execute {
            crimeDao.addCrime(crime)
        }
    }

    companion object{
        private var instance: CrimeRepository? = null
        fun init(context: Context) {
            if (instance == null) {
                instance = CrimeRepository(context)
            }
        }
        fun get(): CrimeRepository {
            return instance ?: throw IllegalStateException("Crime Repository must be init")
        }
    }
}