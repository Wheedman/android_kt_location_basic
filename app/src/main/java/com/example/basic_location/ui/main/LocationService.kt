package com.example.basic_location.ui.main

import android.Manifest
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import android.os.Parcel
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.basic_location.ui.main.database.MeteocoolLocation
import com.example.basic_location.ui.main.database.MeteocoolLocationDao
import kotlinx.coroutines.runBlocking
import org.jetbrains.anko.doAsync
import timber.log.Timber


class LocationService() : Service() {

    private lateinit var locationManager: LocationManager
    private val minTime: Long = 10
    private val minDistance: Float = 500f

    private lateinit var dao: MeteocoolLocationDao

    private lateinit var criteria : Criteria

    private val pendingIntent : PendingIntent
        get() {
            val intent = Intent(this, LocationUpdatesBroadcastReceiver::class.java)
            intent.action = LocationUpdatesBroadcastReceiver.ACTION_PROCESS_UPDATES
            return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

    override fun onCreate() {
        super.onCreate()
        Timber.d("onCreate")
        locationManager =
            applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        dao = (application as BasicLocationApplication).dao

        criteria = Criteria()
        criteria.accuracy = Criteria.ACCURACY_LOW
    }



    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
       Timber.d("onStartCommand")
//        val mLocationListeners = arrayOf(
//            LocationListener(LocationManager.GPS_PROVIDER),
////            LocationListener(LocationManager.NETWORK_PROVIDER)
//        )

        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            //locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER,)
//            locationManager.requestLocationUpdates(
//                LocationManager.GPS_PROVIDER,
//                minTime,
//                minDistance,
//                pendingIntent
//            )
            val provider = locationManager.getBestProvider(criteria, true)
            locationManager.requestLocationUpdates(provider, minTime, minDistance, pendingIntent)
        }
//            locationManager.requestLocationUpdates(
//                LocationManager.NETWORK_PROVIDER,
//                minTime,
//                minDistance,
//                mLocationListeners[1]
//            )
        return START_STICKY
    }

    override fun onDestroy() {
       Timber.d("onDestroy")
    }

    override fun onUnbind(intent: Intent?): Boolean {
       Timber.d("onUnbind")
        return super.onUnbind(intent)

    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
       Timber.d("onRebind")

    }

    override fun onBind(intent: Intent?): IBinder? {
       Timber.d("onBind")
        return null
    }

//    inner class LocationListener(provider: String) : android.location.LocationListener {
//        private val pro = provider
//        override fun onLocationChanged(location: Location) {
//
//            Timber.e("onLocationChanged: $pro $location")
//            val intent = Intent(applicationContext, LocationUpdatesBroadcastReceiver::class.java)
//           intent.putExtra("LOCATION", location)
//            PendingIntent.getBroadcast(applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT).send()
//
//            doAsync {
//                dao.updateLocation(
//                    MeteocoolLocation(
//                        1,
//                        location.longitude,
//                        location.latitude,
//                        location.altitude
//                    )
//                )
//            }
////            getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE).edit()
////                .putString("LONG", location.longitude.toString())
////                .putString("LAT", location.latitude.toString())
////                .putString("ALT", location.altitude.toString())
////                .apply()
//        }
//
//        override fun onProviderDisabled(provider: String) {
//            Timber.e("onProviderDisabled: $provider")
//        }
//
//        override fun onProviderEnabled(provider: String) {
//            Timber.e("onProviderEnabled: $provider")
//        }
//
//        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
//            Timber.e("onStatusChanged: $provider")
//        }
//
//        init {
//            Timber.e("LocationListener $provider")
//            val lastLocation = Location(provider)
//            lastLocation.time
//            doAsync{
//            if(!dao.isExists()) {
//                    dao.insertLocation(
//                        MeteocoolLocation(
//                            1,
//                            lastLocation.longitude,
//                            lastLocation.latitude,
//                            lastLocation.altitude
//                        )
//                    )
//                }
//            }
//        }
//    }

    companion object {
        val TAG = LocationService::class.simpleName
    }


}