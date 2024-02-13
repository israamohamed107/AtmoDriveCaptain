package com.israa.atmodrivecaptain.home.presentation.fragments

import android.Manifest
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.NavHostFragment
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.israa.atmodrivecaptain.R
import com.israa.atmodrivecaptain.auth.presentation.AuthActivity
import com.israa.atmodrivecaptain.databinding.FragmentHomeBinding
import com.israa.atmodrivecaptain.home.data.models.TripDetails
import com.israa.atmodrivecaptain.home.presentation.services.LocationService
import com.israa.atmodrivecaptain.home.presentation.viewmodel.HomeViewModel
import com.israa.atmodrivecaptain.utils.ACCEPTED
import com.israa.atmodrivecaptain.utils.ACTION_CURRENT_LOCATION
import com.israa.atmodrivecaptain.utils.ARRIVED
import com.israa.atmodrivecaptain.utils.AnimationUtils
import com.israa.atmodrivecaptain.utils.LocationHelper
import com.israa.atmodrivecaptain.utils.MapUtils
import com.israa.atmodrivecaptain.utils.MySharedPreference
import com.israa.atmodrivecaptain.utils.NEW
import com.israa.atmodrivecaptain.utils.ON_WAY
import com.israa.atmodrivecaptain.utils.PAY
import com.israa.atmodrivecaptain.utils.Progressbar
import com.israa.atmodrivecaptain.utils.START_TRIP_
import com.israa.atmodrivecaptain.utils.UiState
import com.israa.atmodrivecaptain.utils.getParcelable
import com.israa.atmodrivecaptain.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import java.util.Timer
import kotlin.concurrent.schedule


@AndroidEntryPoint
class HomeFragment : Fragment(), OnMapReadyCallback {


    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var mMap: GoogleMap

    private var mLocationRequest: LocationRequest? = null
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null

    private var marker: Marker? = null
    private var pickupMarker: Marker? = null
    private var dropOffMarker: Marker? = null

    private var currentLocation: LatLng? = null
    private var pickupLocation: LatLng? = null
    private var dropOffLocation: LatLng? = null
    private var previousLatLng: LatLng? = null
    private lateinit var latLng: LatLng
    private var valueAnimator: ValueAnimator? = null

    private var mBackPressed: Long = 0

    private val homeViewModel: HomeViewModel by activityViewModels()

    private var myNavHostFragment: NavHostFragment? = null

    private lateinit var receiver: BroadcastReceiver

    private lateinit var baseBottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var baseBottomSheetView: ConstraintLayout

    private var tripId: Long = 0L

    private var tripStatus = ""
    private var flag = true
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        myNavHostFragment =
            childFragmentManager.findFragmentById(R.id.bottom_sheet_frag_container) as NavHostFragment

        initLocation()
        initViews()

