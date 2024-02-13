package com.israa.atmodrivecaptain.auth.presentation.viewmodels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.israa.atmodrive.auth.data.datasource.remote.ResponseState
import com.israa.atmodrive.auth.domain.usecase.AuthUseCase
import com.israa.atmodrivecaptain.auth.data.model.DeleteModel
import com.israa.atmodrivecaptain.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class VehicleInfoViewModel @Inject constructor(private val authUseCase: AuthUseCase) : ViewModel() {

    companion object {
        const val LEFT_SIDE_FLAG = 1
        const val RIGHT_SIDE_FLAG = 2
        const val BACK_SIDE_FLAG = 4
        const val FRONT_SIDE_FLAG = 5
        const val FRONT_SEAT_FLAG = 3
        const val BACK_SEAT_FLAG = 6
        const val VEHICLE_LICENCE_FRONT_FLAG = 7
        const val VEHICLE_LICENCE_BACK_FLAG = 8
    }

    private var _frontPath: MutableLiveData<UiState?> = MutableLiveData(null)
    val frontPath: LiveData<UiState?> = _frontPath


    private var _backPath: MutableLiveData<UiState?> = MutableLiveData(null)
    val backPath: LiveData<UiState?> = _backPath


    private var _frontSeatPath: MutableLiveData<UiState?> = MutableLiveData(null)
    val frontSeatPath: LiveData<UiState?> = _frontSeatPath


    private var _rightPath: MutableLiveData<UiState?> = MutableLiveData(null)

    val rightPath: LiveData<UiState?> = _rightPath

    private var _leftPath: MutableLiveData<UiState?> = MutableLiveData(null)
    val leftPath: LiveData<UiState?> = _leftPath

    private var _backSeatPath: MutableLiveData<UiState?> = MutableLiveData(null)
    val backSeatPath: LiveData<UiState?> = _backSeatPath


    private var _licenceFrontPath: MutableLiveData<UiState?> = MutableLiveData(null)
    val licenceFrontPath: LiveData<UiState?> = _licenceFrontPath

    private var _licenceBackPath: MutableLiveData<UiState?> = MutableLiveData(null)
    val licenceBackPath: LiveData<UiState?> = _licenceBackPath


    private var _leftSideUri: MutableLiveData<Uri?> = MutableLiveData(null)
    val leftSideUri: LiveData<Uri?> = _leftSideUri

    private var _rightSideUri: MutableLiveData<Uri?> = MutableLiveData(null)
    val rightSideUri: LiveData<Uri?> = _rightSideUri

    private var _frontSideUri: MutableLiveData<Uri?> = MutableLiveData(null)
    val frontSideUri: LiveData<Uri?> = _frontSideUri

    private var _backSideUri: MutableLiveData<Uri?> = MutableLiveData(null)
    val backSideUri: LiveData<Uri?> = _backSideUri

    private var _frontSeatUri: MutableLiveData<Uri?> = MutableLiveData(null)
    val frontSeatUri: LiveData<Uri?> = _frontSeatUri

    private var _sideUri: MutableLiveData<Uri?> = MutableLiveData(null)
    val sideUri: LiveData<Uri?> = _sideUri


    private var _licenceFrontUri: MutableLiveData<Uri?> = MutableLiveData(null)
    val licenceFrontUri: LiveData<Uri?> = _licenceFrontUri

    private var _licenceBackUri: MutableLiveData<Uri?> = MutableLiveData(null)
    val licenceBackUri: LiveData<Uri?> = _licenceBackUri

    private var _registerVehicle = Channel<UiState>()
    val registerVehicle = _registerVehicle.receiveAsFlow()


    private var _deleteImage: MutableLiveData<UiState?> = MutableLiveData()
    val deleteImage: LiveData<UiState?> = _deleteImage

    suspend fun uploadImage(part: MultipartBody.Part, path: RequestBody, flag: Int) {
        viewModelScope.launch(Dispatchers.IO){
            setImagesPaths(UiState.Loading,flag)
            when (val response = authUseCase.uploadImage(part, path)) {
                is ResponseState.Success -> {
                    setImagesPaths(UiState.Success(response.data.data!!),flag)

                }

                is ResponseState.Failure -> {
                    setImagesPaths(UiState.Failure(response.error),flag)
                }

                else -> {}
            }
        }
    }

    private fun setImagesPaths(state:UiState, flag: Int) {
        when (flag) {
            LEFT_SIDE_FLAG -> _leftPath.postValue(state)
            RIGHT_SIDE_FLAG -> _rightPath.postValue(state)
            FRONT_SIDE_FLAG -> _frontPath.postValue(state)
            BACK_SIDE_FLAG -> _backPath.postValue(state)
            FRONT_SEAT_FLAG -> _frontSeatPath.postValue(state)
            BACK_SEAT_FLAG -> _backSeatPath.postValue(state)
            VEHICLE_LICENCE_FRONT_FLAG -> _licenceFrontPath.postValue(state)
            VEHICLE_LICENCE_BACK_FLAG -> _licenceBackPath.postValue(state)
        }
    }


    fun setImageUriValue(uri: Uri?, flag: Int) {
        when (flag) {
            LEFT_SIDE_FLAG -> _leftSideUri.value = uri
            RIGHT_SIDE_FLAG -> _rightSideUri.value = uri
            FRONT_SIDE_FLAG -> _frontSideUri.value = uri
            BACK_SIDE_FLAG -> _backSideUri.value = uri
            FRONT_SEAT_FLAG -> _frontSeatUri.value = uri
            BACK_SEAT_FLAG -> _sideUri.value = uri
            VEHICLE_LICENCE_BACK_FLAG -> _licenceBackUri.value = uri
            VEHICLE_LICENCE_FRONT_FLAG -> _licenceFrontUri.value = uri
        }

    }


    fun registerVehicle(
        vehicleFront: String?,
        vehicleBack: String?,
        vehicleLeft: String?,
        vehicleRight: String?,
        vehicleFrontSeat: String?,
        vehicleBackSeat: String?,
        vehicleLicenseFront: String?,
        vehicleLicenseBack: String?
    ) {
        viewModelScope.launch {
            _registerVehicle.send(UiState.Loading)
            val response = authUseCase.registerVehicle(
                vehicleFront,
                vehicleBack,
                vehicleLeft,
                vehicleRight,
                vehicleFrontSeat,
                vehicleBackSeat,
                vehicleLicenseFront,
                vehicleLicenseBack
            )

            when (response) {
                is ResponseState.Success -> _registerVehicle.send(UiState.Success(response.data))
                is ResponseState.Failure -> _registerVehicle.send(UiState.Failure(response.error))
                else -> {}

            }
        }
    }


    fun deleteImage(path: DeleteModel, flag: Int) {

        viewModelScope.launch {
            setPath(UiState.Loading,flag)
            when (val response = authUseCase.deleteImage(path)) {
                is ResponseState.Success -> {
                   setPath(null,flag)

                }

                is ResponseState.Failure -> {
                   setPath(UiState.Failure(response.error),flag)

                }

                else -> {}
            }
        }

    }

    private fun setPath(state: UiState?, flag: Int) {
        when (flag) {
            LEFT_SIDE_FLAG -> {
                _leftPath.postValue(state)
            }

            RIGHT_SIDE_FLAG -> {
                _rightPath.postValue(state)
            }

            FRONT_SIDE_FLAG -> {
                _frontPath.postValue(state)
            }

            BACK_SIDE_FLAG -> {
                _backPath.postValue(state)
            }

            FRONT_SEAT_FLAG -> {
                _frontSeatPath.postValue(state)
            }

            BACK_SEAT_FLAG -> {
                _backSeatPath.postValue(state)
            }
            VEHICLE_LICENCE_FRONT_FLAG -> {
                _licenceFrontPath.postValue(state)
            }

            VEHICLE_LICENCE_BACK_FLAG -> {
                _licenceBackPath.postValue(state)
            }
        }
    }


    fun deleteImage(path: DeleteModel, flags: List<Int>) {

        viewModelScope.launch {
            setCarImagesPaths(UiState.Loading,flags)
            when (val response = authUseCase.deleteImage(path)) {
                is ResponseState.Success -> {
                    setCarImagesPaths(null,flags)
                }

                is ResponseState.Failure -> {
                    setCarImagesPaths(UiState.Failure(response.error),flags)
                }

                else -> {}
            }
        }

    }

    private fun setCarImagesPaths(state:UiState?, flags: List<Int>){
        for (flag in flags){
            when (flag) {
                LEFT_SIDE_FLAG -> {
                    _leftPath.postValue(state)
                }

                RIGHT_SIDE_FLAG -> {
                    _rightPath.postValue(state)
                }

                FRONT_SIDE_FLAG -> {
                    _frontPath.postValue(state)
                }

                BACK_SIDE_FLAG -> {
                    _backPath.postValue(state)
                }

                FRONT_SEAT_FLAG -> {
                    _frontSeatPath.postValue(state)
                }

                BACK_SEAT_FLAG -> {
                    _backSeatPath.postValue(state)
                }
            }
        }

    }

}
