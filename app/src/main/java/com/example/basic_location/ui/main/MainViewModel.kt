package com.example.basic_location.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.basic_location.storage.MeteocoolLocation

class MainViewModel(private val locationRepository : LocationRepository) : ViewModel() {

    private val _currentLocation = locationRepository.lastLocation.asLiveData()

    val currentLocation : LiveData<MeteocoolLocation>
        get() = _currentLocation


    private val _currentSharedLocation = locationRepository.locationShared

    val currentSharedLocation : LiveData<MeteocoolLocation>
        get() = _currentSharedLocation

    private val _currentSharedStringLocation = locationRepository.locationStringShared

    val currentSharedStringLocation : LiveData<String>
        get() = _currentSharedStringLocation
}