package com.israa.atmodrivecaptain.auth.data.model

data class CheckCodeResponse(
    val `data`: CheckCodeData,
    val message: String?,
    val status: Boolean,
)