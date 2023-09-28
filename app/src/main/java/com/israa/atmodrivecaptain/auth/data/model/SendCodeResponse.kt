package com.israa.atmodrivecaptain.auth.data.model

data class SendCodeResponse(
    val message: String,
    val status: Boolean,
    val data:SendCodeData
)