package com.example.basic_location.ui.main.database

import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.basic_location.ui.main.LocationService
import timber.log.Timber
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


open class LocationServiceListener(private val locationService : LocationService) : LocationListener{
    override fun onLocationChanged(location: Location?) {
        Timber.d("onLocationChanged ${location?.provider}")
        if(location != null){
            Timber.d("$location")
            locationService.applicationContext.getSharedPreferences("Location", Context.MODE_PRIVATE).edit()
                .putString("lat", String.format("%.6f", location.latitude))
                .putString("lon", String.format("%.6f", location.longitude))
                .putString("acc", String.format("%.6f", location.accuracy))
                .apply()
            val logEntry = String.format("%s lat: %.6f lon: %.6f", getCurrentTime(), location.latitude, location.longitude)
            writeToSDFile(logEntry)
        }else{
            Timber.d("null")
        }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        Timber.d("onStatusChanged")
    }

    override fun onProviderEnabled(provider: String?) {
        Timber.d("onProviderEnabled $provider")
    }

    override fun onProviderDisabled(provider: String?) {
        Timber.d("onProviderDisabled $provider")
        locationService.stopSelf()
    }

    fun writeToSDFile(log : String) {
        // Find the root of the external storage.
        // See http://developer.android.com/guide/topics/data/data-  storage.html#filesExternal

        val root = ContextCompat.getExternalFilesDirs(locationService.applicationContext, null)
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
            Timber.d(                    "******* File not found. Did you" + " add a WRITE_EXTERNAL_STORAGE permission to the   manifest?"
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }
        Timber.d("$file written to storage")
    }

    fun getCurrentTime() : String{
        val cal = Calendar.getInstance()
        val date = cal.time
        val dateFormat = SimpleDateFormat("dd.MM HH:mm:ss ", Locale.GERMANY)
        return dateFormat.format(date)
    }
}