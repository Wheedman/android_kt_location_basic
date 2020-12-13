package com.example.basic_location.ui.main

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.work.*
import timber.log.Timber

class LocationUpdatesBroadcastReceiver : BroadcastReceiver(){

    companion object {
        internal const val ACTION_PROCESS_UPDATES = "com.example.basic_location.action" + ".PROCESS_UPDATES"
    }

    override fun onReceive(context: Context, intent: Intent?) {
        Timber.d("onReceive")
        if (intent != null) {
            val action = intent.action
            Timber.d("$intent")
            if (ACTION_PROCESS_UPDATES == action && hasResult(intent)) {
                val result = intent.extras!!.get("location") as Location

                val constraint : Constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
                val uploadWorkRequest: WorkRequest =
                    OneTimeWorkRequestBuilder<UploadWorker>()
                        .setInputData(convertLocationToData(result))
                        .build()
                WorkManager
                    .getInstance(context)
                    .enqueue(uploadWorkRequest)
                }
            }
    }

    private fun hasResult(intent : Intent?) : Boolean{
        if(intent == null){
            return false
        }
        return intent.hasExtra("location");
    }

    private fun convertLocationToData(location : Location) : Data{
        val builder = Data.Builder()
        builder.putDouble("LAT", location.latitude)
        builder.putDouble("LONG", location.longitude)
        builder.putFloat("ACC", location.accuracy)
        return builder.build()
    }
}
