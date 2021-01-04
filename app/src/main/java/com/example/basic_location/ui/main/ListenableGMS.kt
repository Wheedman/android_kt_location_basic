package com.example.basic_location.ui.main

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.concurrent.futures.CallbackToFutureAdapter
import androidx.core.content.ContextCompat
import androidx.work.*
import com.google.common.util.concurrent.ListenableFuture
import timber.log.Timber
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class ListenableGMS(context: Context, params: WorkerParameters) :
    ListenableWorker(context, params) {

    private val criteria : Criteria = Criteria()
    private val locationManager: LocationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    init {
        criteria.accuracy = Criteria.ACCURACY_MEDIUM
        criteria.powerRequirement = Criteria.POWER_MEDIUM
    }


    override fun startWork(): ListenableFuture<Result> {
        return CallbackToFutureAdapter.getFuture {

            if (
                ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val listener = object : MyLocationListener() {
                    override fun onLocationChanged(location: Location?) {
                        super.onLocationChanged(location)
                        if(location != null){
                            val logEntry = String.format("%s lat: %.6f lon: %.6f", getCurrentTime(), location.latitude, location.longitude)
                           writeToSDFile(logEntry)
                            val uploadLocation: OneTimeWorkRequest =
                                OneTimeWorkRequestBuilder<UploadWorker>()
                                    .setInputData(convertLocationToData(location))
                                    .build()
                            WorkManager.getInstance(applicationContext)
                                .enqueue(uploadLocation)
                            it.set(Result.success())
                        }
                        else{
                            it.set(Result.retry())
                        }
                    }

                    override fun onProviderDisabled(provider: String?) {
                        super.onProviderDisabled(provider)
                        it.set(Result.failure())
                    }
                }
                val prov = locationManager.getBestProvider(criteria, true)
                if(prov == null){
                    it.set(Result.failure())
                }else {
                    locationManager.requestLocationUpdates(
                        prov,
                        10,
                        500f,
                        listener
                    )
                }
            } else {
                Result.failure()
            }
        }
    }

    open inner class MyLocationListener : LocationListener{
        override fun onLocationChanged(location: Location?) {
           Timber.d("onLocationChanged $location")
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            Timber.d("onStatusChanged $provider")
        }

        override fun onProviderEnabled(provider: String?) {
            Timber.d("onProviderEnabled $provider")
        }

        override fun onProviderDisabled(provider: String?) {
            Timber.d("onProviderDisabled $provider")
        }
    }



    private fun writeToSDFile(log: String) {
        // Find the root of the external storage.
        // See http://developer.android.com/guide/topics/data/data-  storage.html#filesExternal

        val root = ContextCompat.getExternalFilesDirs(applicationContext, null)
        Timber.d("nExternal file system root: $root")

        // See http://stackoverflow.com/questions/3551821/android-write-to-sd-card-folder

        val dir = File(root[0].absolutePath + "/meteocoolTest")
        dir.mkdirs()
        val file = File(dir, "meteocoolLog.txt")

        try {
            //BufferedWriter for performance, true to set append to file flag
            val buf = BufferedWriter(FileWriter(file, true))
            buf.append(log)
            buf.newLine()
            buf.close()

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            Timber.d(
                "******* File not found. Did you" + " add a WRITE_EXTERNAL_STORAGE permission to the   manifest?"
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }
        Timber.d("$file written to storage")
    }

    private fun getCurrentTime(): String {
        val cal = Calendar.getInstance()
        val date = cal.time
        val dateFormat = SimpleDateFormat("dd.MM HH:mm:ss ", Locale.GERMANY)
        return dateFormat.format(date)
    }

    private fun convertLocationToData(location: Location): Data {
        val builder = Data.Builder()
        builder.putDouble("LAT", location.latitude)
        builder.putDouble("LON", location.longitude)
        builder.putFloat("ACC", location.accuracy)
        return builder.build()
    }
}
