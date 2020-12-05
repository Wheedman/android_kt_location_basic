package com.example.basic_location.ui.main

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class MeteocoolWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams){
    override fun doWork(): Result {
        return Result.success()
    }

}