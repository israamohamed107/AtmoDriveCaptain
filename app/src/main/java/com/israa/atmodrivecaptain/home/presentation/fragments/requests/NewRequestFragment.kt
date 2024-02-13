package com.israa.atmodrivecaptain.home.presentation.fragments.requests

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.model.LatLng
import com.israa.atmodrivecaptain.R
import com.israa.atmodrivecaptain.databinding.FragmentNewRequestBinding
import com.israa.atmodrivecaptain.home.data.models.TripDetails
import com.israa.atmodrivecaptain.home.presentation.viewmodel.HomeViewModel
import com.israa.atmodrivecaptain.utils.ACCEPTED
import com.israa.atmodrivecaptain.utils.NEW
import com.israa.atmodrivecaptain.utils.ON_WAY
import com.israa.atmodrivecaptain.utils.UiState
import com.israa.atmodrivecaptain.utils.showToast
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class NewRequestFragment : Fragment() {

    private var _binding: FragmentNewRequestBinding? = null
    private val binding get() = _binding!!

    private  var counter:String? = null
    private val homeViewModel: HomeViewModel by activityViewModels()
//    private val newRequestViewModel: NewRequestViewModel by viewModels()

    private var tripId = 0L
    private lateinit var currentLocation:LatLng
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentNewRequestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observe()
        onClick()

    }

    private fun enableButtons(isEnable: Boolean) {
        binding.apply {
            btnAccept.isEnabled = isEnable
            btnReject.isEnabled = isEnable
        }
    }

    private fun observe() {

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {


            homeViewModel.currentLocation.observe(viewLifecycleOwner){
                it?.let { currentLocation = it}
            }
            homeViewModel.tripId.observe(viewLifecycleOwner) { tripId_ ->
                when (tripId_) {
                    is UiState.Failure -> {
//                        showToast(tripId_.message)
                    }

                    UiState.Loading -> {}
                    is UiState.Success -> {
                        if (tripId_.data != 0L) {
                            tripId = tripId_.data as Long
                        } else {
                        }
                    }

                    else -> {}
                }
            }


            homeViewModel.counter.observe(viewLifecycleOwner) {
                if (it.isNotEmpty()) {
                    binding.apply {
                        setCounter(it)
                    }
                }
            }

            homeViewModel.tripStatus.observe(viewLifecycleOwner) { tripStatus ->
                when (tripStatus) {
                    is UiState.Failure -> {
                        showToast(tripStatus.message)
                        enableButtons(true)
                        binding.progress.isVisible = false
                    }

                    UiState.Loading -> {
                        binding.progress.isVisible = true
                    }

                    is UiState.Success -> {
                        enableButtons(true)
                        binding.progress.isVisible = false
                        when (tripStatus.data) {
                            ACCEPTED -> {
                                goToPickupOrCancel()
                            }
                            ON_WAY -> {
                                arrivedOrCancel()
                            }
                             NEW ->{
                                acceptOrReject()
                                 if (counter == null)
                                     homeViewModel.startCounter()

                             }
                            else ->{
                            }

                        }
                    }

                    else -> {
//                        acceptOrReject()
//                        homeViewModel.startCounter()
                    }
                }

            }
        }


        homeViewModel.tripDetails.observe(viewLifecycleOwner){ tripData->
            Log.e("bcbccn", "new request frag getTripDetails:$tripId ", )
            when (tripData) {
                is UiState.Failure -> {
                    binding.progress.isVisible = false
//                        showToast(tripData.message)
                }

                UiState.Loading -> {
                    binding.progress.isVisible = true
                }
                is UiState.Success -> {
                    binding.progress.isVisible = false
                    val tripDetails = tripData.data as TripDetails
                    setTripData(tripDetails)
                }
                else->{
                    setTripData(null)
                }
            }
        }
    }

    private fun setCounter(value:String){
        binding.apply {
            counter = getString(R.string._00, value)
            txtTimer.text = counter
            progressBar.progress = 30 - value.toInt()
            if(progressBar.progress > 20)
                progressBar.progressTintList = ColorStateList.valueOf(Color.RED)
            else
                progressBar.progressTintList = ColorStateList.valueOf(resources.getColor(R.color.green,null))

            if(counter == "00:00")
                counter = null
        }
    }

    private fun setTripData(data: TripDetails?) {
        if(data != null){
            binding.apply {
                txtTripPrice.text = data.cost
                txtPassengerName.text = data.passenger.full_name
                txtPickup.text = data.pickup_location_name
                txtDestination.text = data.dropoff_location_name
            }
        }else{
            binding.apply {
                txtTripPrice.text = ""
                txtPassengerName.text = ""
                txtPickup.text = ""
                txtDestination.text = ""
            }
        }
    }

    private fun onClick() {
        binding.apply {
            btnReject.setOnClickListener {
                if (btnReject.text == getString(R.string.reject)) {
                    homeViewModel.rejectTrip()
//                    homeViewModel.stopCounter()
//                    counter = null
                }
                else {
                    homeViewModel.cancelTrip(tripId)
                }
                enableButtons(false)
            }
            btnAccept.setOnClickListener {
                if (btnAccept.text == getString(R.string.accept)) {
//                    homeViewModel.stopCounter()
                    currentLocation.let { homeViewModel.acceptTrip(tripId,it,requireContext()) }
                }
                else if (btnAccept.text == getString(R.string.arrived)){
                    homeViewModel.arrived(tripId)
                }
                else {
                   homeViewModel.goToPickup(tripId)
                }
                enableButtons(false)
            }
        }

    }

    private fun acceptOrReject() {
        binding.apply {
            txtTimer.isVisible = true
            progressBar.isVisible = true

            btnAccept.text = getString(R.string.accept)
            btnReject.text = getString(R.string.reject)

        }
    }

    private fun goToPickupOrCancel() {
        binding.apply {
            binding.txtTimer.isVisible = false
            progressBar.isVisible = false
            btnAccept.text = getString(R.string.on_way)
            btnReject.text = getString(R.string.cancel)

        }
    }

    private fun arrivedOrCancel() {
        binding.apply {
            binding.txtTimer.isVisible = false
            progressBar.isVisible = false
            btnAccept.text = getString(R.string.arrived)
            btnReject.text = getString(R.string.cancel)

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        counter = null
    }
}