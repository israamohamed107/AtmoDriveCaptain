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
import com.israa.atmodrivecaptain.auth.presentation.viewmodels.PersonalInfoViewModel
import com.israa.atmodrivecaptain.auth.presentation.viewmodels.PersonalInfoViewModel.Companion.LICENCE_BACK_FLAG
import com.israa.atmodrivecaptain.auth.presentation.viewmodels.PersonalInfoViewModel.Companion.LICENCE_FRONT_FLAG
import com.israa.atmodrivecaptain.auth.presentation.viewmodels.PersonalInfoViewModel.Companion.NATIONAL_ID_BACK_FLAG
import com.israa.atmodrivecaptain.auth.presentation.viewmodels.PersonalInfoViewModel.Companion.NATIONAL_ID_FRONT_FLAG
import com.israa.atmodrivecaptain.auth.presentation.viewmodels.PersonalInfoViewModel.Companion.PERSONAL_IMAGE_FLAG
import com.israa.atmodrivecaptain.databinding.FragmentPersonalInformationBinding
import com.israa.atmodrivecaptain.utils.Progressbar
import com.israa.atmodrivecaptain.utils.UiState
import com.israa.atmodrivecaptain.utils.exhaustive
import com.israa.atmodrivecaptain.utils.pickImage
import com.israa.atmodrivecaptain.utils.uploadImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async


@Suppress("IMPLICIT_CAST_TO_ANY")
@AndroidEntryPoint
class PersonalInformationFragment : Fragment() {
    private var _binding: FragmentPersonalInformationBinding? = null
    private val binding get() = _binding!!

    private val personalInfoViewModel: PersonalInfoViewModel by viewModels()
    private val args: PersonalInformationFragmentArgs by navArgs()


    var personalImgPath: String? = null
    var idFrontPath: String? = null
    var idBackPath: String? = null
    var licenceFrontPath_: String? = null
    var licenceBackPath_: String? = null

