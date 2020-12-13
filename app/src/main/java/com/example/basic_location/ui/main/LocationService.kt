package com.example.basic_location.ui.main

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Criteria
import android.location.LocationListener
import android.location.LocationManager
import android.os.Binder
import android.os.IBinder
import com.example.basic_location.ui.main.database.LocationServiceListener
import timber.log.Timber
import java.io.*
import java.util.*
import java.util.concurrent.TimeUnit


class LocationService() : Service() {

    private lateinit var locationManager: LocationManager
    private var minTime: Long = TimeUnit.SECONDS.toMillis(10)
    private var minDistance: Float = 500f

    private val binder = LocalBinder()


    private lateinit var listener : LocationListener
    private lateinit var criteria : Criteria


    override fun onCreate() {
        super.onCreate()
        Timber.d("onCreate")
        locationManager =
            applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        listener = LocationServiceListener(this)
        criteria = Criteria()
        criteria.accuracy = Criteria.ACCURACY_MEDIUM
        criteria.powerRequirement = Criteria.POWER_MEDIUM
    }

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
       Timber.d("onStartCommand")
        val test  = locationManager.getProviders(true)
        val test2 = locationManager.getProviders(false)
        val prov = locationManager.getBestProvider(criteria, true)
        val prov2 = locationManager.getBestProvider(criteria, false)
//        locationManager.requestLocationUpdates(minTime, minDistance, criteria, listener, Looper.myLooper())
        locationManager.requestLocationUpdates(prov, minTime, minDistance, listener)


//       locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, listener)
//       locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, listener)
//       locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, minTime, minDistance, listener)
        return START_STICKY
    }

    override fun onDestroy() {
       Timber.d("onDestroy")
        locationManager.removeUpdates(listener)
        stopSelf()
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    inner class LocalBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods
        fun getService(): LocationService = this@LocationService
    }

    fun getListener() : LocationListener{
        return listener
    }

    fun setListener(locationServiceListener: LocationListener){
        listener = locationServiceListener
    }
}