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
import com.israa.atmodrivecaptain.utils.exhaustive
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class VehicleInfoViewModel @Inject constructor(private val authUseCase: AuthUseCase) : ViewModel() {

    companion object {
        const val FIRST_SIDE_FLAG = 1
        const val SECOND_SIDE_FLAG = 2
        const val TOP_SIDE_FLAG = 3
        const val BACK_SIDE_FLAG = 4
        const val FRONT_SIDE_FLAG = 5
        const val SIDE_FLAG = 6
        const val VEHICLE_LICENCE_FRONT_FLAG = 7
        const val VEHICLE_LICENCE_BACK_FLAG = 8
    }

    private var _frontPath: MutableLiveData<UiState?> = MutableLiveData(null)
    val frontPath: LiveData<UiState?> = _frontPath


    private var _backPath: MutableLiveData<UiState?> = MutableLiveData(null)
    val backPath: LiveData<UiState?> = _backPath


    private var _topPath: MutableLiveData<UiState?> = MutableLiveData(null)
    val topPath: LiveData<UiState?> = _topPath


    private var _rightPath: MutableLiveData<UiState?> = MutableLiveData(null)

    val rightPath: LiveData<UiState?> = _rightPath

    private var _leftPath: MutableLiveData<UiState?> = MutableLiveData(null)
    val leftPath: LiveData<UiState?> = _leftPath

    private var _sidePath: MutableLiveData<UiState?> = MutableLiveData(null)
    val sidePath: LiveData<UiState?> = _sidePath


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

    private var _topSideUri: MutableLiveData<Uri?> = MutableLiveData(null)
    val topSideUri: LiveData<Uri?> = _topSideUri

    private var _sideUri: MutableLiveData<Uri?> = MutableLiveData(null)
    val sideUri: LiveData<Uri?> = _sideUri


    private var _registerVehicle = Channel<UiState>()
    val registerVehicle = _registerVehicle.receiveAsFlow()


    private var _deleteImage : MutableLiveData<UiState?> = MutableLiveData()
    val deleteImage:LiveData<UiState?> = _deleteImage

    suspend fun uploadImage(part: MultipartBody.Part, path: RequestBody, flag: Int) {

            val response = authUseCase.uploadImage(part, path)
            when (response) {
                is ResponseState.Success -> {
                    when (flag) {
                        FIRST_SIDE_FLAG -> _leftPath.postValue(UiState.Success(response.data.data!!))
                        SECOND_SIDE_FLAG -> _rightPath.postValue(UiState.Success(response.data.data!!))
                        FRONT_SIDE_FLAG -> _frontPath.postValue(UiState.Success(response.data.data!!))
                        BACK_SIDE_FLAG -> _backPath.postValue(UiState.Success(response.data.data!!))
                        TOP_SIDE_FLAG -> _topPath.postValue(UiState.Success(response.data.data!!))
                        SIDE_FLAG -> _sidePath.postValue(UiState.Success(response.data.data!!))
                        VEHICLE_LICENCE_FRONT_FLAG -> _licenceFrontPath.postValue(UiState.Success(response.data.data!!))
                        VEHICLE_LICENCE_BACK_FLAG -> _licenceBackPath.postValue(UiState.Success(response.data.data!!))
                    }

                }

                is ResponseState.Failure -> {
                    when (flag) {
                        FIRST_SIDE_FLAG -> _leftPath.postValue(UiState.Failure(response.error))
                        SECOND_SIDE_FLAG -> _rightPath.postValue(UiState.Failure(response.error))
                        FRONT_SIDE_FLAG -> _frontPath.postValue(UiState.Failure(response.error))
                        BACK_SIDE_FLAG -> _backPath.postValue(UiState.Failure(response.error))
                        TOP_SIDE_FLAG -> _topPath.postValue(UiState.Failure(response.error))
                        SIDE_FLAG -> _sidePath.postValue(UiState.Failure(response.error))
                        VEHICLE_LICENCE_FRONT_FLAG -> _licenceFrontPath.postValue(UiState.Failure(response.error))
                        VEHICLE_LICENCE_BACK_FLAG -> _licenceBackPath.postValue(UiState.Failure(response.error))
                    }
                }

                else -> {}
            }
        }


    fun resetPath(flag: Int) {
        when (flag) {
            FIRST_SIDE_FLAG -> _leftPath.value = null
            SECOND_SIDE_FLAG -> _rightPath.value = null
            FRONT_SIDE_FLAG -> _frontPath.value = null
            BACK_SIDE_FLAG -> _backPath.value = null
            TOP_SIDE_FLAG -> _topPath.value = null
            SIDE_FLAG -> _sidePath.value = null
            VEHICLE_LICENCE_FRONT_FLAG -> _licenceFrontPath.value = null
            VEHICLE_LICENCE_BACK_FLAG -> _licenceBackPath.value = null
        }
    }

    fun setImageUriValue(uri: Uri, flag: Int) {
        when (flag) {
            FIRST_SIDE_FLAG -> _leftSideUri.value = uri
            SECOND_SIDE_FLAG -> _rightSideUri.value = uri
            FRONT_SIDE_FLAG -> _frontSideUri.value = uri
            BACK_SIDE_FLAG -> _backSideUri.value = uri
            TOP_SIDE_FLAG -> _topSideUri.value = uri
            SIDE_FLAG -> _sideUri.value = uri
        }

    }

    fun resetImageUriValue(flag: Int) {
        when (flag) {
            FIRST_SIDE_FLAG -> _leftSideUri.value = null
            SECOND_SIDE_FLAG -> _rightSideUri.value = null
            FRONT_SIDE_FLAG -> _frontSideUri.value = null
            BACK_SIDE_FLAG -> _backSideUri.value = null
            TOP_SIDE_FLAG -> _topSideUri.value = null
            SIDE_FLAG -> _sideUri.value = null
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

            when(response){
                is ResponseState.Success ->_registerVehicle.send(UiState.Success(response.data))
                is ResponseState.Failure ->_registerVehicle.send(UiState.Failure(response.error))
                else->{}

            }
        }
    }


    fun deleteImage(path: DeleteModel, flag: Int){

        viewModelScope.launch {
            when(val response = authUseCase.deleteImage(path)){
                is ResponseState.Success ->{
                    when(flag){
                        FIRST_SIDE_FLAG ->{
                            _leftPath.postValue(null)
                            _leftSideUri.postValue(null)
                        }
                        SECOND_SIDE_FLAG ->{
                            _rightPath.postValue(null)
                            _rightSideUri.postValue(null)
                        }
                        FRONT_SIDE_FLAG ->{
                            _frontPath.postValue(null)
                            _frontSideUri.postValue(null)
                        }
                        BACK_SIDE_FLAG ->{
                            _backPath.postValue(null)
                            _backSideUri.postValue(null)
                        }
                        TOP_SIDE_FLAG ->{
                            _topPath.postValue(null)
                            _topSideUri.postValue(null)
                        }
                        SIDE_FLAG ->{
                            _sidePath.postValue(null)
                            _sideUri.postValue(null)
                        }
                        VEHICLE_LICENCE_FRONT_FLAG ->{
                            _licenceFrontPath.postValue(null)
                        }
                        VEHICLE_LICENCE_BACK_FLAG ->{
                            _licenceBackPath.postValue(null)
                        }
                    }
                    _deleteImage.postValue(UiState.Success(response.data.message))

                }
                is ResponseState.Failure ->{
                    _deleteImage.postValue(UiState.Failure(response.error))

                }
                else ->{}
            }
        }

    }
}