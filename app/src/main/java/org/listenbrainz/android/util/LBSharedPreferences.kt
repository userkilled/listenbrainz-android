package org.listenbrainz.android.util

import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import org.listenbrainz.android.application.App
import org.listenbrainz.android.model.AccessToken
import org.listenbrainz.android.model.Playable
import org.listenbrainz.android.model.UserInfo

object LBSharedPreferences {
    const val USERNAME = "username"
    const val CURRENT_PLAYABLE = "CURRENT_PLAYABLE"
    const val MB_ACCESS_TOKEN = "access_token"
    const val REFRESH_TOKEN = "refresh_token"
    const val LB_ACCESS_TOKEN = "listenbrainz_user_token"
    const val STATUS_LOGGED_IN = 1
    const val STATUS_LOGGED_OUT = 0
    val preferences: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(App.context!!)

    fun setString(
        key: String?,
        value: String?
    ) {
        val editor = preferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun setInteger(key: String?, value: Int) {
        val editor = preferences.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun setLong(key: String?, value: Long) {
        val editor = preferences.edit()
        editor.putLong(key, value)
        editor.apply()
    }

    fun setBoolean(key: String?, value: Boolean) {
        val editor = preferences.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun saveOAuthToken(token: AccessToken) {
        val editor = preferences.edit()
        editor.putString(MB_ACCESS_TOKEN, token.accessToken)
        editor.putString(REFRESH_TOKEN, token.refreshToken)
        editor.apply()
    }

    fun saveUserInfo(userInfo: UserInfo) {
        val editor = preferences.edit()
        editor.putString(USERNAME, userInfo.username)
        editor.apply()
    }

    fun logoutUser() {
        val editor = preferences.edit()
        editor.remove(MB_ACCESS_TOKEN)
        editor.remove(REFRESH_TOKEN)
        editor.remove(USERNAME)
        editor.apply()
    }

    var currentPlayable : Playable?
        get() = preferences.getString(CURRENT_PLAYABLE, "")?.let {
            if (it.isBlank()) null else
                TypeConverter.playableFromJSON(it)
        }
        set(value) {
            value?.let {
                setString(CURRENT_PLAYABLE, TypeConverter.playableToJSON(it))
            }
        }

    val loginStatus: Int
        get() {
            val accessToken = accessToken
            val username = username
            return if (accessToken!!.isNotEmpty() && username!!.isNotEmpty()) STATUS_LOGGED_IN else STATUS_LOGGED_OUT
        }
    val accessToken: String?
        get() = preferences.getString(MB_ACCESS_TOKEN, "")
    val username: String?
        get() = preferences.getString(USERNAME, "")
    val refreshToken: String?
        get() = preferences.getString(REFRESH_TOKEN, "")
}