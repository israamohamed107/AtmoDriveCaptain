package com.israa.atmodrivecaptain.home.presentation.fragments.requests

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.gms.maps.model.LatLng
import com.israa.atmodrivecaptain.databinding.FragmentFinishingTripBinding
import com.israa.atmodrivecaptain.home.data.models.TripDetails
import com.israa.atmodrivecaptain.home.data.models.TripStatusResponse
import com.israa.atmodrivecaptain.home.presentation.viewmodel.HomeViewModel
import com.israa.atmodrivecaptain.utils.UiState
import com.israa.atmodrivecaptain.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FinishingTripFragment : Fragment() {
    private var _binding: FragmentFinishingTripBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by activityViewModels()
    private var tripId = 0L

    private lateinit var currentLocation:LatLng
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFinishingTripBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe()
        onClick()
    }

    private fun observe() {
        homeViewModel.tripDetails.observe(viewLifecycleOwner) { tripData ->
            when (tripData) {
                is UiState.Failure -> {
                    binding.progress.isVisible = false

                    showToast(tripData.message)
                }

                UiState.Loading -> {
                    binding.progress.isVisible = true
                }
                is UiState.Success -> {
                    binding.progress.isVisible = false

                    val tripDetails = tripData.data as TripDetails
                    setTripData(tripDetails)
                }

                else -> {}
            }
        }

        homeViewModel.tripRealCost.observe(viewLifecycleOwner) {
            if(it != null)
                binding.txtRealCoset.text = it
        }
        homeViewModel.currentLocation.observe(viewLifecycleOwner){
            if(it != null)
                currentLocation = it
        }
        homeViewModel.tripId.observe(viewLifecycleOwner) { tripId_ ->
            when (tripId_) {
                is UiState.Failure -> {
                    showToast(tripId_.message)
                }
                UiState.Loading -> {}
                is UiState.Success -> {
                    if (tripId_.data != 0L) {
                        tripId = tripId_.data as Long
                    } else {

                    }
                }

                else -> {

                }
            }
        }

        homeViewModel.confirmPayment.observe(viewLifecycleOwner){
            when(it){
                is UiState.Failure ->{
                    showToast(it.message)
                }
                UiState.Loading ->{
                }
                is UiState.Success -> {
                    val data = it.data as TripStatusResponse
                    showToast(data.message)
                }
                null -> {
                }
            }
        }

    }

    private fun setTripData(tripDetails: TripDetails) {
        binding.apply {
            txtPassengerName.text = tripDetails.passenger.full_name
            txtEstimatedCost.text = tripDetails.cost
            if (txtRealCoset.text.isEmpty())
                txtEstimatedCost.text = tripDetails.cost
        }
    }

    private fun onClick() {
        binding.apply {
            btnConfirm.setOnClickListener {
                if (txtPaidMoney.text.isNotEmpty()){
                    homeViewModel.confirmPayment(tripId, txtPaidMoney.text.toString().toInt())
                }
            }
//            txtTitle.text = getString(R.string.finish_trip)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}