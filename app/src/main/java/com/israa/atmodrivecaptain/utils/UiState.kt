package com.israa.atmodrivecaptain.utils

sealed class UiState{
    data class Success(val data:Any):UiState()
    data class Failure(val message:String):UiState()
    object Loading:UiState()

}