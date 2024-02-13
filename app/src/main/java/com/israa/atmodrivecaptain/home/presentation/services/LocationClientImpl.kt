package com.israa.atmodrivecaptain.home.presentation.services

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class LocationClientImpl @Inject constructor(
    private val context: Context,
    private val fusedLocationProviderClient: FusedLocationProviderClient
):LocationClient {
    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(interval: Long): Flow<Location> {
        return callbackFlow {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if(!isLocationEnabled && !isNetworkEnabled){
                throw LocationClient.LocationException("GPS is disabled")
            }

            val mLocationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(interval) //FastestInterval
                .setMaxUpdateDelayMillis(interval) //locationMaxWaitTime
                .setMinUpdateDistanceMeters(5f)
                .build()

            val mLocationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    result.lastLocation?.let {
                        launch { send(it) }
                    }
                }
            }
            fusedLocationProviderClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.getMainLooper()
            )

            awaitClose {
                fusedLocationProviderClient.removeLocationUpdates(mLocationCallback)
            }
        }
    }
}