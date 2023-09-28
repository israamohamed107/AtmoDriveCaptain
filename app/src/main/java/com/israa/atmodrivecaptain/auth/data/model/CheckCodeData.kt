package com.israa.atmodrivecaptain.auth.data.model

data class CheckCodeData(
    val is_new: Boolean,
    val user: CheckCodeUser?
)