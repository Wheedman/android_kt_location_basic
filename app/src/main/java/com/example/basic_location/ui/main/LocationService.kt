package com.example.basic_location.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.basic_location.ui.main.database.MeteocoolLocationDao


class LocationService() : Service() {

    private lateinit var locationManager: LocationManager
    private val minTime: Long = 3000
    private val minDistance: Float = 500f

    override fun onCreate() {
        super.onCreate()
    }



    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    inner class LocationListener(provider: String) : android.location.LocationListener {
        private val pro = provider
        override fun onLocationChanged(location: Location) {

            Log.e(TAG, "onLocationChanged: $pro $location")
            getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE).edit()
                .putString("LONG", location.longitude.toString())
                .putString("LAT", location.latitude.toString())
                .putString("ALT", location.altitude.toString())
                .apply()
        }

        override fun onProviderDisabled(provider: String) {
            Log.e(TAG, "onProviderDisabled: $provider")
        }

        override fun onProviderEnabled(provider: String) {
            Log.e(TAG, "onProviderEnabled: $provider")
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
            Log.e(TAG, "onStatusChanged: $provider")
        }

        init {
            Log.e(TAG, "LocationListener $provider")
            val lastLocation = Location(provider)

            getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE).edit()
                .putString("LONG", lastLocation.longitude.toString())
                .putString("LAT", lastLocation.latitude.toString())
                .putString("ALT", lastLocation.altitude.toString())
                .apply()
        }
    }

    companion object {
        val TAG = LocationService::class.simpleName
    }


}