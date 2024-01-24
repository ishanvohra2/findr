package com.ishanvohra2.findr.datastore

import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow

interface DataStorePreferenceAPI {

    suspend fun<T> getPreference(key: Preferences.Key<T>): Flow<T?>
    suspend fun<T> putPreference(key: Preferences.Key<T>, value: T)
    suspend fun<T> removePreference(key: Preferences.Key<T>)
    suspend fun clearAllPreference()

}