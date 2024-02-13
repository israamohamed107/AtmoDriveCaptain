package com.israa.atmodrive.auth.data.datasource.remote

import com.israa.atmodrivecaptain.auth.data.model.CheckCodeResponse
import com.israa.atmodrivecaptain.auth.data.model.RegisterCaptainResponse
import com.israa.atmodrivecaptain.auth.data.model.SendCodeResponse
import com.israa.atmodrivecaptain.utils.CHECK_CODE
import com.israa.atmodrivecaptain.utils.REGISTER_BANK_ACCOUNT
import com.israa.atmodrivecaptain.utils.RESISTER_CAPTAIN
import com.israa.atmodrivecaptain.utils.RESISTER_VEHICLE
import com.israa.atmodrivecaptain.utils.SEND_CODE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthApiService {

    @POST(SEND_CODE)
    @FormUrlEncoded
    suspend fun sendCode(@Field("mobile") mobile: String): SendCodeResponse


    @POST(CHECK_CODE)
    @FormUrlEncoded
    suspend fun checkCode(
        @Field("mobile") mobile: String,
        @Field("verification_code") verificationCode: String,
        @Field("device_token") deviceToken: String
    ): CheckCodeResponse

    @POST(RESISTER_CAPTAIN)
    @FormUrlEncoded
    suspend fun registerCaptain(
        @Field("mobile") mobile: String,
        @Field("avatar") avatar: String?,
        @Field("device_token") deviceToken: String,
        @Field("device_id") deviceId: String,
        @Field("device_type") deviceType: String,
        @Field("national_id_front") nationalIdFront: String?,
        @Field("national_id_back") nationalIdBack: String?,
        @Field("driving_license_front") nationalLicenceFront: String?,
        @Field("driving_license_back") nationalLicenceBack: String?,
        @Field("is_dark_mode") isDarkMode : Int
    ): RegisterCaptainResponse

    @POST(RESISTER_VEHICLE)
    @FormUrlEncoded
    suspend fun registerVehicle(

        @Field("vehicle_front") vehicleFront:String?,
        @Field("vehicle_back") vehicleBack:String?,
        @Field("vehicle_left") vehicleLeft:String?,
        @Field("vehicle_right") vehicleRight:String?,
        @Field("vehicle_front_seat") vehicleFrontSeat:String?,
        @Field("vehicle_back_seat") vehicleBackSeat:String?,
        @Field("vehicle_license_front") vehicleLicenseFront:String?,
        @Field("vehicle_license_back") vehicleLicenseBack:String?,
        ):RegisterCaptainResponse

    @POST(REGISTER_BANK_ACCOUNT)
    @FormUrlEncoded
    suspend fun registerBankAccount(
        @Field("bank_name") bankName: String?,
        @Field("iban_number") ibanNumber: String?,
        @Field("account_name") accountName: String?,
        @Field("account_number") accountNumber: String?
    ): RegisterCaptainResponse



}