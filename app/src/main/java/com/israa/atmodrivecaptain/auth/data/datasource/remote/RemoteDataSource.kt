package com.israa.atmodrive.auth.data.datasource.remote

import com.israa.atmodrive.auth.data.datasource.mapper.asDomain
import com.israa.atmodrivecaptain.auth.data.datasource.remote.ImageApiService
import com.israa.atmodrivecaptain.auth.data.model.DeleteImageResponse
import com.israa.atmodrivecaptain.auth.data.model.DeleteModel
import com.israa.atmodrivecaptain.auth.data.model.SendCodeResponse
import com.israa.atmodrivecaptain.auth.data.model.UploadImageResponse
import com.israa.atmodrivecaptain.auth.domain.model.CaptainDetails
import com.israa.atmodrivecaptain.auth.domain.model.RegisterCaptain
import com.israa.atmodrivecaptain.utils.explain
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject
import kotlin.Exception

class RemoteDataSource @Inject constructor(
    val authApiService: AuthApiService,
    val imageApiService: ImageApiService
) : IRemoteDataSource {
    override suspend fun sendCode(mobile: String): ResponseState<SendCodeResponse> {
        return try {
            val response = ResponseState.Success(authApiService.sendCode(mobile))
            if (response.data.status)
                response
            else
                ResponseState.Failure(response.data.message)

        } catch (e: Exception) {
            e.explain()
        }
    }


    override suspend fun checkCode(
        mobile: String,
        verificationCode: String,
        deviceToken: String
    ): ResponseState<CaptainDetails> {
        return try {
            val response = ResponseState.Success(
                authApiService.checkCode(
                    mobile,
                    verificationCode,
                    deviceToken
                )
            )
            if (response.data.status)
                ResponseState.Success(response.data.asDomain())
            else
                ResponseState.Failure(response.data.message)
        } catch (e: Exception) {
            e.explain()

        }
    }

    override suspend fun registerCaptain(
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
    ): ResponseState<RegisterCaptain> {
        return try {
            val response = authApiService.registerCaptain(
                mobile,
                avatar,
                deviceToken,
                deviceId,
                deviceType,
                nationalIdFront,
                nationalIdBack,
                nationalLicenceFront,
                nationalLicenceBack,
                isDarkMode
            )
            if (response.status)
                ResponseState.Success(response.asDomain())
            else
                ResponseState.Failure(response.message)
        } catch (e: Exception) {
            e.explain()
        }
    }

    override suspend fun registerVehicle(
        vehicleFront: String?,
        vehicleBack: String?,
        vehicleLeft: String?,
        vehicleRight: String?,
        vehicleFrontSeat: String?,
        vehicleBackSeat: String?,
        vehicleLicenseFront: String?,
        vehicleLicenseBack: String?
    ): ResponseState<RegisterCaptain> {
       return try {
           val response = authApiService.registerVehicle(
               vehicleFront,
               vehicleBack,
               vehicleLeft,
               vehicleRight,
               vehicleFrontSeat,
               vehicleBackSeat,
               vehicleLicenseFront,
               vehicleLicenseBack
           )

           if(response.status)
               ResponseState.Success(response.asDomain())
           else
               ResponseState.Failure(response.message)
       }catch (e : Exception){
           e.explain()
       }
    }

    override suspend fun uploadImage(
        part: MultipartBody.Part,
        path: RequestBody
    ): ResponseState<UploadImageResponse> {
        return try {
            val response = imageApiService.uploadImage(part, path)
            if (response.status)
                ResponseState.Success(response)
            else
                ResponseState.Failure(response.message)

        } catch (e: Exception) {

            e.explain()
        }
    }

    override suspend fun deleteImage(path: DeleteModel): ResponseState<DeleteImageResponse> {

        return try {
            val response = imageApiService.deleteImage(path)
            if (response.status)
                ResponseState.Success(response)
            else
                ResponseState.Failure(response.message)
        } catch (e: Exception) {
            e.explain()
        }
    }

    override suspend fun registerBankAccount(
        bankName: String?,
        ibanNumber: String?,
        accountName: String?,
        accountNumber: String?
    ): ResponseState<RegisterCaptain> {

        return try {
            val response = authApiService.registerBankAccount(bankName, ibanNumber, accountName, accountNumber)
            if (response.status)
                ResponseState.Success(response.asDomain())
            else
                ResponseState.Failure(response.message)
        } catch (e: Exception) {
            e.explain()
        }
    }

}