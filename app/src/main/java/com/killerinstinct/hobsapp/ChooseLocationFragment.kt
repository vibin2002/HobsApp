package com.killerinstinct.hobsapp

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMapClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.killerinstinct.hobsapp.databinding.FragmentChooseLocationBinding

class ChooseLocationFragment : Fragment(), OnMapReadyCallback ,OnMapClickListener{

    lateinit var binding: FragmentChooseLocationBinding
    lateinit var gMap: GoogleMap
    private val args: ChooseLocationFragmentArgs by navArgs()
    var chosenLocation: LatLng? = null

    companion object {
        private const val LOCATION_REQ_CODE = 10001;
        private const val TAG = "MapsActivity"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChooseLocationBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query == null)
                    return false
                moveToSearchedLocation(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean = false

        })

        binding.confirmloc.setOnClickListener {
            if (chosenLocation == null)
                return@setOnClickListener
            val action = ChooseLocationFragmentDirections.actionChooseLocationFragmentToUserHiringFragment(
                args.workerName,
                args.workerDesignation,
                args.propic,
                args.workerId,
                chosenLocation!!.latitude.toFloat(),
                chosenLocation!!.longitude.toFloat(),
            )
            findNavController().navigate(action)
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap
        gMap.setOnMapClickListener(this)
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap.isMyLocationEnabled = true
        }

    }

    override fun onMapClick(latLng: LatLng) {
        Log.d(TAG, "onMapClick: $latLng")
        gMap.clear()
        gMap.addMarker(MarkerOptions().position(latLng).title("Your postion"))
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,16f))
        chosenLocation = latLng
    }

    override fun onStart() {
        super.onStart()
        when {
            PermissionUtils.isAccessFineLocationGranted(requireContext())
                    && PermissionUtils.isAccessCoarseLocationGranted(requireContext())
            -> {
                when {
                    PermissionUtils.isLocationEnabled(requireContext()) -> {
                        getLastLocation()
                    }
                    else -> {
                        PermissionUtils.showGPSNotEnabledDialog(requireContext())
                    }
                }
            }
            else -> {
                PermissionUtils.requestAccessFineLocationPermission(
                    requireActivity(),
                    LOCATION_REQ_CODE
                )
                PermissionUtils.requestAccessCoarseLocationPermission(
                    requireActivity(),
                    LOCATION_REQ_CODE
                )
            }
        }
    }

    private fun getLastLocation(){
        Toast.makeText(requireContext(),"getLastLocation",Toast.LENGTH_SHORT).show()
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                val loc = LatLng(location.latitude,location.longitude)
                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc,15f))
            }
        }

    }

    private fun moveToSearchedLocation(location: String) {
        gMap.clear()
        val geocoder = Geocoder(requireContext())
        try {
            val addresses = geocoder.getFromLocationName(location,1)
            if(addresses.isNotEmpty()){
                val address = addresses[0]
                val position = LatLng(address.latitude,address.longitude)
                gMap.addMarker(MarkerOptions().position(position).title(address.featureName))
                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position,15f))
            }
        } catch (e: Exception){
            e.printStackTrace()
        }
    }


}