package com.example.basic_location.ui.main

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.basic_location.R


class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    private lateinit var locationObserver : Observer<Location>

    private var myService : LocationService = LocationService()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val startBtn = view.findViewById<Button>(R.id.startBtn)
        startBtn.setOnClickListener{
            when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) -> {
                    requireActivity().startService(Intent(requireContext(), LocationService::class.java))
                }
                else -> {
                    // You can directly ask for the permission.
                    // The registered ActivityResultCallback gets the result of this request.
                    requireActivity().stopService(Intent(requireContext(), LocationService::class.java))
                    ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 2)
                }
            }
        }

        view.findViewById<Button>(R.id.pause).setOnClickListener {
            requireActivity().stopService(Intent(requireContext(), LocationService::class.java))
        }

        locationObserver = Observer<Location> {
            val text = view.findViewById<View>(R.id.message) as TextView
            text.text = String.format("%.3f, %.3f", it.latitude, it.longitude)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, MainViewModelFactory(LocationRepository(myService, requireActivity().getSharedPreferences("Test", Context.MODE_PRIVATE)))).get(MainViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()

        if (
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            requireActivity().startService(Intent(requireContext(), LocationService::class.java))
            viewModel.currentLocation.observe(viewLifecycleOwner, locationObserver)
        }
    }

    override fun onPause() {
        super.onPause()
        requireActivity().stopService(Intent(requireContext(), LocationService::class.java))
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
                    requireActivity().startService(Intent(requireContext(), LocationService::class.java))
                    viewModel.currentLocation.observe(viewLifecycleOwner, locationObserver)
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