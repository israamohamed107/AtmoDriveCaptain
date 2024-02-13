package com.israa.atmodrivecaptain.utils

import android.content.Context
import android.content.SharedPreferences
import com.israa.atmodrivecaptain.auth.domain.model.CaptainDetails

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

    fun setCaptainInfo(captain: CaptainDetails) {

        getSharedPreferences().edit().apply {
            putString(PreferencesKeys.REMEMBER_TOKEN, captain.rememberToken)
            putString(PreferencesKeys.EMAIL, captain.email)
            putInt(PreferencesKeys.IS_DARK_MODE, captain.isDarkMode!!)
            putInt(PreferencesKeys.STATUS, captain.status!!)
            putInt(PreferencesKeys.REGISTER_STEP, captain.registerStep!!)
            putString(PreferencesKeys.MOBILE, captain.mobile)
            putString(PreferencesKeys.CAPTAIN_CODE, captain.captainCode)
            putString(PreferencesKeys.LANG, captain.lang)
            putString(PreferencesKeys.ID, captain.id.toString())
            putBoolean(PreferencesKeys.IS_ACTIVE,true)

        }.apply()
    }

    fun clear(){
        getSharedPreferences().edit().clear().apply()
    }

    fun putString(key: String, value: String) {
        getSharedPreferences().edit().putString(key, value).apply()
    }

    fun getString(key: String): String? {
        return getSharedPreferences().getString(key, "")
    }

    fun putBoolean(key: String, value: Boolean) {
        getSharedPreferences().edit().putBoolean(key, value).apply()
    }

    fun getBoolean(key: String): Boolean {
        return getSharedPreferences().getBoolean(key, false)
    }

    object PreferencesKeys {
        const val SHARED_PREFERENCES_NAME = "user"
        const val REMEMBER_TOKEN = "rememberToken"
        const val IS_NEW = "isNew"
        const val AVATAR = "avatar"
        const val EMAIL = "email"
        const val FULL_NAME = "fullName"
        const val IS_DARK_MODE = "isDarkMode"
        const val LANG = "lang"
        const val MOBILE = "mobile"
        const val CAPTAIN_CODE = "captainCode"
        const val RATE = "rate"
        const val SHAKE_PHONE = "shakePhone"
        const val STATUS = "status"
        const val REGISTER_STEP = "registerStep"
        const val IS_ACTIVE = "isActive"
        const val GENDER = "gender"
        const val NATIONALITY = "nationality"
        const val ID = "id"
        const val AVAILABILITY = "availability"

    }


}