package com.israa.atmodrive.auth.data.datasource.remote

import com.israa.atmodrivecaptain.auth.data.model.CheckCodeResponse
import com.israa.atmodrivecaptain.auth.data.model.DeleteImageResponse
import com.israa.atmodrivecaptain.auth.data.model.DeleteModel
import com.israa.atmodrivecaptain.auth.data.model.RegisterCaptainResponse
import com.israa.atmodrivecaptain.auth.data.model.SendCodeResponse
import com.israa.atmodrivecaptain.auth.data.model.UploadImageResponse
import com.israa.atmodrivecaptain.auth.domain.model.CheckCode
import com.israa.atmodrivecaptain.auth.domain.model.RegisterCaptain
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.Part

interface IRemoteDataSource {
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
        isDarkMode : Int
    ): ResponseState<RegisterCaptain>

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



    suspend fun uploadImage(
        part: MultipartBody.Part,
        path: RequestBody
    ): ResponseState<UploadImageResponse>

    suspend fun deleteImage(path:DeleteModel):ResponseState<DeleteImageResponse>

    suspend fun registerBankAccount(
        bankName: String?,
        ibanNumber: String?,
        accountName: String?,
        accountNumber: String?
    ):ResponseState<RegisterCaptain>

}