    var personalImgUri: Uri? = null
    var idFrontUri: Uri? = null
    var idBackUri: Uri? = null
    var licenceFrontUri: Uri? = null
    var licenceBackUri: Uri? = null


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
    }

    private fun onRegisterCaptainObserve() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            personalInfoViewModel.registerCaptain.collect { status ->
                when (status) {
                    is UiState.Success -> {
                        Progressbar.dismiss()
                        navigate()
                    }

                    is UiState.Failure -> {
                            Toast.makeText(requireContext(), status.message, Toast.LENGTH_SHORT)
                                .show()
                            Progressbar.dismiss()
                    }

                    is UiState.Loading -> Progressbar.show(requireActivity())

                    else ->{}

                }


            }
        }
    }

    private fun navigate() {
        findNavController().navigate(PersonalInformationFragmentDirections
            .actionPersonalInformationFragmentToVehicleInformationFragment())
    }

    private fun onImageUploadObserve() {

        personalInfoViewModel.apply {
            personalImagePath.observe(viewLifecycleOwner) { path ->
                when (path) {
                    is UiState.Success -> {
                        personalImgPath = path.data.toString()
                        binding.apply {
                            progressPersonalPhoto.isVisible = false
                            imgPersonalPhoto.setImageURI(personalImgUri)
                            imgEditPersonalPhoto.isVisible = true
                            imgDeletePersonalPhoto.isVisible = true
                        }
                    }

                    is UiState.Failure -> {
                        Toast.makeText(requireContext(), path.message, Toast.LENGTH_SHORT).show()
                        binding.apply {
                            progressPersonalPhoto.isVisible = false
                            imgRetryPersonalPhoto.isVisible = true
                        }
                    }
                    UiState.Loading -> {
                        binding.progressPersonalPhoto.isVisible = true
                    }
                    else -> {}
                }.exhaustive
            }

            nationalIdFrontPath.observe(viewLifecycleOwner) { path ->
                when (path) {
                    is UiState.Success -> {
                        idFrontPath = path.data.toString()
                        binding.apply {
                            progressIdFront.isVisible = false
                            imgIdFront.setImageURI(idFrontUri)
                            imgEditIdFront.isVisible = true
                            imgDeleteIdFront.isVisible = true
                        }
                    }

                    is UiState.Failure -> {
                        Toast.makeText(requireContext(), path.message, Toast.LENGTH_SHORT).show()
                        binding.apply {
                            progressIdFront.isVisible = false
                            imgRetryIdFront.isVisible = true
                        }
                    }
                    UiState.Loading -> {
                        binding.progressIdFront.isVisible = true
                    }
                    else -> {}
                }.exhaustive
            }

            nationalIdBackPath.observe(viewLifecycleOwner) { path ->
                when (path) {
                    is UiState.Success -> {
                        idBackPath = path.data.toString()
                        binding.apply {
                            progressIdBack.isVisible = false
                            imgIdBack.setImageURI(idBackUri)
                            imgEditIdBack.isVisible = true
                            imgDeleteIdBack.isVisible = true
                        }
                    }

                    is UiState.Failure -> {
                        Toast.makeText(requireContext(), path.message, Toast.LENGTH_SHORT).show()
                        binding.apply {
                            progressIdBack.isVisible = false
                            imgRetryIdBack.isVisible = true
                        }
                    }
                    UiState.Loading -> {
                        binding.progressIdBack.isVisible = true

                    }
                    else -> {}
                }.exhaustive
            }

            licenceBackPath.observe(viewLifecycleOwner) { path ->
                when (path) {
                    is UiState.Success -> {
                        licenceBackPath_ = path.data.toString()
                        binding.apply {
                            progressLicenceBack.isVisible = false
                            imgLicenceBack.setImageURI(licenceBackUri)
                            imgEditLicenceBack.isVisible = true
                            imgDeleteLicenceBack.isVisible = true
                        }
                    }

                    is UiState.Failure -> {
                        Toast.makeText(requireContext(), path.message, Toast.LENGTH_SHORT).show()
                        binding.apply {
                            progressLicenceBack.isVisible = false
                            imgRetryLicenceBack.isVisible = true
                        }
                    }
                    UiState.Loading -> {
                        binding.progressLicenceBack.isVisible = true

                    }
                    else -> {}
                }.exhaustive
            }
            licenceFrontPath.observe(viewLifecycleOwner) { path ->
                when (path) {
                    is UiState.Success -> {
                        licenceFrontPath_ = path.data.toString()
                        binding.apply {
                            progressLicenceFront.isVisible = false
                            imgLicenceFront.setImageURI(licenceFrontUri)
                            imgEditLicenceFront.isVisible = true
                            imgDeleteLicenceFront.isVisible = true
                        }
                    }

                    is UiState.Failure -> {
                        Toast.makeText(requireContext(), path.message, Toast.LENGTH_SHORT).show()
                        binding.apply {
                            progressLicenceFront.isVisible = false
                            imgRetryLicenceFront.isVisible = true
                        }
                    }
                    UiState.Loading -> {
                        binding.progressLicenceFront.isVisible = true
                    }
                    else -> {}
                }.exhaustive
            }
        }
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
                personalInfoViewModel.deleteImage(DeleteModel(listOf(personalImgPath)), PERSONAL_IMAGE_FLAG)
                onPathDeleted(PERSONAL_IMAGE_FLAG)
            }

            imgInsertIdFront.setOnClickListener {
                pickImage(NATIONAL_ID_FRONT_FLAG)
            }

            imgEditIdFront.setOnClickListener {
                pickImage(NATIONAL_ID_FRONT_FLAG)
            }
            imgDeleteIdFront.setOnClickListener {
                personalInfoViewModel.deleteImage(DeleteModel(listOf(idFrontPath)), NATIONAL_ID_FRONT_FLAG)
                onPathDeleted(NATIONAL_ID_FRONT_FLAG)

            }

            imgInsertIdBack.setOnClickListener {
                pickImage(NATIONAL_ID_BACK_FLAG)
            }
            imgEditIdBack.setOnClickListener {
                pickImage(NATIONAL_ID_FRONT_FLAG)
            }

            imgDeleteIdBack.setOnClickListener {
                personalInfoViewModel.deleteImage(DeleteModel(listOf(idBackPath)), NATIONAL_ID_BACK_FLAG)
                onPathDeleted(NATIONAL_ID_BACK_FLAG)
            }

            imgInsertLicenceFront.setOnClickListener {
                pickImage(LICENCE_FRONT_FLAG)
            }
            imgEditLicenceFront.setOnClickListener {
                pickImage(LICENCE_FRONT_FLAG)
            }

            imgDeleteLicenceFront.setOnClickListener {
                personalInfoViewModel.deleteImage(DeleteModel(listOf(licenceFrontPath_)), LICENCE_FRONT_FLAG)
                onPathDeleted(LICENCE_FRONT_FLAG)
            }

            imgInsertLicenceBack.setOnClickListener {
                pickImage(LICENCE_BACK_FLAG)
            }
            imgEditLicenceBack.setOnClickListener {
                pickImage(LICENCE_BACK_FLAG)
            }

            imgDeleteLicenceBack.setOnClickListener {
                personalInfoViewModel.deleteImage(DeleteModel(listOf(licenceBackPath_)), LICENCE_BACK_FLAG)
                onPathDeleted(LICENCE_BACK_FLAG)
            }

            btnNext.setOnClickListener {
                registerCaptain()
            }

            imgRetryPersonalPhoto.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                    val pair = uploadImage(personalImgUri)
                    personalInfoViewModel.uploadImage(pair.first, pair.second, PERSONAL_IMAGE_FLAG)
                }
            }

            imgRetryIdFront.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                    val pair = uploadImage(idFrontUri)
                    personalInfoViewModel.uploadImage(pair.first, pair.second, PERSONAL_IMAGE_FLAG)
                }
            }

            imgRetryIdBack.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                    val pair = uploadImage(idBackUri)
                    personalInfoViewModel.uploadImage(pair.first, pair.second, PERSONAL_IMAGE_FLAG)
                }
            }

            imgRetryLicenceFront.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                    val pair = uploadImage(licenceFrontUri)
                    personalInfoViewModel.uploadImage(pair.first, pair.second, PERSONAL_IMAGE_FLAG)
                }
            }
            imgRetryLicenceBack.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                    val pair = uploadImage(licenceBackUri)
                    personalInfoViewModel.uploadImage(pair.first, pair.second, PERSONAL_IMAGE_FLAG)
                }
            }

        }
    }

    private fun onPathDeleted(flag:Int) {
        personalInfoViewModel.deleteImage.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Failure -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()

                }

                UiState.Loading -> {
                }
                is UiState.Success -> {
                    when(flag){
                        PERSONAL_IMAGE_FLAG ->{
                            binding.apply {
                                imgPersonalPhoto.setImageURI(null)
                                imgDeletePersonalPhoto.isVisible = false
                                imgEditPersonalPhoto.isVisible = false
                                imgInsertPhoto.isVisible = true
                                txtAddPhoto.isVisible = true
                                personalImgPath = null
                            }
                        }
                        NATIONAL_ID_FRONT_FLAG -> {
                            binding.apply {
                                imgInsertIdFront.isVisible = true
                                txtAddIdFront.isVisible = true
                                imgEditIdFront.isVisible = false
                                imgDeleteIdFront.isVisible = false
                                idFrontPath = null
                            }
                        }
                        NATIONAL_ID_BACK_FLAG ->{
                            binding.apply {

                                imgIdBack.setImageURI(null)
                                imgInsertIdBack.isVisible = true
                                txtAddIdBack.isVisible = true
                                imgEditIdBack.isVisible = false
                                imgDeleteIdBack.isVisible = false
                                idBackPath = null
                            }
                        }
                        LICENCE_FRONT_FLAG ->{
                            binding.apply {

                                imgLicenceFront.setImageURI(null)
                                imgInsertLicenceFront.isVisible = true
                                txtAddLicenceFront.isVisible = true
                                imgEditLicenceFront.isVisible = false
                                imgDeleteLicenceFront.isVisible = false
                                licenceFrontPath_ = null
                            }
                        }
                        LICENCE_BACK_FLAG ->{
                            binding.apply {

                                imgLicenceBack.setImageURI(null)
                                imgInsertLicenceBack.isVisible = true
                                txtAddLicenceBack.isVisible = true
                                imgEditLicenceBack.isVisible = false
                                imgDeleteLicenceBack.isVisible = false
                                licenceBackPath_ = null


                            }
                        }

                        else->{}
                    }

                }

                else -> {}
            }.exhaustive
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
            licenceFrontPath_,
            licenceBackPath_,
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
                personalImgUri = data?.data
                binding.apply {
                    imgInsertPhoto.isVisible = false
                    txtAddPhoto.isVisible = false
                    progressPersonalPhoto.isVisible = true
                }

                viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                    val pair = uploadImage(data?.data)
                    personalInfoViewModel.uploadImage(pair.first, pair.second, PERSONAL_IMAGE_FLAG)
                }

            } else if (requestCode == NATIONAL_ID_FRONT_FLAG) {

//                if(idFrontPath != null){
//                    personalInfoViewModel.deleteImage(DeleteModel(listOf(idFrontPath)), NATIONAL_ID_FRONT_FLAG)
//                    idFrontPath = null
//                }
                idFrontUri = data?.data
                binding.apply {
                    imgInsertIdFront.isVisible = false
                    txtAddIdFront.isVisible = false
                    progressIdFront.isVisible = true
                }

                viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                    val pair = uploadImage(data?.data)
                    personalInfoViewModel.uploadImage(
                        pair.first, pair.second, NATIONAL_ID_FRONT_FLAG
                    )
                }


            } else if (requestCode == NATIONAL_ID_BACK_FLAG) {
//                if(idBackPath != null){
//                    personalInfoViewModel.deleteImage(DeleteModel(listOf(idBackPath)), NATIONAL_ID_BACK_FLAG)
//                    idBackPath = null
//                }
                idBackUri = data?.data

                binding.apply {
                    imgInsertIdBack.isVisible = false
                    txtAddIdBack.isVisible = false
                    progressIdBack.isVisible = true

                }


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

                licenceFrontUri = data?.data

                binding.apply {
                    imgInsertLicenceFront.isVisible = false
                    txtAddLicenceFront.isVisible = false
                    progressLicenceFront.isVisible = true

                }


                viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                    val pair = uploadImage(data?.data)
                    personalInfoViewModel.uploadImage(pair.first, pair.second, LICENCE_FRONT_FLAG)
                }
            } else if (requestCode == LICENCE_BACK_FLAG) {

//                if(licenceBackPath_ != null){
//                    personalInfoViewModel.deleteImage(DeleteModel(listOf(licenceBackPath_)), LICENCE_BACK_FLAG)
//                    licenceBackPath_ = null
//                }

                licenceBackUri = data?.data

                binding.apply {
                    imgInsertLicenceBack.isVisible = false
                    txtAddLicenceBack.isVisible = false
                    progressLicenceBack.isVisible = true

                }


                viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                    val pair = uploadImage(data?.data)
                    personalInfoViewModel.uploadImage(pair.first, pair.second, LICENCE_BACK_FLAG)
                }
            }


            onImageUploadObserve()

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        personalImgPath = null
        idBackPath = null
        idFrontPath = null
        licenceFrontPath_ = null
        licenceBackPath_ = null

        personalImgUri = null
        idBackUri = null
        idFrontUri= null
        licenceFrontUri = null
        licenceBackUri = null

    }



}