package com.israa.atmodrivecaptain.auth.presentation.viewmodels


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.israa.atmodrive.auth.data.datasource.remote.AuthApiService
import com.israa.atmodrive.auth.data.datasource.remote.ResponseState
import com.israa.atmodrive.auth.domain.usecase.AuthUseCase
import com.israa.atmodrive.auth.domain.usecase.IAuthUseCase
import com.israa.atmodrivecaptain.auth.data.datasource.remote.ImageApiService
import com.israa.atmodrivecaptain.utils.IMAGE
import com.israa.atmodrivecaptain.utils.MySharedPreference
import com.israa.atmodrivecaptain.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Part
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class LoginViewModel @Inject constructor(private val useCase: IAuthUseCase):ViewModel() {

    private var _sendCodeData = Channel<UiState>()
    val sendCodeData = _sendCodeData.receiveAsFlow()

    private var _checkCodeData = Channel<UiState>()
    val checkCodeData = _checkCodeData.receiveAsFlow()

    private var _loginEvent = Channel<LoginEvents>()
    val loginEvent = _loginEvent.receiveAsFlow()

    fun sendCode(mobile: String) {
        viewModelScope.launch {
            _sendCodeData.send(UiState.Loading)
            when (val data = useCase.sendCode(mobile)) {
                is ResponseState.Success -> {
                    _sendCodeData.send(UiState.Success(data.data))
                }
                is ResponseState.Failure -> {
                    _sendCodeData.send(UiState.Failure(data.error))
                }

                else -> {}
            }
        }
    }


    fun checkCode(mobile:String, verificationCode:String,deviceToken:String){
        viewModelScope.launch {
            _checkCodeData.send(UiState.Loading)
            when (val data = useCase.checkCode(mobile,verificationCode, deviceToken)) {
                is ResponseState.Success -> {
                    _checkCodeData.send(UiState.Success(data.data))

                }
                is ResponseState.Failure -> {
                    _checkCodeData.send(UiState.Failure(data.error))
                }

                else -> {}
            }
        }
        }

    fun navigate(step:Int?){
      viewModelScope.launch {
          when(step){
              1 -> _loginEvent.send(LoginEvents.NavigateToVehicleInfo)
              2 -> _loginEvent.send(LoginEvents.NavigateToBankAccountInfo)
              3 ->{
                  if(MySharedPreference.getBoolean(MySharedPreference.PreferencesKeys.IS_ACTIVE))
                      _loginEvent.send(LoginEvents.NavigateToHome)
                  else
                  _loginEvent.send(LoginEvents.NotActive)
              }
              else ->{
                  _loginEvent.send(LoginEvents.NavigateToPersonalInfo)
              }
          }
      }
    }

    sealed class LoginEvents(){
        object NavigateToPersonalInfo : LoginEvents()
        object NavigateToVehicleInfo: LoginEvents()
        object NavigateToBankAccountInfo : LoginEvents()
        object NavigateToHome : LoginEvents()
        object NotActive : LoginEvents()

    }


        
    }


