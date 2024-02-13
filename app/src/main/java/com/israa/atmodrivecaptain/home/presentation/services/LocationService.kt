package com.israa.atmodrivecaptain.home.presentation.services

import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DatabaseReference
import com.israa.atmodrivecaptain.R
import com.israa.atmodrivecaptain.utils.ACTION_CURRENT_LOCATION
import com.israa.atmodrivecaptain.utils.LAT
import com.israa.atmodrivecaptain.utils.LNG
import com.israa.atmodrivecaptain.utils.MySharedPreference
import com.israa.atmodrivecaptain.utils.ONLINE_CAPTAINS
import com.israa.atmodrivecaptain.utils.TRIPS
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class LocationService : Service() {

    private val TAG = "LocationService"
    private var serviceScope = CoroutineScope(Dispatchers.IO)
    private lateinit var locationClient: LocationClient
    private lateinit var latLng:LatLng
    private var tripId:Long = 0

    @Inject
    lateinit var ref: DatabaseReference


    enum class Actions{
        ACTION_STOP,
        ACTION_START
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        locationClient = LocationClientImpl(applicationContext,LocationServices.getFusedLocationProviderClient(applicationContext))
        Log.e(TAG, "service created..", )

    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        tripId = intent?.getLongExtra("trip_id",0) ?: 0
        when(intent?.action){
            // start service
            Actions.ACTION_START.toString() -> {
                tripId?.let {
                    start(it)
                } ?: start(0)
            }
            Actions.ACTION_STOP.toString() -> stop() // stop service
        }
        return START_REDELIVER_INTENT
    }

    // get captain location
    private fun start(tripId:Long){
        val notification = NotificationCompat.Builder(this,"location_updates")
            .setContentTitle("AtmoDrive")
            .setContentText("Tab to return to the app ...")
            .setSmallIcon(R.drawable.logo_dark)

        locationClient.getLocationUpdates(3000)
            .catch { e ->
                e.printStackTrace()
            }
            .onEach { location->
                // send location to home fragment
                // update captain location
                updateCurrentLocation(location)
                latLng = LatLng(location.latitude,location.longitude)

                if(MySharedPreference.getBoolean(MySharedPreference.PreferencesKeys.AVAILABILITY))
                    updateCaptainLocation(location.latitude,location.longitude)

                if(tripId != 0L){
                    //TODO update location in trip node
                    updateCaptainLocationInTripDetails(tripId.toString(), latLng)
                }
                Log.e(TAG, "service running..${location.latitude} - ${location.longitude}")

            }.launchIn(serviceScope)

        startForeground(1,notification.build())
    }

    //send current location to home fragment
    private fun updateCurrentLocation(location:Location) {
        Intent().also { intent ->
            intent.action = ACTION_CURRENT_LOCATION
            intent.putExtra("location",location)
            Handler(Looper.getMainLooper()).post {
                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
            }
        }
    }

    //update location in online captains
    private fun updateCaptainLocation(lat:Double , lng:Double) {
                ref.child(ONLINE_CAPTAINS)
                    .child(MySharedPreference.getString(MySharedPreference.PreferencesKeys.ID)!!)
                    .updateChildren(hashMapOf<String, Any>(LAT to lat))


                ref.child(ONLINE_CAPTAINS)
                    .child(MySharedPreference.getString(MySharedPreference.PreferencesKeys.ID)!!)
                    .updateChildren(hashMapOf<String, Any>(LNG to lng))

    }

    private fun updateCaptainLocationInTripDetails(tripId: String, latLng: LatLng) {
        //update on trip details

                ref.child(TRIPS).child(tripId)
                    .updateChildren(hashMapOf<String, Any>(LAT to latLng.latitude.toString()))
                    .addOnFailureListener { e ->

                    }

                ref.child(TRIPS).child(tripId)
                    .updateChildren(hashMapOf<String, Any>(LNG to latLng.longitude.toString()))
                    .addOnFailureListener { e ->
                    }
            }


    private fun stop(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            stopForeground(STOP_FOREGROUND_REMOVE)
        else
            @Suppress("DEPRECATION") stopForeground(true)
        stopSelf()
    }


    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)

        //TODO check if the captain is on trip before change his availability
        if(MySharedPreference.getBoolean(MySharedPreference.PreferencesKeys.AVAILABILITY) && tripId != 0L ){
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workerRequest = OneTimeWorkRequestBuilder<UpdateAvailabilityWorker>()
                .setInputData(
                    workDataOf(
                        LAT to latLng.latitude.toString(),
                        LNG to latLng.longitude.toString())
                )
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL,
                    15,
                    TimeUnit.MILLISECONDS)
                .build()
            Log.e("UpdateAvailabilityWorker", "onTaskRemoved: $latLng", )

            WorkManager.getInstance(this).enqueue(workerRequest)
        }
    }
    override fun onDestroy(){
        super.onDestroy()
        Log.e(TAG, "service destroyed..")
        serviceScope.cancel()
    }


}

