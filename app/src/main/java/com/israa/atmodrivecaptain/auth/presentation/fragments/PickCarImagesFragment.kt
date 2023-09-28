package com.israa.atmodrivecaptain.auth.presentation.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.israa.atmodrivecaptain.R
import com.israa.atmodrivecaptain.auth.data.model.DeleteModel
import com.israa.atmodrivecaptain.auth.data.model.UploadImageResponse
import com.israa.atmodrivecaptain.auth.presentation.viewmodels.VehicleInfoViewModel
import com.israa.atmodrivecaptain.auth.presentation.viewmodels.VehicleInfoViewModel.Companion.BACK_SIDE_FLAG
import com.israa.atmodrivecaptain.auth.presentation.viewmodels.VehicleInfoViewModel.Companion.FIRST_SIDE_FLAG
import com.israa.atmodrivecaptain.auth.presentation.viewmodels.VehicleInfoViewModel.Companion.FRONT_SIDE_FLAG
import com.israa.atmodrivecaptain.auth.presentation.viewmodels.VehicleInfoViewModel.Companion.SECOND_SIDE_FLAG
import com.israa.atmodrivecaptain.auth.presentation.viewmodels.VehicleInfoViewModel.Companion.SIDE_FLAG
import com.israa.atmodrivecaptain.auth.presentation.viewmodels.VehicleInfoViewModel.Companion.TOP_SIDE_FLAG
import com.israa.atmodrivecaptain.databinding.FragmentPickCarImagesBinding
import com.israa.atmodrivecaptain.utils.Progressbar
import com.israa.atmodrivecaptain.utils.UiState
import com.israa.atmodrivecaptain.utils.exhaustive
import com.israa.atmodrivecaptain.utils.pickImage
import com.israa.atmodrivecaptain.utils.uploadImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async

@Suppress("IMPLICIT_CAST_TO_ANY")
@AndroidEntryPoint
class PickCarImagesFragment (val listener:PickCarImagesListener): BottomSheetDialogFragment() {

    private var _binding: FragmentPickCarImagesBinding? = null
    private val binding get() = _binding!!

    private val vehicleInfoViewModel: VehicleInfoViewModel by activityViewModels()

    var pickCarImagesListener:PickCarImagesListener? = null


    init {
        pickCarImagesListener = listener
    }
    
