package com.ishanvohra2.findr.datastore

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class DataStoreManager(
    private val context: Context
): DataStorePreferenceAPI {

    companion object{
        fun getInstance(context: Context) = DataStoreManager(context)

        private val Context.dataStore by preferencesDataStore(
            name = "findr_datastore"
        )
    }

    override suspend fun <T> getPreference(key: Preferences.Key<T>):
            Flow<T?> = context.dataStore.data.catch { exception ->
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map { preferences ->
        preferences[key]
    }

    override suspend fun <T> putPreference(key: Preferences.Key<T>, value: T) {
        context.dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    override suspend fun <T> removePreference(key: Preferences.Key<T>) {
        context.dataStore.edit {
            it.remove(key)
        }
    }

    override suspend fun clearAllPreference() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}