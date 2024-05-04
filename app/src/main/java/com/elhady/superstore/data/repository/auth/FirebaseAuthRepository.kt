package com.elhady.superstore.data.repository.auth

import com.elhady.superstore.data.model.Resource
import com.elhady.superstore.data.model.UserDetailsModel
import kotlinx.coroutines.flow.Flow

interface FirebaseAuthRepository {
    suspend fun loginWithEmailAndPassword(email: String, password: String): Flow<Resource<UserDetailsModel>>

    suspend fun loginWithGoogle(idToken: String): Flow<Resource<UserDetailsModel>>

    suspend fun loginWithFacebook(token: String): Flow<Resource<UserDetailsModel>>

}