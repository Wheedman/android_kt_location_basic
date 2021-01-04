package com.example.basic_location.network

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import timber.log.Timber

class UploadWorker(appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams) {


    override fun doWork(): Result {

        val lat = inputData.getDouble("LAT", Double.NaN)
        val long = inputData.getDouble("LON", Double.NaN)
        val acc = inputData.getFloat("ACC", Float.NaN)

        Timber.d("Uploaded $lat $long $acc")
        return Result.success()
    }

}