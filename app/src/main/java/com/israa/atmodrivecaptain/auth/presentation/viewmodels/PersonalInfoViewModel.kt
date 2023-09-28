package com.israa.atmodrivecaptain.auth.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.israa.atmodrive.auth.data.datasource.remote.ResponseState
import com.israa.atmodrive.auth.domain.usecase.AuthUseCase
import com.israa.atmodrivecaptain.auth.data.model.DeleteModel
import com.israa.atmodrivecaptain.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class PersonalInfoViewModel @Inject constructor(private val authUseCase: AuthUseCase) :ViewModel(){

    companion object{
        const val PERSONAL_IMAGE_FLAG = 1
        const val NATIONAL_ID_FRONT_FLAG = 2
        const val NATIONAL_ID_BACK_FLAG = 3
        const val LICENCE_FRONT_FLAG = 4
        const val LICENCE_BACK_FLAG = 5
    }

    private var _personalImagePath : MutableLiveData<UiState?> = MutableLiveData()
    val personalImagePath:LiveData<UiState?> = _personalImagePath

    private var _nationalIdFrontPath : MutableLiveData<UiState?> = MutableLiveData()
    val nationalIdFrontPath:LiveData<UiState?> = _nationalIdFrontPath

    private var _nationalIdBackPath : MutableLiveData<UiState?> = MutableLiveData()
    val nationalIdBackPath:LiveData<UiState?> = _nationalIdBackPath

    private var _licenceFrontPath : MutableLiveData<UiState?> = MutableLiveData()
    val licenceFrontPath:LiveData<UiState?> = _licenceFrontPath

    private var _licenceBackPath : MutableLiveData<UiState?> = MutableLiveData()
    val licenceBackPath:LiveData<UiState?> = _licenceBackPath

    private var _registerCaptain = Channel<UiState>()
    val registerCaptain = _registerCaptain.receiveAsFlow()

    private var _deleteImage : MutableLiveData<UiState?> = MutableLiveData()
    val deleteImage:LiveData<UiState?> = _deleteImage


     fun uploadImage(part: MultipartBody.Part, path: RequestBody , flag:Int){

        viewModelScope.launch {
            when(val response = authUseCase.uploadImage(part,path)){
                is ResponseState.Success ->{
                    when(flag){
                        PERSONAL_IMAGE_FLAG -> _personalImagePath.postValue(UiState.Success(response.data.data!!))
                        NATIONAL_ID_FRONT_FLAG -> _nationalIdFrontPath.postValue(UiState.Success(response.data.data!!))
                        NATIONAL_ID_BACK_FLAG -> _nationalIdBackPath.postValue(UiState.Success(response.data.data!!))
                        LICENCE_FRONT_FLAG -> _licenceFrontPath.postValue(UiState.Success(response.data.data!!))
                        LICENCE_BACK_FLAG -> _licenceBackPath.postValue(UiState.Success(response.data.data!!))
                    }
                }
                is ResponseState.Failure ->{
//                when(flag){
//                    PERSONAL_IMAGE_FLAG -> _personalImagePath.postValue(UiState.Failure(response.error))
//                    NATIONAL_ID_FRONT_FLAG -> _nationalIdFrontPath.postValue(UiState.Failure(response.error))
//                    NATIONAL_ID_BACK_FLAG -> _nationalIdBackPath.postValue(UiState.Failure(response.error))
//                    LICENCE_FRONT_FLAG -> _licenceFrontPath.postValue(UiState.Failure(response.error))
//                    LICENCE_BACK_FLAG -> _licenceBackPath.postValue(UiState.Failure(response.error))
//                }
                }
                else -> {}
            }
        }
    }

    fun registerCaptain(
        mobile: String,
        avatar: String?,
        deviceToken: String,
        deviceId: String,
        deviceType: String,
        nationalIdFront: String?,
        nationalIdBack: String?,
        nationalLicenceFront: String?,
        nationalLicenceBack: String?,
        isDarkMode: Int
    ){
      viewModelScope.launch {
          _registerCaptain.send(UiState.Loading)
          when(val response = authUseCase.registerCaptain(mobile, avatar, deviceToken, deviceId, deviceType, nationalIdFront, nationalIdBack, nationalLicenceFront, nationalLicenceBack, isDarkMode)){
              is ResponseState.Success -> _registerCaptain.send(UiState.Success(response.data))
              is ResponseState.Failure -> _registerCaptain.send(UiState.Failure(response.error))
              else -> {}
          }
      }
    }

    fun deleteImage(path: DeleteModel, flag: Int){

        viewModelScope.launch {
            when(val response = authUseCase.deleteImage(path)){
                is ResponseState.Success ->{
                    when(flag){
                        PERSONAL_IMAGE_FLAG -> _personalImagePath.postValue(null)
                        NATIONAL_ID_FRONT_FLAG -> _nationalIdFrontPath.postValue(null)
                        NATIONAL_ID_BACK_FLAG -> _nationalIdBackPath.postValue(null)
                        LICENCE_FRONT_FLAG -> _personalImagePath.postValue(null)
                        LICENCE_BACK_FLAG -> _licenceBackPath.postValue(null)

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