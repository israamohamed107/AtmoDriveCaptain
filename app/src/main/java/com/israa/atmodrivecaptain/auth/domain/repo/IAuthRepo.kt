package com.israa.atmodrivecaptain.auth.domain.repo

import com.israa.atmodrive.auth.data.datasource.remote.ResponseState
import com.israa.atmodrivecaptain.auth.data.model.DeleteImageResponse
import com.israa.atmodrivecaptain.auth.data.model.DeleteModel
import com.israa.atmodrivecaptain.auth.data.model.SendCodeResponse
import com.israa.atmodrivecaptain.auth.data.model.UploadImageResponse
import com.israa.atmodrivecaptain.auth.domain.model.CheckCode
import com.israa.atmodrivecaptain.auth.domain.model.RegisterCaptain
import okhttp3.MultipartBody
import okhttp3.RequestBody


interface IAuthRepo {

    suspend fun sendCode(mobile: String): ResponseState<SendCodeResponse>
    suspend fun checkCode(
        mobile: String,
        verificationCode: String,
        deviceToken: String
    ): ResponseState<CheckCode>

    suspend fun registerCaptain(
        mobile: String,
        avatar: String?,
        deviceToken: String,
        deviceId: String,
        deviceType: String,
        nationalIdFront: String?,
        nationalIdBack: String?,
        nationalLicenceFront: String?,
        nationalLicenceBack: String?,
        isDarkMode: Int
    ): ResponseState<RegisterCaptain>


    suspend fun uploadImage(
        part: MultipartBody.Part,
        path: RequestBody
    ): ResponseState<UploadImageResponse>

    suspend fun deleteImage(path: DeleteModel): ResponseState<DeleteImageResponse>

    suspend fun registerVehicle(
        vehicleFront:String?,
        vehicleBack:String?,
        vehicleLeft:String?,
        vehicleRight:String?,
        vehicleFrontSeat:String?,
        vehicleBackSeat:String?,
        vehicleLicenseFront:String?,
        vehicleLicenseBack:String?
    ): ResponseState<RegisterCaptain>

     suspend fun registerBankAccount(
        bankName: String?,
        ibanNumber: String?,
        accountName: String?,
        accountNumber: String?
    ): ResponseState<RegisterCaptain>
}