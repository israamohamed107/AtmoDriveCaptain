package com.israa.atmodrivecaptain.auth.data.model

data class CheckCodeUser(
    val avatar: String,
    val birthday: String,
    val captain_code: String,
    val email: String,
    val full_name: String,
    val gender: String,
    val id: Int,
    val is_active: Int,
    val is_dark_mode: Int,
    val lang: String,
    val mobile: String,
    val nationality: Any,
    val options: CheckCodeOptions,
    val register_step: Int,
    val remember_token: String,
    val status: Int
)