package com.israa.atmodrivecaptain.auth.data.model

data class RegisterCaptainData(
    val avatar: String?,
    val birthday: String?,
    val captain_code: String?,
    val email: String?,
    val full_name: String?,
    val gender: String?,
    val is_active: Int,
    val is_dark_mode: Int,
    val lang: String,
    val mobile: String,
    val nationality: String?,
    val options: RegisterCaptainOptions,
    val register_step: Int,
    val remember_token: String,
    val status: Int
)
