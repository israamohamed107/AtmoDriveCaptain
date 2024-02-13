package com.israa.atmodrivecaptain.auth.data.model

data class CheckCodeOptions(
    val brands: List<String>,
    val colors: List<String>,
    val device_types: List<String>,
    val gender: List<String>,
    val years: List<String>
)