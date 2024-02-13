package com.israa.atmodrive.auth.data.repo


import com.israa.atmodrive.auth.data.datasource.remote.IRemoteDataSource
import com.israa.atmodrive.auth.data.datasource.remote.ResponseState
import com.israa.atmodrivecaptain.auth.data.model.DeleteImageResponse
import com.israa.atmodrivecaptain.auth.data.model.DeleteModel
import com.israa.atmodrivecaptain.auth.data.model.SendCodeResponse
import com.israa.atmodrivecaptain.auth.data.model.UploadImageResponse
import com.israa.atmodrivecaptain.auth.domain.model.CaptainDetails
import com.israa.atmodrivecaptain.auth.domain.model.RegisterCaptain
import com.israa.atmodrivecaptain.auth.domain.repo.IAuthRepo
import com.israa.atmodrivecaptain.utils.MySharedPreference
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class AuthRepo @Inject constructor(
    private val remoteDataSource: IRemoteDataSource
) : IAuthRepo {
    override suspend fun sendCode(mobile: String): ResponseState<SendCodeResponse> =
        remoteDataSource.sendCode(mobile)

    override suspend fun checkCode(
        mobile: String, verificationCode: String, deviceToken: String
    ): ResponseState<CaptainDetails> {
        val response = remoteDataSource.checkCode(mobile, verificationCode, deviceToken)
        when (response) {

            is ResponseState.Success -> {


                if (response.data.isActive == 1)
                    MySharedPreference.setCaptainInfo(response.data)
                else {
                    MySharedPreference.putBoolean(MySharedPreference.PreferencesKeys.IS_ACTIVE, false)
                    response.data.rememberToken?.let{MySharedPreference.putString(MySharedPreference.PreferencesKeys.REMEMBER_TOKEN,it)}
                }


            }

            is ResponseState.Failure -> {}

            else -> {}
        }
        return response
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
    ): ResponseState<RegisterCaptain>{
        val response = remoteDataSource.registerCaptain(mobile, avatar, deviceToken, deviceId, deviceType,
            nationalIdFront, nationalIdBack, nationalLicenceFront, nationalLicenceBack, isDarkMode)
        when(response){
            is ResponseState.Success ->{
                MySharedPreference.putString(MySharedPreference.PreferencesKeys.REMEMBER_TOKEN,response.data.rememberToken!!)
            }
            is ResponseState.Failure -> {}
            else -> {}
        }
        return response
    }

    override suspend fun uploadImage(
        part: MultipartBody.Part,
        path: RequestBody
    ): ResponseState<UploadImageResponse> = remoteDataSource.uploadImage(part, path)

    override suspend fun deleteImage(path: DeleteModel): ResponseState<DeleteImageResponse> =
        remoteDataSource.deleteImage(path)

    override suspend fun registerVehicle(
        vehicleFront: String?,
        vehicleBack: String?,
        vehicleLeft: String?,
        vehicleRight: String?,
        vehicleFrontSeat: String?,
        vehicleBackSeat: String?,
        vehicleLicenseFront: String?,
        vehicleLicenseBack: String?

    ): ResponseState<RegisterCaptain> = remoteDataSource.registerVehicle(
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
    ): ResponseState<RegisterCaptain> =
        remoteDataSource.registerBankAccount(bankName, ibanNumber, accountName, accountNumber)
}




