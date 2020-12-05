package com.example.basic_location.ui.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.util.Log
import timber.log.Timber
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class LocationUpdatesBroadcastReceiver : BroadcastReceiver(){

    companion object {
        internal const val ACTION_PROCESS_UPDATES = "com.example.basic_location.action" + ".PROCESS_UPDATES"
    }

    override fun onReceive(context: Context, intent: Intent?) {
        Timber.d("onReceive")
        if (intent != null) {
            val action = intent.action
            val test = intent
            val b = intent.data
            if(hasResult(intent)){
                val result = intent.extras?.getParcelable<Location>("LOCATION")
                val c = intent.extras?.getParcelable<Location>("LOCATION")
                //writeToSDFile(result.toString())
            }
            Timber.d("$intent")
            if (ACTION_PROCESS_UPDATES == action) {
//                val result = LocationResult.extractResult(intent)
//                if (result != null) {
//                    val location = result.lastLocation
//                    val lastLocation = LocationUtils.getSavedLocationResult(context)
//                    val isDistanceBiggerThan500F = LocationUtils.getDistanceToLastLocation(location, context) > 499f
//                       if(isDistanceBiggerThan500F){
//                            Timber.i("$isDistanceBiggerThan500F")
//                            Timber.i("$location is better than $lastLocation")
//                            val preferences = context.defaultSharedPreferences
//                           val token = preferences.getString("fb_token", "no token")
//                           Timber.d(" Token $token")
//                           Timber.d("$location Upload")
//                           //UploadLocation().execute(location, token)
//                        }else{
//
//                            Timber.i("$location is not better than $lastLocation")
//                        }
                    // Save the location data to SharedPreferences.
                   // LocationUtils.saveResults(context.defaultSharedPreferences, location)
                    //Timber.i(LocationUtils.getSavedLocationResult(context).toString())
//                }
            }
        }
    }

//    private fun extractLocation(intent: Intent){
//        if(!hasResult(intent))
//            return null
//        !hasResult(var0) ? null : (LocationResult)var0.getExtras().getParcelable("com.google.android.gms.location.EXTRA_LOCATION_RESULT");
//    }
//
    private fun hasResult(intent : Intent?) : Boolean{
        if(intent == null){
            return false
        }
        return intent.hasExtra("location");
    }

    fun writeToSDFile(log : String) {
        // Find the root of the external storage.
        // See http://developer.android.com/guide/topics/data/data-  storage.html#filesExternal

        val root = android.os.Environment.getExternalStorageDirectory()
        Timber.d("nExternal file system root: $root")

        // See http://stackoverflow.com/questions/3551821/android-write-to-sd-card-folder

        val dir = File(root.absolutePath + "/meteocoolTest")
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
            Timber.d("******* File not found. Did you" + " add a WRITE_EXTERNAL_STORAGE permission to the   manifest?")
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
