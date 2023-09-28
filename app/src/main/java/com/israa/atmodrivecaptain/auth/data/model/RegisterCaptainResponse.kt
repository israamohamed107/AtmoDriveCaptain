package com.israa.atmodrivecaptain.auth.data.model

data class RegisterCaptainResponse(
    val `data`: RegisterCaptainData?,
    val message: String,
    val status: Boolean
)
