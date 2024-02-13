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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.dhaval2404.imagepicker.ImagePicker
import com.israa.atmodrivecaptain.auth.data.model.DeleteModel
import com.israa.atmodrivecaptain.auth.domain.model.RegisterCaptain
import com.israa.atmodrivecaptain.auth.presentation.viewmodels.PersonalInfoViewModel
import com.israa.atmodrivecaptain.auth.presentation.viewmodels.PersonalInfoViewModel.Companion.LICENCE_BACK_FLAG
import com.israa.atmodrivecaptain.auth.presentation.viewmodels.PersonalInfoViewModel.Companion.LICENCE_FRONT_FLAG
import com.israa.atmodrivecaptain.auth.presentation.viewmodels.PersonalInfoViewModel.Companion.NATIONAL_ID_BACK_FLAG
import com.israa.atmodrivecaptain.auth.presentation.viewmodels.PersonalInfoViewModel.Companion.NATIONAL_ID_FRONT_FLAG
import com.israa.atmodrivecaptain.auth.presentation.viewmodels.PersonalInfoViewModel.Companion.PERSONAL_IMAGE_FLAG
import com.israa.atmodrivecaptain.databinding.FragmentPersonalInformationBinding
import com.israa.atmodrivecaptain.utils.FAILURE
import com.israa.atmodrivecaptain.utils.LOADING
import com.israa.atmodrivecaptain.utils.Progressbar
import com.israa.atmodrivecaptain.utils.SUCCESS
import com.israa.atmodrivecaptain.utils.UiState
import com.israa.atmodrivecaptain.utils.exhaustive
import com.israa.atmodrivecaptain.utils.pickImage
import com.israa.atmodrivecaptain.utils.showToast
import com.israa.atmodrivecaptain.utils.uploadImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


@Suppress("IMPLICIT_CAST_TO_ANY")
@AndroidEntryPoint
class PersonalInformationFragment : Fragment() {
    private var _binding: FragmentPersonalInformationBinding? = null
    private val binding get() = _binding!!

    private val personalInfoViewModel: PersonalInfoViewModel by viewModels()
    private val args: PersonalInformationFragmentArgs by navArgs()


    private var personalImgPath: String? = null
    private var idFrontPath: String? = null
    private var idBackPath: String? = null
    private var licenceFrontPath: String? = null
    private var licenceBackPath: String? = null

    private var personalImgUri: Uri? = null
    private var idFrontUri: Uri? = null
    private var idBackUri: Uri? = null
    private var licenceFrontUri: Uri? = null
    private var licenceBackUri: Uri? = null

