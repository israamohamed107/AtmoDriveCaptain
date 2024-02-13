package com.israa.atmodrivecaptain.home.presentation.fragments.requests

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.model.LatLng
import com.israa.atmodrivecaptain.R
import com.israa.atmodrivecaptain.databinding.FragmentAcceptRequestBinding
import com.israa.atmodrivecaptain.home.data.models.TripDetails
import com.israa.atmodrivecaptain.home.presentation.viewmodel.HomeViewModel
import com.israa.atmodrivecaptain.utils.ARRIVED
import com.israa.atmodrivecaptain.utils.START_TRIP_
import com.israa.atmodrivecaptain.utils.UiState
import com.israa.atmodrivecaptain.utils.showToast
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AcceptedRequestFragment : Fragment() {

    private var _binding: FragmentAcceptRequestBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by activityViewModels()
    private lateinit var currentLocation: LatLng

    private lateinit var tripDetails:TripDetails

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAcceptRequestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observe()
        onClick()
    }

    private fun checkCallPermission() {

            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CALL_PHONE
                ) !=
                PackageManager.PERMISSION_GRANTED
            ){

                requestPermissions(
                   arrayOf(Manifest.permission.CALL_PHONE), 2
                )
            }else
                callPassenger()

    }


    private fun observe() {


        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            homeViewModel.tripDetails.observe(viewLifecycleOwner){ tripData->
                when (tripData) {
                    is UiState.Failure -> {
                        showToast(tripData.message)
                    }

                    UiState.Loading -> {}
                    is UiState.Success -> {
                        tripDetails = tripData.data as TripDetails
                        setTripData(tripDetails)
                    }
                    else->{}
                }
            }

            homeViewModel.currentLocation.observe(viewLifecycleOwner) { location ->
                location?.let { currentLocation = it }
            }
            homeViewModel.tripStatus.observe(viewLifecycleOwner) { tripStatus ->
                when (tripStatus) {
                    is UiState.Failure -> {
                        binding.progress.isVisible = false
                        showToast(tripStatus.message)
                        binding.btn.isEnabled = true
                    }

                    UiState.Loading -> {
                        binding.progress.isVisible = true
                    }

                    is UiState.Success -> {
                        binding.btn.isEnabled = true
                        binding.progress.isVisible = false

                        when (tripStatus.data) {

                            ARRIVED->{
                                binding.apply {
                                    btn.text = getString(R.string.start_trip)
                                    txtTitle.text = getString(R.string.waiting_for_passenger)
                                }
                            }
                            START_TRIP_ -> {
                                binding.apply {
                                    btn.text = getString(R.string.finish_trip)
                                    txtTitle.text = getString(R.string.trip_running)
                                }
                            }
                        }
                    }

                    else -> {}
                }

            }
        }

    }
    private fun onClick() {
        binding.apply {
            btn.setOnClickListener {
                when (btn.text) {
                    getString(R.string.start_trip) -> homeViewModel.startTrip(tripDetails.id)
                    getString(R.string.finish_trip) ->
                        homeViewModel.endTrip(
                        tripDetails.id,
                        currentLocation,
                        requireContext()
                    )
                }
                btn.isEnabled = false

            }

            imgDial.setOnClickListener {
                checkCallPermission()
            }
        }
    }

    private fun callPassenger() {
            val callIntent = Intent(Intent.ACTION_CALL)
            callIntent.data = Uri.parse("tel:${tripDetails.passenger.mobile}")
            startActivity(callIntent)
    }

    private fun setTripData(data: TripDetails) {
        binding.apply {
            txtTripPrice.text = "${data.cost} EGP"
            txtPassengerName.text = data.passenger.full_name
            txtPickup.text = data.pickup_location_name
            txtDestination.text = data.dropoff_location_name
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 2 && permissions.isNotEmpty()
            &&grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            callPassenger()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}