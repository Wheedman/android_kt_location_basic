package com.example.basic_location.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.basic_location.R
import com.example.basic_location.ui.main.database.MeteocoolLocation
import org.jetbrains.anko.doAsync


class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()

        val PERMISSION = Manifest.permission.ACCESS_COARSE_LOCATION
    }

//    private lateinit var locationObserver : Observer<MeteocoolLocation>

    private lateinit var myService : LocationService

    private val locationViewModel : MainViewModel by viewModels{
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
        myService = LocationService()
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 2)
        //myService.setDao((requireActivity().application as BasicLocationApplication).dao)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val startBtn = view.findViewById<Button>(R.id.startBtn)
        startBtn.setOnClickListener{
            when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    PERMISSION
                ) -> {
                    doAsync {
                        requireActivity().startService(
                            Intent(
                                requireContext(),
                                LocationService::class.java
                            )
                        )
                    }
                }
                else -> {
                    // You can directly ask for the permission.
                    // The registered ActivityResultCallback gets the result of this request.
                    requireActivity().stopService(Intent(requireContext(), LocationService::class.java))
                    ActivityCompat.requestPermissions(requireActivity(), arrayOf(PERMISSION), 2)
                }
            }
        }

        view.findViewById<Button>(R.id.pause).setOnClickListener {
            requireActivity().stopService(Intent(requireContext(), LocationService::class.java))
        }

//        locationObserver = Observer<MeteocoolLocation> {
//            val text = view.findViewById<View>(R.id.message) as TextView
//            text.text = String.format("%.6f, %.6f", it.latitude, it.longitude)
//        }
    }

    override fun onResume() {
        super.onResume()

        if (
            ContextCompat.checkSelfPermission(
                requireContext(),
                PERMISSION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            doAsync {
                requireActivity().startService(
                    Intent(
                        requireContext(),
                        LocationService::class.java
                    )
                )
            }
//            locationViewModel.currentLocation.observe(viewLifecycleOwner, locationObserver)
        }
    }

    override fun onPause() {
        super.onPause()
        //requireActivity().stopService(Intent(requireContext(), LocationService::class.java))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            2 -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    doAsync {
                        requireActivity().startService(
                            Intent(
                                requireContext(),
                                LocationService::class.java
                            )
                        )
                    }
//                    locationViewModel.currentLocation.observe(viewLifecycleOwner, locationObserver)
                } else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                    requireActivity().stopService(Intent(requireContext(), LocationService::class.java))
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }
}