    private var deletePersonalImage: Boolean = false
    private var deleteIdFront: Boolean = false
    private var deleteIdBack: Boolean = false
    private var deleteLicenceFront: Boolean = false
    private var deleteLicenceBack: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
        _binding = FragmentPersonalInformationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onClick()
        onRegisterCaptainObserve()
        onImageUploadObserve()
        enableSubmit()
    }

    private fun onRegisterCaptainObserve() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            personalInfoViewModel.registerCaptain.collect { status ->
                when (status) {
                    is UiState.Success -> {
                        val data = status.data as RegisterCaptain
                        navigate(data.rememberToken!!)
                        Progressbar.dismiss()
                        showToast("Send Successfully")
                    }

                    is UiState.Failure -> {
                        Toast.makeText(requireContext(), status.message, Toast.LENGTH_SHORT)
                            .show()
                        Progressbar.dismiss()
                    }

                    is UiState.Loading -> Progressbar.show(requireActivity())

                    else -> {}

                }


            }
        }
    }

    private fun navigate(token: String) {
        findNavController().navigate(
            PersonalInformationFragmentDirections
                .actionPersonalInformationFragmentToVehicleInformationFragment()
        )
    }

    private fun onImageUploadObserve() {


        viewLifecycleOwner.lifecycleScope.launch {
            personalInfoViewModel.apply {

                personalImageUri.observe(viewLifecycleOwner) { uri ->
                    personalImgUri = uri
                    binding.imgPersonalPhoto.setImageURI(personalImgUri)
                }

                nationalIdBackUri.observe(viewLifecycleOwner) { uri ->
                    idBackUri = uri
                    binding.imgIdBack.setImageURI(idBackUri)

                }

                nationalIdFrontUri.observe(viewLifecycleOwner) { uri ->
                    idFrontUri = uri
                    binding.imgIdFront.setImageURI(idFrontUri)

                }

                licenceFrontUri.observe(viewLifecycleOwner) { uri ->
                    this@PersonalInformationFragment.licenceFrontUri = uri
                    binding.imgLicenceFront.setImageURI(this@PersonalInformationFragment.licenceFrontUri)
                }

                licenceBackUri.observe(viewLifecycleOwner) { uri ->
                    this@PersonalInformationFragment.licenceBackUri = uri
                    binding.imgLicenceBack.setImageURI(this@PersonalInformationFragment.licenceBackUri)

                }

                personalImagePath.observe(viewLifecycleOwner) { path ->
                    when (path) {
                        is UiState.Success -> {
                            personalImgPath = path.data.toString()
                            binding.imgPersonalPhoto.setImageURI(personalImgUri)
                            personalImageState(SUCCESS)
                        }

                        is UiState.Failure -> {
                            showToast(path.message)
                            personalImageState(FAILURE)

                        }

                        UiState.Loading -> {
                            personalImageState(LOADING)

                        }

                        null -> {
                            personalImgPath = null
                            personalInfoViewModel.setUri(null, PERSONAL_IMAGE_FLAG)
                            personalImageState(null)
                            deletePersonalImage = false
                        }
                    }
                    enableSubmit()
                }

                nationalIdFrontPath.observe(viewLifecycleOwner) { path ->
                    when (path) {
                        is UiState.Success -> {
                            idFrontPath = path.data.toString()
                            binding.imgIdFront.setImageURI(idFrontUri)
                            nationalIdFrontState(SUCCESS)
                        }

                        is UiState.Failure -> {
                            showToast(path.message)
                            nationalIdFrontState(FAILURE)
                        }

                        UiState.Loading -> {
                            nationalIdFrontState(LOADING)
                        }

                        null -> {
                            idFrontPath = null
                            personalInfoViewModel.setUri(null, NATIONAL_ID_FRONT_FLAG)
                            nationalIdFrontState(null)
                            deleteIdFront = false
                        }
                    }
                    enableSubmit()
                }

                nationalIdBackPath.observe(viewLifecycleOwner) { path ->
                    when (path) {
                        is UiState.Success -> {
                            idBackPath = path.data.toString()
                            binding.imgIdBack.setImageURI(idBackUri)
                            nationalIdBackState(SUCCESS)
                        }

                        is UiState.Failure -> {
                            showToast(path.message)
                            nationalIdBackState(FAILURE)
                        }

                        UiState.Loading -> {
                            nationalIdBackState(LOADING)
                        }

                        null -> {
                            idBackPath = null
                            personalInfoViewModel.setUri(null, NATIONAL_ID_BACK_FLAG)
                            nationalIdBackState(null)
                            deleteIdBack = false
                        }
                    }
                    enableSubmit()
                }

                licenceBackPath.observe(viewLifecycleOwner) { path ->
                    when (path) {
                        is UiState.Success -> {
                            this@PersonalInformationFragment.licenceBackPath = path.data.toString()
                            binding.imgLicenceBack.setImageURI(this@PersonalInformationFragment.licenceBackUri)
                            licenceBackState(SUCCESS)
                        }

                        is UiState.Failure -> {
                            showToast(path.message)
                            licenceBackState(FAILURE)
                        }

                        UiState.Loading -> {
                            licenceBackState(LOADING)
                        }

                        null -> {
                            this@PersonalInformationFragment.licenceBackPath = null
                            personalInfoViewModel.setUri(null, LICENCE_BACK_FLAG)
                            licenceBackState(null)
                            deleteLicenceBack = false
                        }
                    }
                    enableSubmit()
                }
                licenceFrontPath.observe(viewLifecycleOwner) { path ->
                    when (path) {
                        is UiState.Success -> {
                            this@PersonalInformationFragment.licenceFrontPath = path.data.toString()
                            binding.imgLicenceFront.setImageURI(this@PersonalInformationFragment.licenceFrontUri)
                            licenceFrontState(SUCCESS)
                        }

                        is UiState.Failure -> {
                            showToast(path.message)
                            licenceFrontState(FAILURE)
                        }

                        UiState.Loading -> {
                            licenceFrontState(LOADING)
                        }

                        null -> {
                            this@PersonalInformationFragment.licenceFrontPath = null
                            personalInfoViewModel.setUri(null, LICENCE_FRONT_FLAG)
                            licenceFrontState(null)
                            deleteLicenceFront = false
                        }
                    }
                    enableSubmit()
                }
            }
        }
    }

    private fun licenceFrontState(state: Int?) {
        binding.apply {
            progressLicenceFront.isVisible = state == LOADING
            imgEditLicenceFront.isVisible = state == SUCCESS
            imgDeleteLicenceFront.isVisible = state == SUCCESS
            imgRetryLicenceFront.isVisible = state == FAILURE
            imgInsertLicenceFront.isVisible = state == null
            txtAddLicenceFront.isVisible = state == null
        }
    }

    private fun licenceBackState(state: Int?) {
        binding.apply {
            progressLicenceBack.isVisible = state == LOADING
            imgEditLicenceBack.isVisible = state == SUCCESS
            imgDeleteLicenceBack.isVisible = state == SUCCESS
            imgRetryLicenceBack.isVisible = state == FAILURE
            imgInsertLicenceBack.isVisible = state == null
            txtAddLicenceBack.isVisible = state == null
        }
    }

    private fun nationalIdBackState(state: Int?) {

        binding.apply {
            progressIdBack.isVisible = state == LOADING
            imgEditIdBack.isVisible = state == SUCCESS
            imgDeleteIdBack.isVisible = state == SUCCESS
            imgRetryIdBack.isVisible = state == FAILURE
            imgInsertIdBack.isVisible = state == null
            txtAddIdBack.isVisible = state == null

        }
    }

    private fun nationalIdFrontState(state: Int?) {
        binding.apply {
            progressIdFront.isVisible = state == LOADING
            imgEditIdFront.isVisible = state == SUCCESS
            imgDeleteIdFront.isVisible = state == SUCCESS
            imgRetryIdFront.isVisible = state == FAILURE
            imgInsertIdFront.isVisible = state == null
            txtAddIdFront.isVisible = state == null
        }
    }

    private fun personalImageState(state: Int?) {
        binding.apply {
            progressPersonalPhoto.isVisible = state == LOADING
            imgEditPersonalPhoto.isVisible = state == SUCCESS
            imgDeletePersonalPhoto.isVisible = state == SUCCESS
            imgRetryPersonalPhoto.isVisible = state == FAILURE
            imgInsertPhoto.isVisible = state == null
            txtAddPhoto.isVisible = state == null
        }
    }

    private fun enableSubmit(){
        binding.btnSubmit.isEnabled = personalImgPath != null && idFrontPath != null && idBackPath != null && licenceFrontPath != null && licenceBackPath !=null

    }
    private fun onClick() {

        binding.apply {
            imgInsertPhoto.setOnClickListener {
                pickImage(PERSONAL_IMAGE_FLAG)

            }
            imgEditPersonalPhoto.setOnClickListener {
                pickImage(PERSONAL_IMAGE_FLAG)
            }
            imgDeletePersonalPhoto.setOnClickListener {
                deletePersonalImage = true
                personalInfoViewModel.deleteImage(
                    DeleteModel(listOf(personalImgPath)),
                    PERSONAL_IMAGE_FLAG
                )
            }

            imgInsertIdFront.setOnClickListener {
                pickImage(NATIONAL_ID_FRONT_FLAG)
            }

            imgEditIdFront.setOnClickListener {
                pickImage(NATIONAL_ID_FRONT_FLAG)
            }
            imgDeleteIdFront.setOnClickListener {
                deleteIdFront = true
                personalInfoViewModel.deleteImage(
                    DeleteModel(listOf(idFrontPath)),
                    NATIONAL_ID_FRONT_FLAG
                )
            }

            imgInsertIdBack.setOnClickListener {
                pickImage(NATIONAL_ID_BACK_FLAG)
            }
            imgEditIdBack.setOnClickListener {
                pickImage(NATIONAL_ID_BACK_FLAG)
            }

            imgDeleteIdBack.setOnClickListener {
                deleteIdBack = true
                personalInfoViewModel.deleteImage(
                    DeleteModel(listOf(idBackPath)),
                    NATIONAL_ID_BACK_FLAG
                )
            }

            imgInsertLicenceFront.setOnClickListener {
                pickImage(LICENCE_FRONT_FLAG)
            }
            imgEditLicenceFront.setOnClickListener {
                pickImage(LICENCE_FRONT_FLAG)
            }

            imgDeleteLicenceFront.setOnClickListener {
                deleteLicenceFront = true
                personalInfoViewModel.deleteImage(
                    DeleteModel(listOf(licenceFrontPath)),
                    LICENCE_FRONT_FLAG
                )
            }

            imgInsertLicenceBack.setOnClickListener {
                pickImage(LICENCE_BACK_FLAG)
            }
            imgEditLicenceBack.setOnClickListener {
                pickImage(LICENCE_BACK_FLAG)
            }

            imgDeleteLicenceBack.setOnClickListener {
                deleteLicenceBack = true
                personalInfoViewModel.deleteImage(
                    DeleteModel(listOf(licenceBackPath)),
                    LICENCE_BACK_FLAG
                )

            }

            btnSubmit.setOnClickListener {
                    registerCaptain()
            }

            imgRetryPersonalPhoto.setOnClickListener {
                imgRetryPersonalPhoto.isVisible = false
                if (deletePersonalImage) {
                    personalInfoViewModel.deleteImage(
                        DeleteModel(listOf(personalImgPath)),
                        PERSONAL_IMAGE_FLAG
                    )
                } else {
                    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                        val pair = uploadImage(personalImgUri)
                        personalInfoViewModel.uploadImage(
                            pair.first,
                            pair.second,
                            PERSONAL_IMAGE_FLAG
                        )
                    }
                }
            }

            imgRetryIdFront.setOnClickListener {
                imgRetryIdFront.isVisible = false
                if (deleteIdFront) {
                    personalInfoViewModel.deleteImage(
                        DeleteModel(listOf(idFrontPath)),
                        NATIONAL_ID_FRONT_FLAG
                    )
                } else {
                    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                        val pair = uploadImage(idFrontUri)
                        personalInfoViewModel.uploadImage(
                            pair.first,
                            pair.second,
                            NATIONAL_ID_FRONT_FLAG
                        )
                    }
                }
            }

            imgRetryIdBack.setOnClickListener {
                imgRetryIdBack.isVisible = false
                if (deleteIdBack) {
                    personalInfoViewModel.deleteImage(
                        DeleteModel(listOf(idBackPath)),
                        NATIONAL_ID_BACK_FLAG
                    )
                } else {
                    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                        val pair = uploadImage(idBackUri)
                        personalInfoViewModel.uploadImage(
                            pair.first,
                            pair.second,
                            NATIONAL_ID_BACK_FLAG
                        )
                    }
                }
            }

            imgRetryLicenceFront.setOnClickListener {
                imgRetryLicenceFront.isVisible = false
                if (deleteLicenceFront) {
                    personalInfoViewModel.deleteImage(
                        DeleteModel(listOf(licenceFrontPath)),
                        LICENCE_FRONT_FLAG
                    )
                } else {
                    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                        val pair = uploadImage(this@PersonalInformationFragment.licenceFrontUri)
                        personalInfoViewModel.uploadImage(
                            pair.first,
                            pair.second,
                            LICENCE_FRONT_FLAG
                        )
                    }
                }
            }
            imgRetryLicenceBack.setOnClickListener {
                imgRetryLicenceBack.isVisible = false
                if (deleteLicenceBack) {
                    personalInfoViewModel.deleteImage(
                        DeleteModel(listOf(licenceBackPath)),
                        LICENCE_BACK_FLAG
                    )
                } else {
                    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                        val pair = uploadImage(this@PersonalInformationFragment.licenceBackUri)
                        personalInfoViewModel.uploadImage(
                            pair.first,
                            pair.second,
                            LICENCE_BACK_FLAG
                        )
                    }
                }
            }
            btnBack.setOnClickListener {
                findNavController().popBackStack()
            }

        }
    }


    private fun registerCaptain() {
        Progressbar.show(requireActivity())
        personalInfoViewModel.registerCaptain(
            args.mobile,
            personalImgPath,
            "device_token:${args.mobile}",
            "device_id",
            "android",
            idFrontPath,
            idBackPath,
            licenceFrontPath,
            licenceBackPath,
            1
        )

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == PERSONAL_IMAGE_FLAG) {

                //in case of editing the current image
//                if(personalImgPath != null){
//                    personalInfoViewModel.deleteImage(DeleteModel(listOf(personalImgPath)),PERSONAL_IMAGE_FLAG)
//                    onPathDeleted(PERSONAL_IMAGE_FLAG)
//                }
                personalInfoViewModel.setUri(data?.data, PERSONAL_IMAGE_FLAG)
//                binding.apply {
//                    imgInsertPhoto.isVisible = false
//                    txtAddPhoto.isVisible = false
//                    progressPersonalPhoto.isVisible = true
//                }

                viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                    val pair = uploadImage(data?.data)
                    personalInfoViewModel.uploadImage(pair.first, pair.second, PERSONAL_IMAGE_FLAG)
                }

            } else if (requestCode == NATIONAL_ID_FRONT_FLAG) {

//                if(idFrontPath != null){
//                    personalInfoViewModel.deleteImage(DeleteModel(listOf(idFrontPath)), NATIONAL_ID_FRONT_FLAG)
//                    idFrontPath = null
//                }
                personalInfoViewModel.setUri(data?.data, NATIONAL_ID_FRONT_FLAG)
//                binding.apply {
//                    imgInsertIdFront.isVisible = false
//                    txtAddIdFront.isVisible = false
//                    progressIdFront.isVisible = true
//                }

                viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                    val pair = uploadImage(data?.data)
                    personalInfoViewModel.uploadImage(
                        pair.first,
                        pair.second,
                        NATIONAL_ID_FRONT_FLAG
                    )
                }


            } else if (requestCode == NATIONAL_ID_BACK_FLAG) {
//                if(idBackPath != null){
//                    personalInfoViewModel.deleteImage(DeleteModel(listOf(idBackPath)), NATIONAL_ID_BACK_FLAG)
//                    idBackPath = null
//                }
                personalInfoViewModel.setUri(data?.data, NATIONAL_ID_BACK_FLAG)
//
//                binding.apply {
//                    imgInsertIdBack.isVisible = false
//                    txtAddIdBack.isVisible = false
//                    progressIdBack.isVisible = true
//
//                }


                viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                    val pair = uploadImage(data?.data)
                    personalInfoViewModel.uploadImage(
                        pair.first, pair.second, NATIONAL_ID_BACK_FLAG
                    )
                }
            } else if (requestCode == LICENCE_FRONT_FLAG) {

//                if(licenceFrontPath_ != null){
//                    personalInfoViewModel.deleteImage(DeleteModel(listOf(licenceFrontPath_)), LICENCE_FRONT_FLAG)
//                    licenceFrontPath_ = null
//                }

                personalInfoViewModel.setUri(data?.data, LICENCE_FRONT_FLAG)
//
//                binding.apply {
//                    imgInsertLicenceFront.isVisible = false
//                    txtAddLicenceFront.isVisible = false
//                    progressLicenceFront.isVisible = true
//
//                }


                viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                    val pair = uploadImage(data?.data)
                    personalInfoViewModel.uploadImage(pair.first, pair.second, LICENCE_FRONT_FLAG)
                }
            } else if (requestCode == LICENCE_BACK_FLAG) {

//                if(licenceBackPath_ != null){
//                    personalInfoViewModel.deleteImage(DeleteModel(listOf(licenceBackPath_)), LICENCE_BACK_FLAG)
//                    licenceBackPath_ = null
//                }

                personalInfoViewModel.setUri(data?.data, LICENCE_BACK_FLAG)
//
//                binding.apply {
//                    imgInsertLicenceBack.isVisible = false
//                    txtAddLicenceBack.isVisible = false
//                    progressLicenceBack.isVisible = true
//
//                }
//

                viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                    val pair = uploadImage(data?.data)
                    personalInfoViewModel.uploadImage(pair.first, pair.second, LICENCE_BACK_FLAG)
                }
            }

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null


    }


}