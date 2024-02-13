package com.israa.atmodrive.auth.data.datasource.mapper

import com.israa.atmodrivecaptain.auth.data.model.CheckCodeResponse
import com.israa.atmodrivecaptain.auth.data.model.RegisterCaptainResponse
import com.israa.atmodrivecaptain.auth.domain.model.CaptainDetails
import com.israa.atmodrivecaptain.auth.domain.model.RegisterCaptain


fun CheckCodeResponse.asDomain(

): CaptainDetails {

    return data.user?.let {

        CaptainDetails(
            it.avatar,
            it.captain_code,
            it.email,
            it.full_name,
            it.gender,
            it.id,
            it.is_active,
            it.is_dark_mode,
            it.lang,
            it.mobile,
            it.register_step,
            it.remember_token,
            it.status
        )
    }
        ?: CaptainDetails()
}

fun RegisterCaptainResponse.asDomain(): RegisterCaptain {
    return data?.let {
        RegisterCaptain(
            it.avatar,
            it.captain_code,
            it.email,
            it.full_name,
            it.gender,
            it.is_active,
            it.is_dark_mode,
            it.lang,
            it.mobile,
            it.nationality,
            it.register_step,
            it.remember_token,
            it.status
        )
    } ?: RegisterCaptain()
}