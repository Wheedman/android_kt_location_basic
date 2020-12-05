package com.example.basic_location.ui.main

import android.content.SharedPreferences
import android.location.Location
import androidx.lifecycle.LiveData

class LocationRepository(private val locationService: LocationService, private val locationCache: SharedPreferences) {

    fun isRequestingLocation() : LiveData<Boolean> {
        return locationCache.booleanLiveData("requesting", false)
    }

    fun getLocation() : LiveData<Location> {
        return locationService.getLastLocation()
    }
}