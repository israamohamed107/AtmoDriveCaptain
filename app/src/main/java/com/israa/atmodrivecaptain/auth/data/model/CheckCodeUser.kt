package com.israa.atmodrivecaptain.auth.data.model

data class CheckCodeUser(
    val avatar: String?,
    val email: String?,
    val full_name: String?,
    val is_dark_mode: Int?,
    val lang: String?,
    val mobile: String?,
    val options: CheckCodeOptions?,
    val captain_code: String?,
    val rate: Int?,
    val remember_token: String?,
    val shake_phone: Int?,
    val status: Int?,
    val `suspend`: Int?,
    val register_step: Int?,
    val nationality: String?,
)