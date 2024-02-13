package com.israa.atmodrivecaptain.auth.presentation.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.israa.atmodrivecaptain.R
import com.israa.atmodrivecaptain.auth.data.model.DeleteModel
import com.israa.atmodrivecaptain.auth.presentation.viewmodels.VehicleInfoViewModel
import com.israa.atmodrivecaptain.auth.presentation.viewmodels.VehicleInfoViewModel.Companion.BACK_SIDE_FLAG
import com.israa.atmodrivecaptain.auth.presentation.viewmodels.VehicleInfoViewModel.Companion.LEFT_SIDE_FLAG
import com.israa.atmodrivecaptain.auth.presentation.viewmodels.VehicleInfoViewModel.Companion.FRONT_SIDE_FLAG
import com.israa.atmodrivecaptain.auth.presentation.viewmodels.VehicleInfoViewModel.Companion.RIGHT_SIDE_FLAG
import com.israa.atmodrivecaptain.auth.presentation.viewmodels.VehicleInfoViewModel.Companion.BACK_SEAT_FLAG
import com.israa.atmodrivecaptain.auth.presentation.viewmodels.VehicleInfoViewModel.Companion.FRONT_SEAT_FLAG
import com.israa.atmodrivecaptain.databinding.FragmentPickCarImagesBinding
import com.israa.atmodrivecaptain.utils.FAILURE
import com.israa.atmodrivecaptain.utils.LOADING
import com.israa.atmodrivecaptain.utils.SUCCESS
import com.israa.atmodrivecaptain.utils.UiState
import com.israa.atmodrivecaptain.utils.pickImage
import com.israa.atmodrivecaptain.utils.showToast
import com.israa.atmodrivecaptain.utils.uploadImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.internal.toImmutableMap

@Suppress("IMPLICIT_CAST_TO_ANY")
@SuppressLint("UseCompatLoadingForDrawables")
@AndroidEntryPoint
class PickCarImagesFragment() : BottomSheetDialogFragment() {

    private var _binding: FragmentPickCarImagesBinding? = null
    private val binding get() = _binding!!

    private val vehicleInfoViewModel: VehicleInfoViewModel by activityViewModels()

    private lateinit var carImagesListener: CarImages

    private var leftSidePath: String? = null
    private var rightSidePath: String? = null
    private var frontSidePath: String? = null
    private var backSidePath: String? = null
    private var frontSeatPath: String? = null
    private var backSeatPath: String? = null

    private var leftSideUri: Uri? = null
    private var rightSideUri: Uri? = null
    private var frontSideUri: Uri? = null
    private var backSideUri: Uri? = null
    private var frontSeatUri: Uri? = null
    private var backSeatUri: Uri? = null

    private  var deletedImagesList :MutableMap<Int,String>? = null
    private  var insertedImagesList :MutableMap<Int,Uri>? =  null

    private var deleteLeftSide: Boolean = false
    private var deleteRightSide: Boolean = false
    private var deleteFrontSide: Boolean = false
    private var deleteBackSide: Boolean = false
    private var deleteFrontSeat: Boolean = false
    private var deleteBackSeat: Boolean = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPickCarImagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onClick()
        onObserveOnPaths()
        onObserveOnUris()

    }

