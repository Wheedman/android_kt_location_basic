package com.example.basic_location.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.work.*
import com.example.basic_location.R
import timber.log.Timber
import java.util.concurrent.TimeUnit


class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()

        val PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION
    }

//    private lateinit var locationObserver : Observer<MeteocoolLocation>

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
//            text.text = String.format("%.6f, %.6f", it.latitude, it.longitude)
//        }
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
                    .enqueueUniquePeriodicWork("periodic", ExistingPeriodicWorkPolicy.REPLACE, uploadWorkRequest)

            }
        }
    }
}