package com.israa.atmodrivecaptain.auth.domain.model

import com.israa.atmodrivecaptain.auth.data.model.CheckCodeOptions


data class CheckCode(
    val isNew : Boolean,
    val avatar: String? = null,
    val email: String?= null,
    val fullName: String?= null,
    val isDarkMode: Int?= null,
    val lang: String?= null,
    val mobile: String?= null,
    val captainCode: String?= null,
    val rate: Int?= null,
    val rememberToken: String?= null,
    val shakePhone: Int?= null,
    val status: Int?= null,
    val register_step: Int? = null,

    )
