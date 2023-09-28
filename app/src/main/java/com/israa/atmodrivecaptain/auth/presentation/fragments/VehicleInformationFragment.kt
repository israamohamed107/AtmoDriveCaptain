package com.israa.atmodrivecaptain.auth.presentation.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentResultListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.israa.atmodrivecaptain.R
import com.israa.atmodrivecaptain.auth.data.model.UploadImageResponse
import com.israa.atmodrivecaptain.auth.domain.model.RegisterCaptain
import com.israa.atmodrivecaptain.auth.presentation.viewmodels.VehicleInfoViewModel.Companion.VEHICLE_LICENCE_FRONT_FLAG
import com.israa.atmodrivecaptain.auth.presentation.viewmodels.VehicleInfoViewModel.Companion.VEHICLE_LICENCE_BACK_FLAG
import com.israa.atmodrivecaptain.auth.presentation.viewmodels.VehicleInfoViewModel
import com.israa.atmodrivecaptain.databinding.FragmentVehicleInformationBinding
import com.israa.atmodrivecaptain.utils.Progressbar
import com.israa.atmodrivecaptain.utils.UiState
import com.israa.atmodrivecaptain.utils.exhaustive
import com.israa.atmodrivecaptain.utils.pickImage
import com.israa.atmodrivecaptain.utils.uploadImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.observeOn

@AndroidEntryPoint
class VehicleInformationFragment : Fragment(), PickCarImagesFragment.PickCarImagesListener {

     var pickCarImagesFragment: PickCarImagesFragment? = null
    private var _binding: FragmentVehicleInformationBinding? = null

    private val vehicleInfoViewModel: VehicleInfoViewModel by activityViewModels()
    private val binding get() = _binding!!

    var leftSidePath:String? = null
    var rightSidePath:String? = null
    var frontSidePath:String? = null
    var backSidePath:String? = null
    var frontSeatPath:String? = null
    var backSeatParh:String? = null

