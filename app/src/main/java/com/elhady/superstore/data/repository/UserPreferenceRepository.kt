package com.elhady.superstore.data.repository

import kotlinx.coroutines.flow.Flow

interface UserPreferenceRepository {
    suspend fun updateUserId(userId: String)
    suspend fun getUserId(): Flow<String>
    suspend fun clearUserPreferences()
}