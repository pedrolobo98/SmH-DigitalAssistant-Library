package com.example.digitalassistantapp.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

object SharedPreference {
    private const val PREF_USER_ID = "userId"
    private const val PREF_REMEMBER_LOGIN = "rememberLogin"
    private const val PREF_MIC_AUTOMATIC = "chatAutoMic"
    private const val PREF_TALKBACK = "talkBack"

    private fun getSharedPreferences(ctx: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(ctx)
    }

    // SET PREFERENCES
    fun setUserId(ctx: Context, userId: String?) {
        val editor: SharedPreferences.Editor = getSharedPreferences(ctx).edit()
        editor.putString(PREF_USER_ID, userId)
        editor.apply()
    }

    fun setRememberLogin(ctx: Context, remember: String?) {
        val editor: SharedPreferences.Editor = getSharedPreferences(ctx).edit()
        editor.putString(PREF_REMEMBER_LOGIN, remember)
        editor.apply()
    }

    fun setAutoMic(ctx: Context, autoMic: Boolean) {
        val editor: SharedPreferences.Editor = getSharedPreferences(ctx).edit()
        editor.putBoolean(PREF_MIC_AUTOMATIC, autoMic)
        editor.apply()
    }

    fun setTalkBack(ctx: Context, tts: Boolean) {
        val editor: SharedPreferences.Editor = getSharedPreferences(ctx).edit()
        editor.putBoolean(PREF_TALKBACK, tts)
        editor.apply()
    }

    // GET PREFERENCES
    fun getUserId(ctx: Context): String? {
        return getSharedPreferences(ctx).getString(PREF_USER_ID, "")
    }

    fun getRememberLogin(ctx: Context): String? {
        return getSharedPreferences(ctx).getString(PREF_REMEMBER_LOGIN, "")
    }

    fun getAutoMic(ctx: Context): Boolean {
        return getSharedPreferences(ctx).getBoolean(PREF_MIC_AUTOMATIC, false)
    }

    fun getTalkBack(ctx: Context): Boolean {
        return getSharedPreferences(ctx).getBoolean(PREF_TALKBACK, false)
    }
}