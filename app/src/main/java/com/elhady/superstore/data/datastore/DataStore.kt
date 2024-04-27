package com.elhady.superstore.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.elhady.superstore.data.datastore.DataStoreKeys.SUPER_STORE_PREFERENCES

object DataStoreKeys {
    const val SUPER_STORE_PREFERENCES = "superstore_preferences"
    val IS_USER_LOGGED_IN = booleanPreferencesKey("is_user_logged_in")
}

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = SUPER_STORE_PREFERENCES)
