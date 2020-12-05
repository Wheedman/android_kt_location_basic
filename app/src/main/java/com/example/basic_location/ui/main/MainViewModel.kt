package com.example.basic_location.ui.main

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.basic_location.ui.main.database.MeteocoolLocation

class MainViewModel(private val locationRepository : LocationRepository) : ViewModel() {

    private val _currentLocation = locationRepository.lastLocation.asLiveData()

    val currentLocation : LiveData<MeteocoolLocation>
        get() = _currentLocation
}