    var licenceFrontPath_: String? = null
    var licenceBackPath_: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentVehicleInformationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onClick()
        observeOnRegisterVehicle()
        observeOnLicenceImages()

    }

    private fun observeOnRegisterVehicle(){
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            vehicleInfoViewModel.registerVehicle.collect {
                when(it){
                    is UiState.Failure -> {
                        Progressbar.dismiss()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    UiState.Loading -> Progressbar.show(requireActivity())
                    is UiState.Success ->{
                        Toast.makeText(requireContext(),"Request Successful", Toast.LENGTH_SHORT).show()
                        Progressbar.dismiss()
                        navigate()
                    }
                }.exhaustive
            }
        }
    }
    private fun observeOnLicenceImages() {
        vehicleInfoViewModel.apply {
            licenceBackPath.observe(viewLifecycleOwner){
                when(it){
                    is UiState.Failure -> Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    UiState.Loading -> Progressbar.show(requireActivity())
                    is UiState.Success ->{
                        licenceBackPath_ = it.data as String
                        Progressbar.dismiss()
                    }
                    else -> {}
                }.exhaustive
            }

            licenceFrontPath.observe(viewLifecycleOwner){
                when(it){
                    is UiState.Failure -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        Progressbar.dismiss()
                    }
                    UiState.Loading -> Progressbar.show(requireActivity())
                    is UiState.Success ->{
                        licenceFrontPath_ = it.data as String
                        Progressbar.dismiss()
                    }
                    else -> {}
                }.exhaustive
            }
        }

    }

    private fun navigate() {
        findNavController().navigate(VehicleInformationFragmentDirections.
        actionVehicleInformationFragmentToBankAccountFragment())
    }


    private fun setCarImages() {
        
        vehicleInfoViewModel.apply {
            leftSideUri.observe(viewLifecycleOwner) { uri ->
                binding.apply {
                    imgCarSide1.setImageURI(uri)
                }
            }
            rightSideUri.observe(viewLifecycleOwner) { uri ->
                binding.apply {
                    imgCarSide2.setImageURI(uri)

                }
            }

            frontSideUri.observe(viewLifecycleOwner) { uri ->
                binding.apply {
                    imgCarSide3.setImageURI(uri)

                }
            }

            backSideUri.observe(viewLifecycleOwner) { uri ->
                binding.apply {
                    imgCarSide4.setImageURI(uri)

                }
            }

            topSideUri.observe(viewLifecycleOwner) { uri ->
                binding.apply {
                    imgCarSide5.setImageURI(uri)
                }
            }
            sideUri.observe(viewLifecycleOwner) { uri ->
                binding.apply {
                    imgCarSide6.setImageURI(uri)
                }

            }

        }

    }


    private fun onClick() {


        binding.apply {

            btnNext.setOnClickListener {
                registerVehicle()
            }
            imgInsertCarPhotos.setOnClickListener {
                showDialog()
            }

            imgInsertCarLicenceFront.setOnClickListener {
                pickImage(VEHICLE_LICENCE_FRONT_FLAG)
            }
            imgEditFront.setOnClickListener {
                pickImage(VEHICLE_LICENCE_FRONT_FLAG)
            }
            imgInsertCarLicenceBack.setOnClickListener {
                pickImage(VEHICLE_LICENCE_BACK_FLAG)
            }
            imgEditBack.setOnClickListener {
                pickImage(VEHICLE_LICENCE_BACK_FLAG)
            }
            imgEditCarImages.setOnClickListener {
                showDialog()
            }

        }
    }

    private fun registerVehicle() {
        vehicleInfoViewModel.registerVehicle(vehicleFront = frontSidePath, vehicleBack = backSidePath, vehicleLeft = leftSidePath,
            vehicleRight = rightSidePath, vehicleFrontSeat = frontSeatPath, vehicleBackSeat = backSeatParh
        , vehicleLicenseFront = licenceFrontPath_, vehicleLicenseBack = licenceBackPath_)
    }

    private fun showDialog() {

        if(pickCarImagesFragment == null){
            pickCarImagesFragment = PickCarImagesFragment(this)
            }
        val fragmentManager = (activity as FragmentActivity).supportFragmentManager

        fragmentManager.let { pickCarImagesFragment!!.show(it, "aaaaaaa")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == VEHICLE_LICENCE_FRONT_FLAG) {
                binding.apply {
                    imgLicenceFront.setImageURI(data?.data)
                    imgInsertCarLicenceFront.isVisible = false
                    txtAddCarLicenceFront.isVisible = false
                    imgDeleteFront.isVisible = true
                    imgEditFront.isVisible = true
                }

                viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                    val pair = uploadImage(data?.data)
                    vehicleInfoViewModel.uploadImage(pair.first, pair.second, VEHICLE_LICENCE_FRONT_FLAG)
                }
            } else if (requestCode == VEHICLE_LICENCE_BACK_FLAG) {
                binding.apply {
                    imgLicenceBack.setImageURI(data?.data)
                    imgInsertCarLicenceBack.isVisible = false
                    txtAddCarLicenceBack.isVisible = false
                    imgDeleteBack.isVisible = true
                    imgEditBack.isVisible = true
                }

                viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                    val pair = uploadImage(data?.data)
                    vehicleInfoViewModel.uploadImage(pair.first, pair.second,VEHICLE_LICENCE_BACK_FLAG)
                }

            }


        }
    }

    override fun onConfirm(
        leftSidePath: String?,
        rightSidePath: String?,
        frontSidePath: String?,
        backSidePath: String?,
        frontSeatPath: String?,
        backSeatPath: String?
    ) {
        this.leftSidePath = leftSidePath
        this.rightSidePath = rightSidePath
        this.frontSidePath = frontSidePath
        this.backSidePath = backSidePath
        this.frontSeatPath = frontSeatPath
        this.backSeatParh = backSeatPath


        if( leftSidePath==null && rightSidePath == null && frontSidePath == null && backSidePath == null
            && frontSeatPath == null && backSeatParh == null){
            binding.apply {
                imgInsertCarPhotos.isVisible = true
                txtAddCarrPhotoes.isVisible = true
                imgEditCarImages.isVisible = false
            }
        }
        else{
            binding.apply {
                imgInsertCarPhotos.isVisible = false
                txtAddCarrPhotoes.isVisible = false
                imgEditCarImages.isVisible = true
            }
        }
        
        if(leftSidePath==null){
            binding.imgCarSide1.setImageURI(null)
        }else if(rightSidePath==null){
            binding.imgCarSide2.setImageURI(null)
        }else if(frontSidePath==null){
            binding.imgCarSide3.setImageURI(null)
        }else if(backSidePath==null){
            binding.imgCarSide4.setImageURI(null)
        }else if(frontSeatPath==null){
            binding.imgCarSide5.setImageURI(null)
        }else if(backSeatPath==null){
            binding.imgCarSide5.setImageURI(null)
        }


        setCarImages()

    }
    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null

        licenceFrontPath_ = null
        licenceBackPath_ = null

        leftSidePath = null
        rightSidePath = null
        frontSidePath = null
        rightSidePath = null
        frontSeatPath = null
        backSeatParh = null

        pickCarImagesFragment = null


    }




}