   var leftSidePath:String? = null
   var rightSidePath:String? = null
   var frontSidePath:String? = null
   var backSidePath:String? = null
   var frontSeatPath:String? = null
   var backSeatPath:String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPickCarImagesBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onClick()
        navigate()
        onObserve()

    }

    private fun onObserve() {
        vehicleInfoViewModel.apply {
            leftPath.observe(viewLifecycleOwner) { path ->
                when (path) {
                    is UiState.Failure -> {
                        binding.progressLeftSide.isVisible = false
                        Toast.makeText(requireContext(), path.message, Toast.LENGTH_SHORT).show()

                    }
                    UiState.Loading -> {}
                    is UiState.Success -> {
                        leftSidePath = path.data as String
                        leftSideUri.observe(viewLifecycleOwner) { uri ->
                            binding.apply {
                                progressLeftSide.isVisible = false
                                imgInsertLeftSide.isVisible = false
                                imgDeleteLeftSide.isVisible = true
                                imgEditLeftSide.isVisible = true
                                imgLeftSide.setImageURI(uri)
                            }

                        }
                    }

                    else -> {}
                }.exhaustive
            }

            rightPath.observe(viewLifecycleOwner){ path->
                when(path){
                    is UiState.Failure ->{
                        binding.progressRightSide.isVisible = false
                        Toast.makeText(requireContext(), path.message, Toast.LENGTH_SHORT).show()
                    }
                    UiState.Loading -> {}
                    is UiState.Success -> {
                        rightSidePath = path.data as String
                        rightSideUri.observe(viewLifecycleOwner){uri ->
                            binding.apply {
                                progressRightSide.isVisible = false
                                imgRightSide.setImageURI(uri)
                                imgInsertRightSide.isVisible = false
                                imgDeleteRightSide.isVisible = true
                                imgEditRightSide.isVisible = true
                            }
                        }
                    }
                    else -> {}
                }.exhaustive
            }

            frontPath.observe(viewLifecycleOwner){ path ->
                when(path){
                    is UiState.Failure -> {
                        binding.progressFrontSide.isVisible = false
                        Toast.makeText(requireContext(), path.message, Toast.LENGTH_SHORT).show()
                    }
                    UiState.Loading -> {}
                    is UiState.Success -> {
                        frontSidePath = path.data as String
                        frontSideUri.observe(viewLifecycleOwner){uri ->
                            binding.apply {
                                progressFrontSide.isVisible = false
                                imgFrontSide.setImageURI(uri)
                                imgInsertFrontSide.isVisible = false
                                imgDeleteFrontSide.isVisible = true
                                imgEditFrontSide.isVisible = true
                            }
                       }
                    }
                    null -> {}
                }.exhaustive
            }

            backPath.observe(viewLifecycleOwner) { path ->
                when (path) {
                    is UiState.Success -> {
                        backSidePath = path.data as String
                        backSideUri.observe(viewLifecycleOwner) { uri ->
                            binding.apply {
                                progressBackSide.isVisible=false
                                imgBackSide.setImageURI(uri)
                                imgInsertBackSide.isVisible = false
                                imgDeleteBackSide.isVisible = true
                                imgEditBackSide.isVisible = true
                            }

                        }
                    }

                    is UiState.Failure -> {
                        binding.progressBackSide.isVisible = false
                        Toast.makeText(requireContext(), path.message, Toast.LENGTH_SHORT).show()
                    }

                    is UiState.Loading -> {}
                    else -> {}

                }.exhaustive

                topPath.observe(viewLifecycleOwner) { path ->
                    when(path){
                        is UiState.Failure ->{
                            binding.progressFrontSeat.isVisible = false
                            Toast.makeText(requireContext(), path.message, Toast.LENGTH_SHORT).show()
                        }
                        UiState.Loading -> {}
                        is UiState.Success -> {
                            frontSeatPath = path.data as String
                            topSideUri.observe(viewLifecycleOwner) { uri ->
                                binding.apply {
                                    progressFrontSeat.isVisible=false
                                    imgFrontSeat.setImageURI(uri)
                                    imgEditFrontSeat.isVisible = false
                                    imgDeleteFrontSeat.isVisible = true
                                    imgEditFrontSeat.isVisible = true
                                }
                            }
                        }
                        else -> {}
                    }.exhaustive


                }
                sidePath.observe(viewLifecycleOwner){path->
                    when(path){
                        is UiState.Failure -> {
                            binding.progressBackSeat.isVisible = false
                            Toast.makeText(requireContext(), path.message, Toast.LENGTH_SHORT).show()
                        }
                        UiState.Loading -> {}
                        is UiState.Success -> {
                            backSeatPath = path.data as String
                            sideUri.observe(viewLifecycleOwner){uri->
                                binding.apply {
                                    progressBackSeat.isVisible = false
                                    imgBackSeat.setImageURI(uri)
                                    imgInsertBackSeat.isVisible = false
                                    imgDeleteBackSeat.isVisible = true
                                    imgEditBackSeat.isVisible = true
                                }
                            }
                        }
                        else -> {}
                    }.exhaustive

                }

            }


        }
    }

        private fun navigate() {

        }

        private fun onClick() {
            binding.apply {

                btnConfirm.setOnClickListener {
                    pickCarImagesListener?.onConfirm(leftSidePath,rightSidePath,frontSidePath
                    ,backSidePath,frontSeatPath,backSeatPath)

                    dismiss()
                }
                close.setOnClickListener {
                    leftSidePath?.let {
                        vehicleInfoViewModel.deleteImage(DeleteModel(listOf(it)), FIRST_SIDE_FLAG)
                    }

                    leftSidePath = null

                    rightSidePath?.let {
                        vehicleInfoViewModel.deleteImage(DeleteModel(listOf(it)), SECOND_SIDE_FLAG)
                    }
                    rightSidePath = null

                    frontSidePath?.let {
                        vehicleInfoViewModel.deleteImage(DeleteModel(listOf(it)), FRONT_SIDE_FLAG)
                    }
                    frontSidePath = null

                    backSidePath?.let {
                        vehicleInfoViewModel.deleteImage(DeleteModel(listOf(it)), BACK_SIDE_FLAG)
                    }
                    backSidePath = null
                    frontSeatPath?.let {
                        vehicleInfoViewModel.deleteImage(DeleteModel(listOf(it)), TOP_SIDE_FLAG)
                    }
                    frontSeatPath = null
                    backSeatPath?.let {
                        vehicleInfoViewModel.deleteImage(DeleteModel(listOf(it)), SIDE_FLAG)
                    }
                    backSeatPath = null
                    dismiss()
                }

                imgInsertLeftSide.setOnClickListener {
                    pickImage(FIRST_SIDE_FLAG)
                }
                imgEditLeftSide.setOnClickListener {
                    pickImage(FIRST_SIDE_FLAG)
                }

                imgInsertBackSide.setOnClickListener {
                    pickImage(BACK_SIDE_FLAG)
                }
                imgEditBackSide.setOnClickListener {
                    pickImage(BACK_SIDE_FLAG)
                }

                imgInsertFrontSeat.setOnClickListener {
                    pickImage(TOP_SIDE_FLAG)
                }
                imgEditFrontSeat.setOnClickListener {
                    pickImage(TOP_SIDE_FLAG)
                }

                imgInsertRightSide.setOnClickListener {
                    pickImage(SECOND_SIDE_FLAG)
                }
                imgEditRightSide.setOnClickListener {
                    pickImage(SECOND_SIDE_FLAG)
                }
                imgInsertFrontSide.setOnClickListener {
                    pickImage(FRONT_SIDE_FLAG)
                }
                imgEditFrontSide.setOnClickListener {
                    pickImage(FRONT_SIDE_FLAG)
                }

                imgInsertBackSeat.setOnClickListener {
                    pickImage(SIDE_FLAG)
                }
                imgEditBackSeat.setOnClickListener {
                    pickImage(SIDE_FLAG)
                }

                imgDeleteLeftSide.setOnClickListener {
                    vehicleInfoViewModel.deleteImage(DeleteModel(listOf(leftSidePath)), FIRST_SIDE_FLAG)
                    onPathDeleted(FIRST_SIDE_FLAG)
                }
                imgDeleteRightSide.setOnClickListener {
                    vehicleInfoViewModel.deleteImage(DeleteModel(listOf(rightSidePath)), SECOND_SIDE_FLAG)
                    onPathDeleted(SECOND_SIDE_FLAG)
                }
                imgDeleteFrontSide.setOnClickListener {
                    vehicleInfoViewModel.deleteImage(DeleteModel(listOf(frontSidePath)), FRONT_SIDE_FLAG)
                    onPathDeleted(FRONT_SIDE_FLAG)
                }
                imgDeleteBackSide.setOnClickListener {
                    vehicleInfoViewModel.deleteImage(DeleteModel(listOf(backSidePath)), BACK_SIDE_FLAG)
                    onPathDeleted(BACK_SIDE_FLAG)
                }
                imgDeleteFrontSeat.setOnClickListener {
                    vehicleInfoViewModel.deleteImage(DeleteModel(listOf(frontSeatPath)), TOP_SIDE_FLAG)
                    onPathDeleted(TOP_SIDE_FLAG)
                }
                imgDeleteBackSeat.setOnClickListener {
                    vehicleInfoViewModel.deleteImage(DeleteModel(listOf(backSeatPath)), SIDE_FLAG)
                    onPathDeleted(SIDE_FLAG)
                }

            }
        }


    private fun onPathDeleted(flag:Int){
        vehicleInfoViewModel.deleteImage.observe(viewLifecycleOwner){
            when(it){
                is UiState.Failure ->
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                UiState.Loading ->{}
                is UiState.Success ->{
                    when(flag){
                        FIRST_SIDE_FLAG ->{
                            binding.apply {
                                imgLeftSide.setImageDrawable(resources.getDrawable(R.drawable.left_side))
                                imgEditLeftSide.isVisible = false
                                imgDeleteLeftSide.isVisible = false
                                imgInsertLeftSide.isVisible = true
                            }
                            leftSidePath = null
                        }
                        SECOND_SIDE_FLAG -> {
                            binding.apply {
                                imgRightSide.setImageDrawable(resources.getDrawable(R.drawable.right_side))
                                imgEditRightSide.isVisible = false
                                imgDeleteRightSide.isVisible = false
                                imgInsertRightSide.isVisible = true
                            }
                            rightSidePath = null
                        }
                        FRONT_SIDE_FLAG ->{
                            binding.apply {
                                imgFrontSide.setImageDrawable(resources.getDrawable(R.drawable.front_side))
                                imgEditFrontSide.isVisible = false
                                imgDeleteFrontSide.isVisible = false
                                imgInsertFrontSide.isVisible = true
                            }

                            frontSidePath = null
                        }
                        BACK_SIDE_FLAG ->{
                            binding.apply {
                                imgBackSide.setImageDrawable(resources.getDrawable(R.drawable.back_side))
                                imgEditBackSide.isVisible = false
                                imgDeleteFrontSide.isVisible = false
                                imgInsertBackSide.isVisible = true
                            }
                            backSidePath = null
                        }
                        TOP_SIDE_FLAG ->{
                            binding.apply {
                                imgFrontSeat.setImageDrawable(resources.getDrawable(R.drawable.front_seat))
                                imgEditFrontSeat.isVisible = false
                                imgDeleteFrontSeat.isVisible = false
                                imgInsertFrontSeat.isVisible = true
                            }
                            frontSeatPath = null
                        }
                        SIDE_FLAG ->{
                            binding.apply {
                                imgBackSeat.setImageDrawable(resources.getDrawable(R.drawable.back_seat))
                                imgEditBackSeat.isVisible = false
                                imgDeleteBackSeat.isVisible = false
                                imgInsertBackSeat.isVisible = true
                            }

                            backSeatPath = null
                        }
                        else ->{}

                    }
                }
                else ->{}
            }.exhaustive
        }
    }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)

            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == FIRST_SIDE_FLAG) {

                    if (leftSidePath != null){
                        vehicleInfoViewModel.resetPath(FIRST_SIDE_FLAG)
                    }

                    binding.progressLeftSide.isVisible = true
                    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                        val pair = uploadImage(data?.data)
                        vehicleInfoViewModel.uploadImage(pair.first, pair.second, FIRST_SIDE_FLAG)
                    }

                    vehicleInfoViewModel.setImageUriValue(data?.data!!, FIRST_SIDE_FLAG)


                } else if (requestCode == BACK_SIDE_FLAG) {


                    if(backSidePath!=null){
                        vehicleInfoViewModel.resetPath(BACK_SIDE_FLAG)
                    }
                    binding.progressBackSide.isVisible=true
                    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                        val pair = uploadImage(data?.data)
                        vehicleInfoViewModel.uploadImage(pair.first, pair.second, BACK_SIDE_FLAG)
                    }

                    vehicleInfoViewModel.setImageUriValue(data?.data!!, BACK_SIDE_FLAG)


                } else if (requestCode == TOP_SIDE_FLAG) {

                    if(frontSeatPath!=null){
                        vehicleInfoViewModel.resetPath(TOP_SIDE_FLAG)
                    }

                    binding.progressFrontSeat.isVisible=false
                    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                        val pair = uploadImage(data?.data)
                        vehicleInfoViewModel.uploadImage(pair.first, pair.second, TOP_SIDE_FLAG)
                    }
                    vehicleInfoViewModel.setImageUriValue(data?.data!!, TOP_SIDE_FLAG)

                } else if (requestCode == SECOND_SIDE_FLAG) {

                    if(rightSidePath!=null){
                        vehicleInfoViewModel.resetPath(SECOND_SIDE_FLAG)
                    }

                    binding.progressRightSide.isVisible = true
                    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                        val pair = uploadImage(data?.data)
                        vehicleInfoViewModel.uploadImage(pair.first, pair.second, SECOND_SIDE_FLAG)
                    }

                    vehicleInfoViewModel.setImageUriValue(data?.data!!, SECOND_SIDE_FLAG)


                } else if (requestCode == FRONT_SIDE_FLAG) {

                    if(frontSidePath!=null){
                        vehicleInfoViewModel.resetPath(FRONT_SIDE_FLAG)
                    }

                    binding.progressFrontSide.isVisible = true
                    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                        val pair = uploadImage(data?.data)
                        vehicleInfoViewModel.uploadImage(pair.first, pair.second, FRONT_SIDE_FLAG)
                    }

                    vehicleInfoViewModel.setImageUriValue(data?.data!!, FRONT_SIDE_FLAG)

                } else if (requestCode ==SIDE_FLAG) {

                    if(backSeatPath !=null){
                        vehicleInfoViewModel.resetPath(SIDE_FLAG)
                    }
                    binding.progressBackSeat.isVisible = true
                    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                        val pair = uploadImage(data?.data)
                        vehicleInfoViewModel.uploadImage(pair.first, pair.second, SIDE_FLAG)
                    }
                    vehicleInfoViewModel.setImageUriValue(data?.data!!, SIDE_FLAG)

                }
            }
        }



    interface PickCarImagesListener{

        fun onConfirm( leftSidePath: String?,
                       rightSidePath: String?,
                       frontSidePath: String?,
                       backSidePath: String?,
                       frontSeatPath: String?,
                       backSeatPath: String?)

    }

}