package com.israa.atmodrivecaptain.auth.presentation.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.israa.atmodrivecaptain.auth.data.model.DeleteModel
import com.israa.atmodrivecaptain.auth.presentation.viewmodels.VehicleInfoViewModel.Companion.VEHICLE_LICENCE_FRONT_FLAG
import com.israa.atmodrivecaptain.auth.presentation.viewmodels.VehicleInfoViewModel.Companion.VEHICLE_LICENCE_BACK_FLAG
import com.israa.atmodrivecaptain.auth.presentation.viewmodels.VehicleInfoViewModel
import com.israa.atmodrivecaptain.databinding.FragmentVehicleInformationBinding
import com.israa.atmodrivecaptain.utils.BACK_SEAT
import com.israa.atmodrivecaptain.utils.BACK_SEAT_PATH
import com.israa.atmodrivecaptain.utils.BACK_SIDE
import com.israa.atmodrivecaptain.utils.BACK_SIDE_PATH
import com.israa.atmodrivecaptain.utils.FAILURE
import com.israa.atmodrivecaptain.utils.FRONT_SEAT
import com.israa.atmodrivecaptain.utils.FRONT_SEAT_PATH
import com.israa.atmodrivecaptain.utils.FRONT_SIDE
import com.israa.atmodrivecaptain.utils.FRONT_SIDE_PATH
import com.israa.atmodrivecaptain.utils.LEFT_SIDE
import com.israa.atmodrivecaptain.utils.LEFT_SIDE_PATH
import com.israa.atmodrivecaptain.utils.LOADING
import com.israa.atmodrivecaptain.utils.Progressbar
import com.israa.atmodrivecaptain.utils.RIGHT_SIDE
import com.israa.atmodrivecaptain.utils.RIGHT_SIDE_PATH
import com.israa.atmodrivecaptain.utils.SUCCESS
import com.israa.atmodrivecaptain.utils.UiState
import com.israa.atmodrivecaptain.utils.parcelable
import com.israa.atmodrivecaptain.utils.pickImage
import com.israa.atmodrivecaptain.utils.showToast
import com.israa.atmodrivecaptain.utils.uploadImage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VehicleInformationFragment : Fragment(),CarImages{

    private var pickCarImagesFragment: PickCarImagesFragment? = null
    private var _binding: FragmentVehicleInformationBinding? = null

    private val vehicleInfoViewModel: VehicleInfoViewModel by activityViewModels()

    private val binding get() = _binding!!

    private var leftSidePath: String? = null
    private var rightSidePath: String? = null
    private var frontSidePath: String? = null
    private var backSidePath: String? = null
    private var frontSeatPath: String? = null
    private var backSeatPath: String? = null

    private var licenceFrontPath: String? = null
    private var licenceBackPath: String? = null

    private var leftSideUri: Uri? = null
    private var rightSideUri: Uri? = null
    private var frontSideUri: Uri? = null
    private var backSideUri: Uri? = null
    private var frontSeatUri: Uri? = null
    private var backSeatUri: Uri? = null
    private var licenceFrontUri: Uri? = null
    private var licenceBackUri: Uri? = null

    private var deleteLicenceFront: Boolean = false
    private var deleteLicenceBack: Boolean = false

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
        initDialog()
        getSavedImages(savedInstanceState)
        insertImagesVisibility()
        setCarImages()
        enableSubmitButton()
    }

    private fun enableSubmitButton() {
        binding.btnNext.isEnabled = licenceFrontPath!= null && licenceBackPath != null && leftSidePath != null && rightSidePath != null
        frontSidePath != null && backSidePath != null && frontSeatPath != null && backSeatPath != null

    }

    private fun getSavedImages(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            it.apply {
                leftSideUri = parcelable(LEFT_SIDE)
                rightSideUri = parcelable(RIGHT_SIDE)
                frontSideUri = parcelable(FRONT_SIDE)
                backSideUri = parcelable(BACK_SIDE)
                frontSeatUri = parcelable(FRONT_SEAT)
                backSeatUri = parcelable(BACK_SEAT)

                leftSidePath = getString(LEFT_SIDE_PATH)
                rightSidePath = getString(RIGHT_SIDE_PATH)
                frontSidePath = getString(FRONT_SIDE_PATH)
                backSidePath = getString(BACK_SIDE_PATH)
                frontSeatPath = getString(FRONT_SEAT_PATH)
                backSeatPath = getString(BACK_SEAT_PATH)
            }
        }
    }

    private fun observeOnRegisterVehicle() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            vehicleInfoViewModel.registerVehicle.collect {
                when (it) {
                    is UiState.Failure -> {
                        Progressbar.dismiss()
                        showToast(it.message)
                    }

                    UiState.Loading -> Progressbar.show(requireActivity())
                    is UiState.Success -> {
                        showToast("Send Successfully")
                        Progressbar.dismiss()
                        navigate()
                    }

                    else -> {}
                }
            }
        }
    }

    private fun licenceBackState(state:Int?){
        binding.apply {
            progressLicenceBack.isVisible = state == LOADING
            imgInsertCarLicenceBack.isVisible = state == null
            txtAddCarLicenceBack.isVisible = state == null
            imgRetryLicenceBack.isVisible = state == FAILURE
            imgEditBack.isVisible = state == SUCCESS
            imgDeleteBack.isVisible = state == SUCCESS
        }
    }

    private fun licenceFrontState(state:Int?){
        binding.apply {
            progressLicenceFront.isVisible = state == LOADING
            imgInsertCarLicenceFront.isVisible = state == null
            txtAddCarLicenceFront.isVisible = state == null
            imgRetryLicenceFront.isVisible = state == FAILURE
            imgEditFront.isVisible = state == SUCCESS
            imgDeleteFront.isVisible = state == SUCCESS
        }
    }

    private fun observeOnLicenceImages() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            vehicleInfoViewModel.apply {
                licenceBackUri.observe(viewLifecycleOwner){uri->
                    this@VehicleInformationFragment.licenceBackUri = uri

                }
                licenceFrontUri.observe(viewLifecycleOwner){uri->
                    this@VehicleInformationFragment.licenceFrontUri = uri

                }
                licenceBackPath.observe(viewLifecycleOwner) {
                    when (it) {
                        is UiState.Failure -> {
                            licenceBackState(FAILURE)
                            showToast(it.message)
                        }
                        UiState.Loading -> {
                           licenceBackState(LOADING)
                        }

                        is UiState.Success -> {
                            this@VehicleInformationFragment.licenceBackPath = it.data as String
                            binding.imgLicenceBack.setImageURI(this@VehicleInformationFragment.licenceBackUri)
                            licenceBackState(SUCCESS)

                        }

                        else -> {
                            this@VehicleInformationFragment.licenceBackPath = null
                            vehicleInfoViewModel.setImageUriValue(null, VEHICLE_LICENCE_BACK_FLAG)
                            binding.imgLicenceBack.setImageURI(null)
                            deleteLicenceBack = false
                            licenceBackState(null)
                        }
                    }
                    enableSubmitButton()
                }

                licenceFrontPath.observe(viewLifecycleOwner) {
                    when (it) {
                        is UiState.Failure -> {
                            licenceFrontState(FAILURE)
                            showToast(it.message)
                        }

                        UiState.Loading ->{
                            licenceFrontState(LOADING)

                        }
                        is UiState.Success -> {
                            this@VehicleInformationFragment.licenceFrontPath = it.data as String
                            binding.imgLicenceFront.setImageURI(this@VehicleInformationFragment.licenceFrontUri)

                            licenceFrontState(SUCCESS)
                        }

                        else -> {
                            this@VehicleInformationFragment.licenceFrontPath = null
                            binding.imgLicenceFront.setImageURI(null)
                            vehicleInfoViewModel.setImageUriValue(null, VEHICLE_LICENCE_FRONT_FLAG)
                            deleteLicenceFront = false
                            licenceFrontState(null)
                        }
                    }
                    enableSubmitButton()
                }
            }
        }

    }

    private fun navigate() {
        findNavController().navigate(
            VehicleInformationFragmentDirections.actionVehicleInformationFragmentToBankAccountFragment()
        )
    }


    private fun setCarImages() {

      binding.apply {
          imgLeftSide.setImageURI(leftSideUri)
          imgRightSide.setImageURI(rightSideUri)
          imgFrontSide.setImageURI(frontSideUri)
          imgBackSide.setImageURI(backSideUri)
          imgFrontSeat.setImageURI(frontSeatUri)
          imgBackSeat.setImageURI(backSeatUri)
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
            imgDeleteBack.setOnClickListener {
                deleteLicenceBack = true
                vehicleInfoViewModel.deleteImage(
                    DeleteModel(listOf(leftSidePath)), VEHICLE_LICENCE_BACK_FLAG)
            }
            imgDeleteFront.setOnClickListener {
                deleteLicenceFront = true
                vehicleInfoViewModel.deleteImage(
                    DeleteModel(listOf(frontSidePath)), VEHICLE_LICENCE_FRONT_FLAG)
            }
            imgRetryLicenceBack.setOnClickListener {
                imgRetryLicenceBack.isVisible = false
                if (deleteLicenceBack){
                    vehicleInfoViewModel.deleteImage(
                        DeleteModel(listOf(leftSidePath)), VEHICLE_LICENCE_BACK_FLAG)
                }else{
                    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                        val pair = uploadImage(licenceBackUri)
                        vehicleInfoViewModel.uploadImage(pair.first, pair.second,
                            VEHICLE_LICENCE_BACK_FLAG
                        )
                    }
                }
            }
            imgRetryLicenceFront.setOnClickListener {
                imgRetryLicenceFront.isVisible = false
                if (deleteLicenceFront){
                    vehicleInfoViewModel.deleteImage(
                        DeleteModel(listOf(frontSidePath)), VEHICLE_LICENCE_FRONT_FLAG)
                }else{
                    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                        val pair = uploadImage(licenceFrontUri)
                        vehicleInfoViewModel.uploadImage(pair.first, pair.second,
                            VEHICLE_LICENCE_FRONT_FLAG)
                    }
                }
            }
        }
    }


    private fun registerVehicle() {
        vehicleInfoViewModel.registerVehicle(
            vehicleFront = frontSidePath,
            vehicleBack = backSidePath,
            vehicleLeft = leftSidePath,
            vehicleRight = rightSidePath,
            vehicleFrontSeat = frontSeatPath,
            vehicleBackSeat = backSeatPath,
            vehicleLicenseFront = licenceFrontPath,
            vehicleLicenseBack = licenceBackPath
        )
    }

    private fun initDialog(){
        if (pickCarImagesFragment == null) {
            pickCarImagesFragment = PickCarImagesFragment()
            pickCarImagesFragment!!.setCarImagesListener(this)
        }
    }
    private fun showDialog() {
        val fragmentManager = (activity as FragmentActivity).supportFragmentManager

        fragmentManager.let {
            pickCarImagesFragment!!.show(it, "aaaaaaa")
            pickCarImagesFragment!!.isCancelable = false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == VEHICLE_LICENCE_FRONT_FLAG) {
                vehicleInfoViewModel.setImageUriValue(data?.data, VEHICLE_LICENCE_FRONT_FLAG)
                viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                    val pair = uploadImage(data?.data)
                    vehicleInfoViewModel.uploadImage(
                        pair.first,
                        pair.second,
                        VEHICLE_LICENCE_FRONT_FLAG
                    )
                }
            } else if (requestCode == VEHICLE_LICENCE_BACK_FLAG) {
                vehicleInfoViewModel.setImageUriValue(data?.data, VEHICLE_LICENCE_BACK_FLAG)
                viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                    val pair = uploadImage(data?.data)
                    vehicleInfoViewModel.uploadImage(
                        pair.first,
                        pair.second,
                        VEHICLE_LICENCE_BACK_FLAG
                    )
                }

            }


        }
    }


    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    override fun onImagesConfirmed(
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
        this.backSeatPath = backSeatPath
        insertImagesVisibility()
        enableSubmitButton()    

    }

    private fun insertImagesVisibility() {

        if (leftSidePath == null && rightSidePath == null && frontSidePath == null && backSidePath == null
            && frontSeatPath == null && this.backSeatPath == null
        ) {
            binding.apply {
                imgInsertCarPhotos.isVisible = true
                txtAddCarrPhotoes.isVisible = true
                imgEditCarImages.isVisible = false
            }
        } else {
            binding.apply {
                imgInsertCarPhotos.isVisible = false
                txtAddCarrPhotoes.isVisible = false
                imgEditCarImages.isVisible = true
            }
        }
    }

    override fun confirmedImagesUris(
        leftSideUri: Uri?,
        rightSideUri: Uri?,
        frontSideUri: Uri?,
        backSideUri: Uri?,
        frontSeatUri: Uri?,
        backSeatUri: Uri?
    ) {
            this@VehicleInformationFragment.leftSideUri = leftSideUri
            this@VehicleInformationFragment.rightSideUri = rightSideUri
            this@VehicleInformationFragment.frontSideUri = frontSideUri
            this@VehicleInformationFragment.backSideUri = backSideUri
            this@VehicleInformationFragment.frontSeatUri = frontSeatUri
            this@VehicleInformationFragment.backSeatUri = backSeatUri

            setCarImages()

    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.apply {
            putParcelable(LEFT_SIDE,leftSideUri)
            putParcelable(RIGHT_SIDE,rightSideUri)
            putParcelable(FRONT_SIDE,frontSideUri)
            putParcelable(BACK_SIDE,backSideUri)
            putParcelable(FRONT_SEAT,frontSeatUri)
            putParcelable(BACK_SEAT,backSeatUri)

            putString(LEFT_SIDE_PATH,leftSidePath)
            putString(RIGHT_SIDE_PATH,rightSidePath)
            putString(FRONT_SEAT_PATH,frontSeatPath)
            putString(BACK_SEAT_PATH,backSeatPath)
            putString(FRONT_SIDE_PATH,frontSidePath)
            putString(BACK_SIDE_PATH,backSidePath)
        }
    }


}