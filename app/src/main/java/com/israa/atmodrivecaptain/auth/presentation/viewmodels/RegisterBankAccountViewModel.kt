package com.israa.atmodrivecaptain.auth.presentation.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.israa.atmodrive.auth.data.datasource.remote.ResponseState
import com.israa.atmodrive.auth.domain.usecase.AuthUseCase
import com.israa.atmodrivecaptain.utils.UiState
import com.israa.atmodrivecaptain.utils.exhaustive
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterBankAccountViewModel @Inject constructor(val authUseCase: AuthUseCase) :ViewModel() {

    private var _registerAccount = MutableLiveData<UiState>(null)
     val registerAccount = _registerAccount

      fun registerBankAccount(
        bankName: String?,
        ibanNumber: String?,
        accountName: String?,
        accountNumber: String?
    ){
          _registerAccount.value = UiState.Loading
          viewModelScope.launch {
              val result = authUseCase.registerBankAccount(bankName, ibanNumber, accountName, accountNumber)
              when(result){
                  is ResponseState.Success -> _registerAccount.postValue(UiState.Success(result.data))
                  is ResponseState.Failure -> _registerAccount.postValue(UiState.Success(result.error))
                  else ->{}
              }

          }
      }
}