        //init availability
        availability(MySharedPreference.getBoolean(MySharedPreference.PreferencesKeys.AVAILABILITY))
        if (MySharedPreference.getBoolean(MySharedPreference.PreferencesKeys.AVAILABILITY)) {
            homeViewModel.observeOnTripId()
        }
        onClick()
//        registerBroadCastReceiver()
    }


    private fun unRegisterBroadCastReceiver(){
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(receiver)
    }

    private fun initViews() {

        // init bottom sheet
        baseBottomSheetView = binding.bottomSheetId.baseBottomSheet
        baseBottomSheetBehavior = BottomSheetBehavior.from(baseBottomSheetView)
        baseBottomSheetBehavior.isDraggable = false
        baseBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

    }


    private fun onClick() {
        binding.apply {
            btnToggle.setOnCheckedChangeListener { _, _ ->
                currentLocation?.let { homeViewModel.updateAvailability(it) }
            }

            btnLogout.setOnClickListener {
                homeViewModel.logout()
                startActivity(Intent(requireActivity(),AuthActivity::class.java))
                requireActivity().finish()
                stopService()
            }
        }
    }

    private fun observe() {
        Log.e("getCaptainLocation", "observe: ", )
        viewLifecycleOwner.lifecycleScope.launchWhenStarted{

            homeViewModel.currentLocation.observe(viewLifecycleOwner){
                if(it!=null)
                    currentLocation = it
            }

            homeViewModel.isCaptainAvailable.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is UiState.Failure -> {
                        Progressbar.dismiss()
                        showToast(response.message)
                    }

                    UiState.Loading -> {
                        Progressbar.show(requireActivity())
                    }

                    is UiState.Success -> {
                        Progressbar.dismiss()
                        val available = response.data as Boolean
                        availability(available)
                        getCaptainLocation()
                        if (available){
                            homeViewModel.observeOnTripId()
                        }
                        else{
                            try {
                                homeViewModel.removeObserverOnTripId()
                            }catch (e:Exception){

                            }
                        }
                    }
                    else ->{

                    }
                }
            }

            homeViewModel.tripId.observe(viewLifecycleOwner) { tripId_ ->
                when (tripId_) {
                    is UiState.Failure -> {
                        unLockAvailability(true)
                    }

                    UiState.Loading -> {}
                    is UiState.Success -> {
                        if (tripId_.data as Long == 0L) {
                            if (tripId != 0L) {
                                homeViewModel.removeObserverOnTripStatus(tripId)
                                homeViewModel.resetTripDetails()
                                homeViewModel.resetTripStatus()
                                if(tripStatus.isNotEmpty() && tripStatus != NEW)
                                    stopService()
                            }
                            tripId = 0L
                            getCaptainLocation()

                        } else {
                            tripId = tripId_.data
                            Timer().schedule(2000){
                                homeViewModel.getTripDetails(tripId)
                            }
//                            homeViewModel.getTripDetails(tripId)
                        }
                    }

                    else -> {
                    }
                }
            }
            homeViewModel.tripDetails.observe(viewLifecycleOwner) { tripData ->
                when (tripData) {
                    is UiState.Failure -> {
                        showToast(tripData.message)
                    }

                    UiState.Loading -> {
                    }

                    is UiState.Success -> {
                        val data = tripData.data as TripDetails
                        pickupLocation = LatLng(data.pickup_lat.toDouble(),data.pickup_lng.toDouble())
                        dropOffLocation = LatLng(data.dropoff_lat.toDouble(),data.dropoff_lng.toDouble())
                        homeViewModel.observeOnTripStatus(tripId.toString())
                    }

                    else -> {}
                }
            }
            homeViewModel.tripStatus.observe(viewLifecycleOwner) { status ->
                when (status) {
                    is UiState.Failure -> {

                    }

                    UiState.Loading -> {
                    }

                    is UiState.Success -> {
                        when (status.data) {
//                            NEW, ACCEPTED, ON_WAY -> {
//                                if (!(NEW_REQUEST_LIST.contains(tripStatus) && NEW_REQUEST_LIST.contains(
//                                        status.data as String
//                                    ))
//                                ){
//                                    setMyNavHostFragmentGraph(R.navigation.nav_new_trip)
//                                    pickupLocation?.let {
//                                        addPickUpMarker(it)
//                                        cameraBounds(currentLocation!!,it)
//                                    }
//                                }
//                            }
//
//                            ARRIVED, START_TRIP_ -> {
//                                if (!(CURRENT_TRIP_LIST.contains(tripStatus) && CURRENT_TRIP_LIST.contains(
//                                        status.data as String
//                                    ))
//                                )
//                                    setMyNavHostFragmentGraph(R.navigation.nav_current_trip)
//
//                                if(status.data == ARRIVED) {
//                                    addPickUpMarker(pickupLocation!!)
//                                    cameraBounds(currentLocation!!, pickupLocation!!)
//                                }
//
//                                else {
//                                    addDropOffMarkerOnMap(dropOffLocation!!)
//                                    pickupMarker?.remove()
//                                    cameraBounds(currentLocation!!,dropOffLocation!!)
//
//                                }
//                            }
                            NEW, ACCEPTED, ON_WAY -> {
                                    setMyNavHostFragmentGraph(R.navigation.nav_new_trip)
                                    pickupLocation?.let {
                                        addPickUpMarker(it)
                                        cameraBounds(currentLocation!!,it)
                                    }
                            }

                            ARRIVED, START_TRIP_ -> {
                                setMyNavHostFragmentGraph(R.navigation.nav_current_trip)

                                if(status.data == ARRIVED) {
                                    addPickUpMarker(pickupLocation!!)
                                    cameraBounds(currentLocation!!, pickupLocation!!)
                                }

                                else {
                                    addDropOffMarkerOnMap(dropOffLocation!!)
                                    pickupMarker?.remove()
                                    cameraBounds(currentLocation!!,dropOffLocation!!)

                                }
                            }

                            PAY -> {
                                if (tripStatus != PAY){
                                    setMyNavHostFragmentGraph(R.navigation.nav_confirm_payment)
                                    addDropOffMarkerOnMap(dropOffLocation!!)
                                    addCarMarker(dropOffLocation!!)
                                    pickupMarker?.remove()
                                }
                            }

                            else -> {
                                baseBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                                flag = true
                                homeViewModel.resetTripCost()
                                unLockAvailability(true)
                                clearMap()

                            }
                        }
                        tripStatus = status.data as String
                        if(tripStatus.isNotEmpty() && tripStatus != NEW)
                            //TODO update location in trip node
                            startService(tripId)

                        if (flag && tripStatus.isNotEmpty()) {
                            unLockAvailability(false)
                            baseBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                            flag = false
                        }
                    }

                    else -> {

                    }
                }

            }


        }


    }



    private fun clearMap() {
        dropOffLocation = null
        pickupLocation = null
        pickupMarker?.remove()
        dropOffMarker?.remove()
        pickupMarker = null
        dropOffMarker = null
        moveToCurrentLocation(currentLocation!!)
    }

    private fun cameraBounds(firstLocation: LatLng, secondLocation: LatLng) {
        val builder = LatLngBounds.Builder()
        builder.include(firstLocation)
        builder.include(secondLocation)
        //bounds for camera
        val bounds = builder.build()
        val width = resources.displayMetrics.widthPixels
        val height = resources.displayMetrics.heightPixels
        val padding = (width * 0.15).toInt() // offset from edges of the map 15% of screen
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding))
    }
    private fun setMyNavHostFragmentGraph(navGraphId: Int) {
        val inflater = myNavHostFragment?.navController?.navInflater
        val graph = inflater?.inflate(navGraphId)
        myNavHostFragment?.navController?.graph = graph!!

    }

    private fun unLockAvailability(flag: Boolean) {
        binding.txtAvailability.isVisible = flag
        binding.btnToggle.isVisible = flag
    }

    private fun availability(isAvailable: Boolean) {
        binding.apply {
            if (isAvailable) {
                txtAvailability.setBackgroundColor(
                    resources.getColor(
                        R.color.backgroundColor,
                        null
                    )
                )
                txtAvailability.text = resources.getString(R.string.you_are_online)
                btnToggle.isChecked = true
            } else {
                txtAvailability.setBackgroundColor(resources.getColor(R.color.red, null))
                txtAvailability.text = resources.getString(R.string.you_are_offline)
                btnToggle.isChecked = false

            }

        }
    }

    private fun initLocation() {
        if(mFusedLocationProviderClient == null)
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        if (mLocationRequest == null){
            mLocationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(3000) //FastestInterval
                .setMaxUpdateDelayMillis(3000) //locationMaxWaitTime
                .setMinUpdateDistanceMeters(5f)
                .build()
        }

//        setSmallestDisplacement(5f)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            val success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(), R.raw.map_style
                )
            )
            if (!success) {
                Log.e("TAG", "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e("TAG", "Can't find style. Error: ", e)
        }
        mMap = googleMap

        checkPermissions()

    }

    private fun checkPermissions() {

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED
        ) {

            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ), 1
            );
        } else
            locationChecker()
    }


    @SuppressLint("MissingPermission")
    private fun getCaptainLocation() {
        startService(tripId)
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.let { intent ->
                        currentLocation = intent.getParcelable("location")?.let { location ->
                            LatLng(location.latitude,location.longitude)
                        }?.also {
                            homeViewModel.setCurrentLocation(it)
                            if(tripStatus.isNotEmpty() && tripStatus != NEW){
                                updateCarLocation(it)
                            }else{
                                addCarMarker(it)
                                moveToCurrentLocation(it)
                            }
                        }
                }
            }
        }
    LocalBroadcastManager.getInstance(requireContext()).registerReceiver(receiver,
        IntentFilter(ACTION_CURRENT_LOCATION)
    )

    }

    private fun startService(tripId:Long){
        val intent = Intent(requireContext(),LocationService::class.java)
        intent.putExtra("trip_id",tripId)
        intent.action = LocationService.Actions.ACTION_START.toString()
        requireActivity().startService(intent)
    }

    private fun stopService(){
        val intent = Intent(requireContext(),LocationService::class.java)
        intent.action = LocationService.Actions.ACTION_STOP.toString()
        requireActivity().startService(intent)
    }

    private fun addPickUpMarker(pickUpLocation: LatLng) {
        if (pickupMarker == null) {
            //create a new marker
            val markerOption = MarkerOptions()
            markerOption.apply {
                position(pickUpLocation)
                title(homeViewModel.getAddress(pickUpLocation,requireContext()))
                anchor(.5f, .5f)
                //set custom icon
                icon(bitmapFromVector(requireContext(), R.drawable.pickup_marker))

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pickUpLocation, 16f))

            }
            //add marker
            pickupMarker = mMap.addMarker(markerOption)//

        } else {
            //update the current marker
            pickupMarker!!.apply {
                position = pickUpLocation
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pickUpLocation, 16f))
            }
        }
    }

    private fun addDropOffMarkerOnMap(dropOffLocation: LatLng) {
        if (dropOffMarker == null) {
            //create a new marker
            val markerOption = MarkerOptions()
            markerOption.apply {
                position(dropOffLocation)
                title(homeViewModel.getAddress(dropOffLocation,requireContext()))
                anchor(.5f, .5f)
                //set custom icon
                icon(bitmapFromVector(requireContext(), R.drawable.dropoff_marker))

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dropOffLocation, 16f))

            }
            //add marker
            dropOffMarker = mMap.addMarker(markerOption)//

        } else {
            //update the current marker
            dropOffMarker!!.apply {
                position = dropOffLocation
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dropOffLocation, 16f))
            }
        }


    }
    private fun addCarMarker(currentLocation: LatLng) {
        if (marker == null) {
            //create a new marker
            val markerOption = MarkerOptions()
            markerOption.apply {
                position(currentLocation)
                flat(true)
                icon(bitmapFromVector(requireContext(), R.drawable.car))
            }
            marker = mMap.addMarker(markerOption)

        } else {
            //update the current marker
            marker!!.apply {
                position = currentLocation
            }
        }

    }
    private fun updateCarLocation(latLng: LatLng) {
        val a = System.currentTimeMillis() - mBackPressed
        if (a < 2700)
            return

        mBackPressed = System.currentTimeMillis()
        if (marker == null)
            addCarMarker(latLng)

        if (previousLatLng == null) {
            currentLocation = latLng
            previousLatLng = latLng
            marker?.position = currentLocation!!
            marker?.setAnchor(0.5f, 0.5f)

        } else {
            // animateCameraInTrip(currentLatLng!!, previousLatLng!!)
            previousLatLng = currentLocation

            animateCameraInTrip(latLng, previousLatLng!!)
            valueAnimator = AnimationUtils.carAnimator()

            valueAnimator?.addUpdateListener { va ->
                currentLocation = latLng
                if (previousLatLng != null) {

                    val multiplier = va.animatedFraction
                    val nextLocation = LatLng(
                        multiplier * currentLocation!!.latitude + (1 - multiplier) * previousLatLng!!.latitude,
                        multiplier * currentLocation!!.longitude + (1 - multiplier) * previousLatLng!!.longitude
                    )
                    marker?.position = nextLocation
                    val rotation = MapUtils.getRotation(previousLatLng!!, nextLocation)
                    if (!rotation.isNaN()) {
                        marker?.rotation = rotation
                    }
                    marker?.setAnchor(0.5f, 0.5f)
                }
            }
            valueAnimator?.start()
        }
        if(tripStatus == START_TRIP_)
            animateCameraInTrip(currentLocation!!, previousLatLng!!)
        else
            moveToCurrentLocation(latLng)

    }

    private fun animateCameraInTrip(latLng: LatLng, previous: LatLng) {

        val cameraPosition = CameraPosition.Builder()
            .bearing(LocationHelper.getBearing(previous, latLng))
            .target(latLng).tilt(45f).zoom(18f).build()
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

    }
    private fun moveToCurrentLocation(latlng: LatLng) {

        val camPos = CameraPosition.builder()
            .target(latlng)
            .zoom(18f)
            .build()

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos))
    }

    private fun locationChecker() {

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(mLocationRequest!!)

        val result: Task<LocationSettingsResponse> =
            LocationServices.getSettingsClient(requireContext())
                .checkLocationSettings(builder.build())

        result.addOnCompleteListener { task ->

            try {
                task.getResult(ApiException::class.java)
                getCaptainLocation()
                observe()

            } catch (exception: ApiException) {

                when (exception.statusCode) {

                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        try {

                            val resolve = exception as ResolvableApiException
                            startIntentSenderForResult(
                                resolve.resolution.intentSender,
                                Priority.PRIORITY_HIGH_ACCURACY,
                                null,
                                0,
                                0,
                                0,
                                null
                            )

                        } catch (e: Exception) {
                        }

                    }

                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {

                    }

                }
            }

        }

    }


    private fun bitmapFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        //drawable generator
        var vectorDrawable: Drawable = ContextCompat.getDrawable(context, vectorResId)!!
        vectorDrawable.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )
        //bitmap generator
        var bitmap: Bitmap =
            Bitmap.createBitmap(
                vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
        //canvas generator
        //pass bitmap in canvas constructor
        var canvas: Canvas = Canvas(bitmap)
        //pass canvas in drawable
        vectorDrawable.draw(canvas)
        //return BitmapDescriptorFactory
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unRegisterBroadCastReceiver()
        try {
            if (tripId != 0L)
                homeViewModel.removeObserverOnTripStatus(tripId)

            if(MySharedPreference.getBoolean(MySharedPreference.PreferencesKeys.AVAILABILITY))
                homeViewModel.removeObserverOnTripId()

        }catch (e:Exception){
        }
        clearMap()
        myNavHostFragment = null
        _binding = null

//        unRegisterBroadCastReceiver()

    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            Priority.PRIORITY_HIGH_ACCURACY -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        getCaptainLocation()
                        observe()
                    }

                    Activity.RESULT_CANCELED -> {
                        locationChecker()
                    }
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && permissions.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            locationChecker()
        }
    }

}

