package com.elhady.superstore.data.repository

import android.content.Context
import kotlinx.coroutines.flow.Flow

class UserPreferenceRepositoryImpl(private val context: Context) : UserPreferenceRepository {
    override suspend fun updateUserId(userId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getUserId(): Flow<String> {
        TODO("Not yet implemented")
    }

    override suspend fun clearUserPreferences() {
        TODO("Not yet implemented")
    }

}