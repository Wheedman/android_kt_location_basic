package com.example.basic_location.app

import android.app.Application
import com.example.basic_location.storage.BasicLocationDatabase
import com.example.basic_location.ui.main.LocationRepository
import timber.log.Timber

class BasicLocationApplication : Application(){
//    private val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { BasicLocationDatabase.getDatabase(this)}
    val dao by lazy {database.meteoLocationDao()}
    val sharedPrefs by lazy {getSharedPreferences("Location", MODE_PRIVATE)}
    val repository by lazy { LocationRepository(dao, sharedPrefs) }


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