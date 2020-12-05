package com.example.basic_location.ui.main

import android.location.Location
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.example.basic_location.ui.main.database.MeteocoolLocation
import com.example.basic_location.ui.main.database.MeteocoolLocationDao
import kotlinx.coroutines.flow.Flow

class LocationRepository(private val meteocoolLocationDao: MeteocoolLocationDao) {

    val lastLocation : Flow<MeteocoolLocation> = meteocoolLocationDao.getLastLocation()

    @WorkerThread
    suspend fun insert(location: MeteocoolLocation) {
        meteocoolLocationDao.insertLocation(location)
    }

}