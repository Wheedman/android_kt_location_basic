package com.example.basic_location.ui.main

import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import androidx.annotation.WorkerThread
import androidx.arch.core.util.Function
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.basic_location.ui.main.database.MeteocoolLocation
import com.example.basic_location.ui.main.database.MeteocoolLocationDao
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow

class LocationRepository(
    private val meteocoolLocationDao: MeteocoolLocationDao,
    private val sharedPrefs: SharedPreferences
) {

    val lastLocation: Flow<MeteocoolLocation> = meteocoolLocationDao.getLastLocation()

    val locationStringShared : LiveData<String> = sharedPrefs.stringLiveData("location", "")

    val locationShared: LiveData<MeteocoolLocation> = Transformations.map(locationStringShared) { location: String ->
        getLocationFromShared(location)
    }

    private fun getLocationFromShared(location : String) : MeteocoolLocation?{
        if(location.isEmpty()){
            return null
        }
        val lat =  sharedPrefs.getFloat("lat", Float.NaN).toDouble()
        val lon =  sharedPrefs.getFloat("lon", Float.NaN).toDouble()

        return MeteocoolLocation(0, lat, lon, lon)
    }

    private fun buildLocationFromSharedPrefs(json : String?): Location? {
        if(json == null){
            return null
        }
        return Gson().fromJson(json, Location::class.java)
    }


    @WorkerThread
    suspend fun insert(location: MeteocoolLocation) {
        meteocoolLocationDao.insertLocation(location)
    }

}