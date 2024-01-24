package com.ishanvohra2.findr.datastore

import androidx.datastore.preferences.core.stringPreferencesKey

object DataStoreConstants {

    val USER_PROFILE = stringPreferencesKey("user_profile")
    val AUTH_TOKEN = stringPreferencesKey("token")

}