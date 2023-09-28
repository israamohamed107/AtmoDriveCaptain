package com.israa.atmodrive.auth.data.datasource.mapper

import com.israa.atmodrivecaptain.auth.data.model.CheckCodeResponse
import com.israa.atmodrivecaptain.auth.data.model.RegisterCaptainResponse
import com.israa.atmodrivecaptain.auth.domain.model.CheckCode
import com.israa.atmodrivecaptain.auth.domain.model.RegisterCaptain


fun CheckCodeResponse.asDomain(

): CheckCode {

    return data.user?.let {

    CheckCode(
        data.is_new,
        it.avatar,
        it.email,
        it.full_name,
        it.is_dark_mode,
        it.lang,
        it.mobile,
        it.captain_code,
        it.rate,
        it.remember_token,
        it.shake_phone,
        it.status,
        it.register_step
        )
    }
        ?: CheckCode(isNew = data.is_new)
}

fun RegisterCaptainResponse.asDomain():RegisterCaptain{
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