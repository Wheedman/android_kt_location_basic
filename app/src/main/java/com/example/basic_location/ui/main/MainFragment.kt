package com.example.basic_location.ui.main

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.provider.DocumentsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.work.*
import com.example.basic_location.R
import com.example.basic_location.ui.main.database.MeteocoolLocation
import com.google.gson.Gson
import org.jetbrains.anko.support.v4.defaultSharedPreferences
import timber.log.Timber
import java.util.concurrent.TimeUnit


class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()

        val PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION
    }

    private lateinit var mService: LocationService
    private var mBound: Boolean = false
    private val backgroundWorkName: String = "cycle"

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as LocationService.LocalBinder
            mService = binder.getService()
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    private lateinit var  serviceIntent : Intent


    private lateinit var locationObserver : Observer<MeteocoolLocation>
    private lateinit var locationSharedObserver : Observer<MeteocoolLocation>
    private lateinit var locationSharedStringObserver : Observer<String?>

    private val locationViewModel: MainViewModel by viewModels {
        MainViewModelFactory((requireActivity().application as BasicLocationApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 2)
//        myService.setDao((requireActivity().application as BasicLocationApplication).dao)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val startBtn = view.findViewById<Button>(R.id.startBtn)
        startBtn.setOnClickListener {
            requestPermissions(arrayOf(PERMISSION), 2)
        }

        view.findViewById<Button>(R.id.pause).setOnClickListener {
          val isFineEnabled = ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            val isCoarseEnabled = ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

            val isBackgroundEnabled = ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            val output = String.format("fine: %b , coarse: %b, background: %b", isFineEnabled, isCoarseEnabled, isBackgroundEnabled)
            Timber.d(output)
            view.findViewById<TextView>(R.id.message).text = output
        }

        view.findViewById<Button>(R.id.coarse).setOnClickListener {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 4)
        }
        view.findViewById<Button>(R.id.fine).setOnClickListener {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 5)
        }
        view.findViewById<Button>(R.id.background).setOnClickListener {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION), 6)
            }
        }

//        locationObserver = Observer<MeteocoolLocation> {
//            val text = view.findViewById<View>(R.id.message) as TextView
//            if(it !=null) {
//                text.text = String.format("%.6f, %.6f", it.latitude, it.longitude)
//            }else{
//                text.text = "Location not updated yet"
//            }
//        }
        locationSharedObserver = Observer<MeteocoolLocation> {
            val text = view.findViewById<View>(R.id.message) as TextView
            if(it !=null) {
                text.text = String.format("%.6f, %.6f", it.latitude, it.longitude)
            }else{
                text.text = "Location not updated yet (Shared)"
            }
        }
//        locationSharedStringObserver = Observer<String?> {
//            val text = view.findViewById<View>(R.id.message) as TextView
//            Timber.d(it)
////            text.text = it
//
//            if(it.isNotEmpty()) {
//                val location = Gson().fromJson(it, Location::class.java)
//                text.text = String.format("%.6f, %.6f", location.latitude, location.longitude)
//            }else{
//                text.text = "Location not updated yet (Shared Strig)"
//            }
//        }
       // locationViewModel.currentLocation.observe(viewLifecycleOwner, locationObserver)
        locationViewModel.currentSharedLocation.observe(viewLifecycleOwner, locationSharedObserver)
//        locationViewModel.currentSharedStringLocation.observe(viewLifecycleOwner, locationSharedStringObserver)
    }

    override fun onStart() {
        super.onStart()
        stopBackgroundWork(backgroundWorkName)
        serviceIntent = Intent(requireContext(), LocationService::class.java)
            serviceIntent.also { intent ->
                requireContext().bindService(intent, connection, Context.BIND_AUTO_CREATE)
            }
        if(ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {
            requireContext().startService(serviceIntent)
        }
    }

    override fun onStop() {
        super.onStop()
        requireContext().unbindService(connection)
        mBound = false
        if(ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED){
            startBackgroundWork(backgroundWorkName)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Timber.d("onRequestPermissionResult")
        if (requestCode == 2) {
            if ((grantResults.isNotEmpty() &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED)
            ) {
                Timber.d("granted")
                requireContext().startService(serviceIntent)
            }
        }
    }

    private fun startBackgroundWork(workName : String){
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val uploadWorkRequest: PeriodicWorkRequest =
            PeriodicWorkRequestBuilder<ListenableLocationUpdateWorker>(
                15, TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.LINEAR, 5,
                    TimeUnit.MINUTES
                )
                .build()

        WorkManager
            .getInstance(requireContext())
            .enqueueUniquePeriodicWork(workName, ExistingPeriodicWorkPolicy.REPLACE, uploadWorkRequest)
    }

    private fun stopBackgroundWork(workName : String){
        WorkManager
            .getInstance(requireContext())
            .cancelAllWorkByTag(workName)
    }
}