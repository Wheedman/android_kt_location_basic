package com.example.basic_location.ui.main

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class MainViewModel(private val locationRepository : LocationRepository) : ViewModel() {

    private val _requestingLocationUpdates = locationRepository.isRequestingLocation()
    private val _currentLocation = locationRepository.getLocation()

    val locationData : LiveData<Boolean>
        get() = _requestingLocationUpdates

    val currentLocation : LiveData<Location>
        get() = _currentLocation
}