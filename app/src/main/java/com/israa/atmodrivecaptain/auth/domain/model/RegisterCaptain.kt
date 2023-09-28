package com.israa.atmodrivecaptain.auth.domain.model

data class RegisterCaptain(
    val avatar: String? = null ,
    val captainCode: String? = null ,
    val email: String? = null ,
    val fullName: String? = null ,
    val gender: String? = null ,
    val isActive: Int = 0 ,
    val isDarkMode: Int = 0 ,
    val lang: String? = null ,
    val mobile: String? = null ,
    val nationality: String? = null ,
    val registerStep: Int = 0 ,
    val rememberToken: String? = null ,
    val status: Int = 0 ,
)
