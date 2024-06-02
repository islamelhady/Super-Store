package com.elhady.superstore.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppPreferencesDataSource(private val context: Context) {

    suspend fun saveLoginState(isLoggedIn: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DataStoreKeys.IS_USER_LOGGED_IN] = isLoggedIn
        }
    }
    val isUserLoggedIn: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[DataStoreKeys.IS_USER_LOGGED_IN] ?: false
    }

}
