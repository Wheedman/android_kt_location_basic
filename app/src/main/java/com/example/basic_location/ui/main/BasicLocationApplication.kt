package com.example.basic_location.ui.main

import android.app.Application
import com.example.basic_location.ui.main.database.BasicLocationDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import timber.log.Timber

class BasicLocationApplication : Application(){
//    private val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy {BasicLocationDatabase.getDatabase(this)}
    val dao by lazy {database.meteoLocationDao()}
    val repository by lazy { LocationRepository(dao)}


    override fun onCreate() {
        super.onCreate()
        //if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
       // } else {
            //Timber.plant(CrashReportingTree())
            //Fabric.with(this, Crashlytics())

            //FirebaseAnalytics.getInstance(this)
       // }
    }
}