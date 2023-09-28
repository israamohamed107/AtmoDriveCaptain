package com.israa.atmodrivecaptain.utils

import android.content.Context
import android.content.SharedPreferences
import com.israa.atmodrivecaptain.auth.domain.model.CheckCode
import com.israa.atmodrivecaptain.auth.domain.model.RegisterCaptain

object MySharedPreference {
    private var mAppContext: Context? = null
    fun init(appContext: Context?) {
        mAppContext = appContext
    }

    private fun getSharedPreferences(): SharedPreferences {
        return mAppContext!!.getSharedPreferences(
            PreferencesKeys.SHARED_PREFERENCES_NAME,
            Context.MODE_PRIVATE
        )
    }

    fun setCaptainInfo(captain : RegisterCaptain){

        getSharedPreferences().edit().apply {
            putString(PreferencesKeys.REMEMBER_TOKEN,captain.rememberToken)
            putString(PreferencesKeys.EMAIL,captain.email)
            putInt(PreferencesKeys.IS_DARK_MODE,captain.isDarkMode)
            putInt(PreferencesKeys.STATUS,captain.status)
            putInt(PreferencesKeys.REGISTER_STEP,captain.registerStep)
            putString(PreferencesKeys.MOBILE,captain.mobile)
            putString(PreferencesKeys.CAPTAIN_CODE,captain.captainCode)
            putString(PreferencesKeys.LANG,captain.lang)
            null

        }.apply()
    }

    fun getCaptain():RegisterCaptain{
        getSharedPreferences().apply {
            return RegisterCaptain(
                getString(PreferencesKeys.AVATAR,null),
                getString(PreferencesKeys.CAPTAIN_CODE,null),
                null,
                getString(PreferencesKeys.FULL_NAME,null),
                null,
                getInt(PreferencesKeys.IS_ACTIVE,0),
                getInt(PreferencesKeys.IS_DARK_MODE,0),
                getString(PreferencesKeys.LANG,null),
                getString(PreferencesKeys.MOBILE,null),
                getString(PreferencesKeys.NATIONALITY,null),
                getInt(PreferencesKeys.REGISTER_STEP,0),
                getString(PreferencesKeys.REMEMBER_TOKEN,null),
                getInt(PreferencesKeys.STATUS,0)
            )
        }

    }

    fun setIsNew(isNew:Boolean){
        getSharedPreferences().edit().putBoolean(PreferencesKeys.IS_NEW,isNew).apply()
    }
    fun getIsNew():Boolean{
        return getSharedPreferences().getBoolean(PreferencesKeys.IS_NEW, true)
    }
    fun setVehicleInfo(captain : CheckCode){

    }
    fun setUserToken(token:String){
        getSharedPreferences().edit().putString(PreferencesKeys.REMEMBER_TOKEN,token).apply()
    }
    fun getUserToken(): String? {
        return getSharedPreferences().getString(PreferencesKeys.REMEMBER_TOKEN, null)
    }
    private object PreferencesKeys {
        val SHARED_PREFERENCES_NAME = "user"
        val REMEMBER_TOKEN = "rememberToken"
        val IS_NEW = "isNew"
        val AVATAR = "avatar"
        val EMAIL = "email"
        val FULL_NAME = "fullName"
        val IS_DARK_MODE = "isDarkMode"
        val LANG = "lang"
        val MOBILE = "mobile"
        val CAPTAIN_CODE = "captainCode"
        val RATE = "rate"
        val SHAKE_PHONE = "shakePhone"
        val STATUS = "status"
        val REGISTER_STEP = "registerStep"
        val IS_ACTIVE = "isActive"
        val GENDER = "gender"
        val NATIONALITY = "nationality"

    }


}