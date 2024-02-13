package com.israa.atmodrivecaptain.home.data.models


data class UpdateAvailabilityResponse(
    val message: String,
    val status: Boolean,
    val available:Boolean?
)