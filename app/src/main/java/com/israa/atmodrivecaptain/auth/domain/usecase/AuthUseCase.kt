package com.israa.atmodrive.auth.domain.usecase

import com.israa.atmodrive.auth.data.datasource.remote.ResponseState
import com.israa.atmodrivecaptain.auth.data.model.DeleteImageResponse
import com.israa.atmodrivecaptain.auth.data.model.DeleteModel
import com.israa.atmodrivecaptain.auth.data.model.SendCodeResponse
import com.israa.atmodrivecaptain.auth.data.model.UploadImageResponse

import com.israa.atmodrivecaptain.auth.domain.model.CheckCode
import com.israa.atmodrivecaptain.auth.domain.model.RegisterCaptain
import com.israa.atmodrivecaptain.auth.domain.repo.IAuthRepo
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class AuthUseCase @Inject constructor(val iRepo: IAuthRepo) : IAuthUseCase {
    override suspend fun sendCode(mobile: String): ResponseState<SendCodeResponse> =
        iRepo.sendCode(mobile)


    override suspend fun checkCode(
        mobile: String,
        verificationCode: String,
        deviceToken: String
    ): ResponseState<CheckCode> =
        iRepo.checkCode(mobile, verificationCode, deviceToken)


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
    ): ResponseState<RegisterCaptain> =
        iRepo.registerCaptain(
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

    override suspend fun uploadImage(
        part: MultipartBody.Part,
        path: RequestBody
    ): ResponseState<UploadImageResponse> = iRepo.uploadImage(part, path)

    override suspend fun deleteImage(path: DeleteModel): ResponseState<DeleteImageResponse> =
        iRepo.deleteImage(path)

    override suspend fun registerVehicle(
        vehicleFront: String?,
        vehicleBack: String?,
        vehicleLeft: String?,
        vehicleRight: String?,
        vehicleFrontSeat: String?,
        vehicleBackSeat: String?,
        vehicleLicenseFront: String?,
        vehicleLicenseBack: String?
    ): ResponseState<RegisterCaptain> =
        iRepo.registerVehicle(
            vehicleFront,
            vehicleBack,
            vehicleLeft,
            vehicleRight,
            vehicleFrontSeat,
            vehicleBackSeat,
            vehicleLicenseFront,
            vehicleLicenseBack
        )

    override suspend fun registerBankAccount(
        bankName: String?,
        ibanNumber: String?,
        accountName: String?,
        accountNumber: String?
    ): ResponseState<RegisterCaptain> = iRepo.registerBankAccount(bankName, ibanNumber, accountName, accountNumber)

}