//    private fun enableConfirmButton() {
//        binding.btnConfirm.isEnabled = (insertedImagesList?.size == 6) || ( leftSidePath != null && rightSidePath != null &&
//                frontSidePath != null && backSidePath != null && frontSeatPath != null && backSeatPath != null)
//    }

    private fun initLists() {
        if(insertedImagesList == null)
            insertedImagesList = mutableMapOf()
        if(deletedImagesList == null)
            deletedImagesList = mutableMapOf()
    }

    private fun onObserveOnPaths() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            vehicleInfoViewModel.apply {

                leftPath.observe(viewLifecycleOwner) { path ->
                    when (path) {
                        is UiState.Failure -> {
                            leftSideState(FAILURE)
                            showToast(path.message)

                        }

                        UiState.Loading -> {
                            leftSideState(LOADING)
                        }

                        is UiState.Success -> {
                            leftSidePath = path.data as String
                            vehicleInfoViewModel.setImageUriValue(this@PickCarImagesFragment.leftSideUri, LEFT_SIDE_FLAG)
                           leftSideState(SUCCESS)
                            insertedImagesList?.remove(LEFT_SIDE_FLAG)
                        }

                        null -> {
                            leftSidePath = null
                            deleteLeftSide = false
                            vehicleInfoViewModel.setImageUriValue(null, LEFT_SIDE_FLAG)
                            deletedImagesList?.remove(LEFT_SIDE_FLAG)
                            leftSideState(null)
                        }
                    }
                    check()

                }

                rightPath.observe(viewLifecycleOwner) { path ->
                    when (path) {
                        is UiState.Failure -> {
                            rightSideState(FAILURE)
                        }

                        UiState.Loading -> {
                            rightSideState(LOADING)
                        }

                        is UiState.Success -> {
                            rightSidePath = path.data as String
                            vehicleInfoViewModel.setImageUriValue(this@PickCarImagesFragment.rightSideUri, RIGHT_SIDE_FLAG)
                            rightSideState(SUCCESS)
                            insertedImagesList?.remove(RIGHT_SIDE_FLAG)
                        }

                        null -> {
                            rightSidePath = null
                            deleteRightSide = false
                            vehicleInfoViewModel.setImageUriValue(null, RIGHT_SIDE_FLAG)
                            deletedImagesList?.remove(RIGHT_SIDE_FLAG)
                            rightSideState(null)
                        }
                    }
                    check()
                }

                frontPath.observe(viewLifecycleOwner) { path ->
                    when (path) {
                        is UiState.Failure -> {
                            frontSideState(FAILURE)
                            showToast(path.message)
                        }

                        UiState.Loading -> {
                           frontSideState(LOADING)
                        }

                        is UiState.Success -> {
                            frontSidePath = path.data as String
                            vehicleInfoViewModel.setImageUriValue(this@PickCarImagesFragment.frontSideUri,
                                FRONT_SIDE_FLAG)
                            frontSideState(SUCCESS)

                            insertedImagesList?.remove(FRONT_SIDE_FLAG)
                        }

                        null -> {
                            frontSidePath = null
                            deleteFrontSide = false
                            vehicleInfoViewModel.setImageUriValue(null, FRONT_SIDE_FLAG)
                            deletedImagesList?.remove(FRONT_SIDE_FLAG)
                            frontSideState(null)
                        }
                    }
                    check()
                }

                backPath.observe(viewLifecycleOwner) { path ->
                    when (path) {
                        is UiState.Success -> {
                            backSidePath = path.data as String
                            vehicleInfoViewModel.setImageUriValue(this@PickCarImagesFragment.backSideUri,
                                BACK_SIDE_FLAG)
                            backSideState(SUCCESS)

                            insertedImagesList?.remove(BACK_SIDE_FLAG)
                        }

                        is UiState.Failure -> {
                            backSideState(FAILURE)
                            showToast(path.message)
                        }

                        is UiState.Loading -> {
                          backSideState(LOADING)
                        }

                        null -> {
                            backSidePath = null
                            deleteBackSide = false
                            vehicleInfoViewModel.setImageUriValue(null, BACK_SIDE_FLAG)
                            deletedImagesList?.remove(BACK_SIDE_FLAG)
                            backSideState(null)

                        }

                    }
                    check()
                }

                frontSeatPath.observe(viewLifecycleOwner) { path ->
                    when (path) {
                        is UiState.Failure -> {
                           frontSeatState(FAILURE)
                            showToast(path.message)
                        }

                        UiState.Loading -> {
                            frontSeatState(LOADING)
                        }

                        is UiState.Success -> {
                            this@PickCarImagesFragment.frontSeatPath = path.data as String
                            vehicleInfoViewModel.setImageUriValue(this@PickCarImagesFragment.frontSeatUri,
                                FRONT_SEAT_FLAG)
                            frontSeatState(SUCCESS)
                            insertedImagesList?.remove(FRONT_SEAT_FLAG)

                        }

                        null -> {
                            this@PickCarImagesFragment.frontSeatPath = null
                            deleteFrontSeat = false
                            vehicleInfoViewModel.setImageUriValue(null, FRONT_SEAT_FLAG)
                            deletedImagesList?.remove(FRONT_SEAT_FLAG)
                            frontSeatState(null)
                        }
                    }
                    check()
                }
                backSeatPath.observe(viewLifecycleOwner) { path ->
                    when (path) {
                        is UiState.Failure -> {
                            backSeatState(FAILURE)
                            showToast(path.message)
                        }

                        UiState.Loading -> {
                            backSeatState(LOADING)
                        }

                        is UiState.Success -> {
                            this@PickCarImagesFragment.backSeatPath = path.data as String
                            vehicleInfoViewModel.setImageUriValue(this@PickCarImagesFragment.backSeatUri,
                                BACK_SEAT_FLAG)
                            backSeatState(SUCCESS)
                            insertedImagesList?.remove(BACK_SEAT_FLAG)

                        }

                        null -> {
                            this@PickCarImagesFragment.backSeatPath = null
                            deleteBackSeat = false
                            vehicleInfoViewModel.setImageUriValue(null, BACK_SEAT_FLAG)
                            deletedImagesList?.remove(BACK_SEAT_FLAG)
                            backSeatState(null)
                        }
                    }
                    check()

                }

            }


        }
    }

    private fun backSeatState(state: Int?) {
        binding.apply {
            progressBackSeat.isVisible = state == LOADING
            imgRetryBackSeat.isVisible = state == FAILURE
            imgInsertBackSeat.isVisible = state == null
            imgEditBackSeat.isVisible = state == SUCCESS
            imgDeleteBackSeat.isVisible = state == SUCCESS
        }
    }

    private fun frontSeatState(state: Int?) {
        binding.apply {
            progressFrontSeat.isVisible = state == LOADING
            imgRetryFrontSeat.isVisible = state == FAILURE
            imgInsertFrontSeat.isVisible = state == null
            imgEditFrontSeat.isVisible = state == SUCCESS
            imgDeleteFrontSeat.isVisible = state == SUCCESS
        }
    }

    private fun backSideState(state: Int?) {
        binding.apply {
            progressBackSide.isVisible = state == LOADING
            imgRetryBackSide.isVisible = state == FAILURE
            imgInsertBackSide.isVisible = state == null
            imgEditBackSide.isVisible = state == SUCCESS
            imgDeleteBackSide.isVisible = state == SUCCESS
        }
    }

    private fun frontSideState(state: Int?) {
        binding.apply {
            progressFrontSide.isVisible = state == LOADING
            imgRetryFrontSide.isVisible = state == FAILURE
            imgInsertFrontSide.isVisible = state == null
            imgEditFrontSide.isVisible = state == SUCCESS
            imgDeleteFrontSide.isVisible = state == SUCCESS
        }
    }

    private fun rightSideState(state: Int?) {
        binding.apply {
            progressRightSide.isVisible = state == LOADING
            imgRetryRightSide.isVisible = state == FAILURE
            imgInsertRightSide.isVisible = state == null
            imgEditRightSide.isVisible = state == SUCCESS
            imgDeleteRightSide.isVisible = state == SUCCESS
        }
    }

    private fun leftSideState(state: Int?) {
        binding.apply {
            progressLeftSide.isVisible = state == LOADING
            imgRetryLeftSide.isVisible = state == FAILURE
            imgInsertLeftSide.isVisible = state == null
            imgEditLeftSide.isVisible = state == SUCCESS
            imgDeleteLeftSide.isVisible = state == SUCCESS
        }
    }

    private fun onObserveOnUris() {
        viewLifecycleOwner.lifecycleScope.launch {
            vehicleInfoViewModel.apply {
                leftSideUri.observe(viewLifecycleOwner) { uri ->
                    binding.imgLeftSide.setImageURI(uri)

                }
                rightSideUri.observe(viewLifecycleOwner) { uri ->
                    binding.imgRightSide.setImageURI(uri)

                }

                frontSideUri.observe(viewLifecycleOwner) { uri ->
                    binding.imgFrontSide.setImageURI(uri)

                }

                backSideUri.observe(viewLifecycleOwner) { uri ->
                    binding.imgBackSide.setImageURI(uri)
                }

                frontSeatUri.observe(viewLifecycleOwner) { uri ->
                    binding.imgFrontSeat.setImageURI(uri)

                }
                sideUri.observe(viewLifecycleOwner) { uri ->
                    binding.imgBackSeat.setImageURI(uri)
                }

            }

        }
    }


    private fun onClick() {
        binding.apply {

            btnConfirm.setOnClickListener {

                viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                    insertedImagesList?.let {
                        val list = insertedImagesList!!.toImmutableMap()
                        for (element in list){
                            val pair = uploadImage(element.value)
                            vehicleInfoViewModel.uploadImage(pair.first, pair.second, element.key)
                        }
                    }

                    deletedImagesList?.let {
                        if(it.isNotEmpty()){
                            vehicleInfoViewModel.deleteImage(DeleteModel((it.values.toList())),it.keys.toList())
                        }
                    }
                }

                if(insertedImagesList == null && deletedImagesList == null)
                    dismiss()

            }
            btnClose.setOnClickListener {
                dismiss()

            }

            imgInsertLeftSide.setOnClickListener {
                pickImage(LEFT_SIDE_FLAG)
            }
            imgEditLeftSide.setOnClickListener {
                pickImage(LEFT_SIDE_FLAG)
            }

            imgInsertBackSide.setOnClickListener {
                pickImage(BACK_SIDE_FLAG)
            }
            imgEditBackSide.setOnClickListener {
                pickImage(BACK_SIDE_FLAG)
            }

            imgInsertFrontSeat.setOnClickListener {
                pickImage(FRONT_SEAT_FLAG)
            }
            imgEditFrontSeat.setOnClickListener {
                pickImage(FRONT_SEAT_FLAG)
            }

            imgInsertRightSide.setOnClickListener {
                pickImage(RIGHT_SIDE_FLAG)
            }
            imgEditRightSide.setOnClickListener {
                pickImage(RIGHT_SIDE_FLAG)
            }
            imgInsertFrontSide.setOnClickListener {
                pickImage(FRONT_SIDE_FLAG)
            }
            imgEditFrontSide.setOnClickListener {
                pickImage(FRONT_SIDE_FLAG)
            }

            imgInsertBackSeat.setOnClickListener {
                pickImage(BACK_SEAT_FLAG)
            }
            imgEditBackSeat.setOnClickListener {
                pickImage(BACK_SEAT_FLAG)
            }

            imgDeleteLeftSide.setOnClickListener {
                deleteLeftSide = true
                setLeftSideImage(null)
                initLists()
                leftSidePath?.let { deletedImagesList!![LEFT_SIDE_FLAG] = it } ?: insertedImagesList!!.remove(LEFT_SIDE_FLAG)
            }
            imgDeleteRightSide.setOnClickListener{
                deleteRightSide = true
                setRightSideImage(null)
                initLists()
                rightSidePath?.let { deletedImagesList!![RIGHT_SIDE_FLAG] = it } ?: insertedImagesList!!.remove(
                    RIGHT_SIDE_FLAG)
            }
            imgDeleteFrontSide.setOnClickListener {
                deleteFrontSide = true
                setFrontSideImage(null)
                initLists()
                frontSidePath?.let { deletedImagesList!![FRONT_SIDE_FLAG] = it } ?: insertedImagesList!!.remove(
                    FRONT_SIDE_FLAG)
            }
            imgDeleteBackSide.setOnClickListener {
                deleteBackSide = true
                setBackSideImage(null)
                initLists()
                backSidePath?.let { deletedImagesList!![BACK_SIDE_FLAG] = it } ?: insertedImagesList!!.remove(
                    BACK_SIDE_FLAG)
            }
            imgDeleteFrontSeat.setOnClickListener {
                deleteFrontSeat = true
                setFrontSeatImage(null)
                initLists()
                frontSeatPath?.let { deletedImagesList!![FRONT_SEAT_FLAG] = it } ?: insertedImagesList!!.remove(
                    FRONT_SEAT_FLAG)
            }
            imgDeleteBackSeat.setOnClickListener {
                deleteBackSeat = true
                setBackSeatImage(null)
                initLists()
                backSeatPath?.let { deletedImagesList!![BACK_SEAT_FLAG] = it } ?: insertedImagesList!!.remove(
                    BACK_SEAT_FLAG)
            }

            imgRetryLeftSide.setOnClickListener {
                imgRetryLeftSide.isVisible = false
                if(deleteLeftSide){
                    vehicleInfoViewModel.deleteImage(
                        DeleteModel(listOf(leftSidePath)),
                        LEFT_SIDE_FLAG
                    )
                }else{
                    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                        val pair = uploadImage(leftSideUri)
                        vehicleInfoViewModel.uploadImage(
                            pair.first, pair.second,
                            LEFT_SIDE_FLAG
                        )
                    }
                }
            }

            imgRetryRightSide.setOnClickListener {
                imgRetryRightSide.isVisible = false

                if(deleteRightSide){
                    vehicleInfoViewModel.deleteImage(
                        DeleteModel(listOf(rightSidePath)),
                        RIGHT_SIDE_FLAG
                    )
                }else{
                    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                        val pair = uploadImage(rightSideUri)
                        vehicleInfoViewModel.uploadImage(
                            pair.first, pair.second,
                            RIGHT_SIDE_FLAG
                        )
                    }
                }
            }


            imgRetryFrontSeat.setOnClickListener {
                imgRetryFrontSeat.isVisible = false
                if(deleteFrontSeat){
                    vehicleInfoViewModel.deleteImage(
                        DeleteModel(listOf(frontSeatPath)),
                        FRONT_SEAT_FLAG
                    )
                }else{
                    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                        val pair = uploadImage(frontSeatUri)
                        vehicleInfoViewModel.uploadImage(
                            pair.first, pair.second,
                            FRONT_SEAT_FLAG
                        )
                    }
                }
            }

            imgRetryBackSeat.setOnClickListener {
                imgRetryBackSeat.isVisible = false
                if(deleteBackSeat){
                    vehicleInfoViewModel.deleteImage(
                        DeleteModel(listOf(backSeatPath)),
                        BACK_SEAT_FLAG
                    )
                }else{
                    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                        val pair = uploadImage(backSeatUri)
                        vehicleInfoViewModel.uploadImage(
                            pair.first, pair.second,
                            BACK_SEAT_FLAG
                        )
                    }
                }
            }

            imgRetryFrontSide.setOnClickListener {
                imgRetryFrontSide.isVisible = false
                if(deleteFrontSide){
                    vehicleInfoViewModel.deleteImage(
                        DeleteModel(listOf(frontSidePath)),
                        FRONT_SIDE_FLAG
                    )
                }else{
                    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                        val pair = uploadImage(frontSideUri)
                        vehicleInfoViewModel.uploadImage(
                            pair.first, pair.second,
                            FRONT_SIDE_FLAG
                        )
                    }
                }
            }

            imgRetryBackSide.setOnClickListener {
                imgRetryBackSide.isVisible = false
                if(deleteBackSide){
                    vehicleInfoViewModel.deleteImage(
                        DeleteModel(listOf(backSidePath)),
                        BACK_SIDE_FLAG
                    )
                }else{
                    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                        val pair = uploadImage(backSideUri)
                        vehicleInfoViewModel.uploadImage(
                            pair.first, pair.second,
                            BACK_SIDE_FLAG
                        )
                    }
                }
            }
        }
    }



    private fun check() {
        if(insertedImagesList != null && deletedImagesList != null){
            if(insertedImagesList!!.isEmpty()  && deletedImagesList!!.isEmpty()) {

                carImagesListener.onImagesConfirmed(
                    leftSidePath,
                    rightSidePath,
                    frontSidePath,
                    backSidePath,
                    frontSeatPath,
                    backSeatPath
                )

                carImagesListener.confirmedImagesUris(
                    leftSideUri,
                    rightSideUri,
                    frontSideUri,
                    backSideUri,
                    frontSeatUri,
                    backSeatUri
                )
                dismiss()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                LEFT_SIDE_FLAG -> {
                    setLeftSideImage(data?.data)
                    initLists()
                    insertedImagesList!![LEFT_SIDE_FLAG]= leftSideUri!!

                }
                RIGHT_SIDE_FLAG -> {
                    setRightSideImage(data?.data)
                    initLists()
                    insertedImagesList!![RIGHT_SIDE_FLAG]= rightSideUri!!
                }
                BACK_SIDE_FLAG -> {
                    setBackSideImage(data?.data)
                    initLists()
                    insertedImagesList!![BACK_SIDE_FLAG]= backSideUri!!

                }
                FRONT_SIDE_FLAG -> {
                    setFrontSideImage(data?.data)
                    initLists()
                    insertedImagesList!![FRONT_SIDE_FLAG] = frontSideUri!!

                }
                FRONT_SEAT_FLAG -> {
                    setFrontSeatImage(data?.data)
                    initLists()
                    insertedImagesList!![FRONT_SEAT_FLAG]= frontSeatUri!!
                }


                BACK_SEAT_FLAG -> {

                    setBackSeatImage(data?.data)
                    initLists()
                    insertedImagesList!![BACK_SEAT_FLAG] = backSeatUri!!

                }
            }
        }
    }

    private fun setLeftSideImage(data: Uri?) {
            binding.apply {
                leftSideUri = data
                leftSideUri?.let { imgLeftSide.setImageURI(it) } ?:  imgLeftSide.setImageDrawable(
                    resources.getDrawable(
                        R.drawable.left_side,
                        null
                    )
                )
                imgInsertLeftSide.isVisible = leftSideUri == null
                imgDeleteLeftSide.isVisible = leftSideUri != null
                imgEditLeftSide.isVisible = leftSideUri != null
            }


    }

    private fun setRightSideImage(data: Uri?) {
        rightSideUri = data
        binding.apply {
            rightSideUri?.let { imgRightSide.setImageURI(it) } ?:  imgRightSide.setImageDrawable(
                resources.getDrawable(
                    R.drawable.right_side,
                    null
                )
            )
            imgInsertRightSide.isVisible = rightSideUri == null
            imgDeleteRightSide.isVisible = rightSideUri != null
            imgEditRightSide.isVisible = rightSideUri != null
        }

    }

    private fun setFrontSideImage(data: Uri?) {
        frontSideUri = data
        binding.apply {
            frontSideUri?.let { imgFrontSide.setImageURI(it) } ?:  imgFrontSide.setImageDrawable(
                resources.getDrawable(
                    R.drawable.front_side,
                    null
                )
            )
            imgInsertFrontSide.isVisible = frontSideUri == null
            imgDeleteFrontSide.isVisible = frontSideUri != null
            imgEditFrontSide.isVisible = frontSideUri != null
        }

    }
    private fun setBackSideImage(data: Uri?) {
        backSideUri = data
        binding.apply {
            backSideUri?.let { imgBackSide.setImageURI(it) } ?:  imgBackSide.setImageDrawable(
                resources.getDrawable(
                    R.drawable.back_side,
                    null
                )
            )
            imgInsertBackSide.isVisible = backSideUri == null
            imgDeleteBackSide.isVisible = backSideUri != null
            imgEditBackSide.isVisible = backSideUri != null
        }

    }

    private fun setFrontSeatImage(data: Uri?) {
        frontSeatUri = data
        binding.apply {
            frontSeatUri?.let { imgFrontSeat.setImageURI(it) } ?:  imgFrontSeat.setImageDrawable(
                resources.getDrawable(
                    R.drawable.front_seat,
                    null
                )
            )
            imgInsertFrontSeat.isVisible = frontSeatUri == null
            imgDeleteFrontSeat.isVisible = frontSeatUri != null
            imgEditFrontSeat.isVisible = frontSeatUri != null
        }

    }

    private fun setBackSeatImage(data: Uri?) {
        backSeatUri = data
        binding.apply {
            backSeatUri?.let { imgBackSeat.setImageURI(it) } ?:  imgBackSeat.setImageDrawable(
                resources.getDrawable(
                    R.drawable.back_seat,
                    null
                )
            )
            imgInsertBackSeat.isVisible = backSeatUri == null
            imgDeleteBackSeat.isVisible = backSeatUri != null
            imgEditBackSeat.isVisible = backSeatUri != null
        }

    }



    fun setCarImagesListener(carImagesListener: CarImages) {
        this.carImagesListener = carImagesListener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        insertedImagesList = null
        deletedImagesList = null
    }
}

interface CarImages {
    fun onImagesConfirmed(
        leftSidePath: String?,
        rightSidePath: String?,
        frontSidePath: String?,
        backSidePath: String?,
        frontSeatPath: String?,
        backSeatPath: String?
    )

    fun confirmedImagesUris(
        leftSideUri: Uri?,
        rightSideUri: Uri?,
        frontSideUri: Uri?,
        backSideUri: Uri?,
        frontSeatUri: Uri?,
        backSeatUri: Uri?
    )
}