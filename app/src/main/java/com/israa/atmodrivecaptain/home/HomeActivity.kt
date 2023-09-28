package com.israa.atmodrivecaptain.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.israa.atmodrivecaptain.R
import com.israa.atmodrivecaptain.auth.AuthActivity
import com.israa.atmodrivecaptain.auth.domain.model.RegisterCaptain
import com.israa.atmodrivecaptain.databinding.ActivityHomeBinding
import com.israa.atmodrivecaptain.utils.MySharedPreference
import dagger.hilt.android.AndroidEntryPoint
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import androidx.lifecycle.lifecycleScope
import java.util.Locale


@AndroidEntryPoint
class HomeActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityHomeBinding

    private var mLocationRequest: LocationRequest? =null
    private var mLocationCallback:LocationCallback? =null
    private var mFusedLocationProviderClient:FusedLocationProviderClient? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        initLocation()
        onClick()
    }

    private fun onClick() {
        binding.apply {
            btnLogout.setOnClickListener {
                    MySharedPreference.setCaptainInfo(RegisterCaptain())
                    val intent = Intent(this@HomeActivity ,AuthActivity::class.java)
                    startActivity(intent)
                    finish()
            }
        }
    }


    private fun initLocation() {

        mFusedLocationProviderClient.let{
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        }

        mLocationRequest.let{
            mLocationRequest = LocationRequest.create()
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setInterval(2000)
                .setFastestInterval(2000)
                .setSmallestDisplacement(5f)
        }

    }
    @SuppressLint("MissingPermission")
    private fun getMyLocation() {
        mMap.isMyLocationEnabled = true
        mLocationCallback = object : LocationCallback(){
            override fun onLocationResult(result: LocationResult) {

                result.lastLocation?.let {
                    LatLng(
                        it.latitude,
                        it.longitude
                    )
                }?.let {
                    moveToCurrentLocation(
                        it
                    )
                    getAddress(it)
                }
            }
        }
        mFusedLocationProviderClient?.requestLocationUpdates(mLocationRequest!!,mLocationCallback!!,
            Looper.getMainLooper() )
    }

    private fun getAddress(latLng: LatLng) {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses: List<Address>?
        val address: Address?
        var addressText = ""

        addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

        addresses?.let {
            if (addresses.isNotEmpty()) {
                address = addresses[0]
                addressText = address.getAddressLine(0)
            } else{
                addressText = "its not appear"
            }
        }
        binding.txtPickup.text = addressText
    }

    private fun moveToCurrentLocation(latlng:LatLng){

        val camPos = CameraPosition.builder()
            .target(latlng).zoom(16f).build()

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos))
    }



    private fun checkPermissions(){

        if(ActivityCompat.checkSelfPermission(this , Manifest.permission.ACCESS_COARSE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED  &&
            ActivityCompat.checkSelfPermission(this , Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION ,
                Manifest.permission.ACCESS_FINE_LOCATION) , 1)
        }
        else
            getMyLocation()
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.apply {
            isMyLocationButtonEnabled = true
        }

        checkPermissions()

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==1 && permissions.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            getMyLocation()
        }
    }
}