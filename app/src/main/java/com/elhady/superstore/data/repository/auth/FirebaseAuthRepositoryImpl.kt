package com.elhady.superstore.data.repository.auth

import com.elhady.superstore.data.model.AuthProvider
import com.elhady.superstore.data.model.Resource
import com.elhady.superstore.data.model.UserDetailsModel
import com.elhady.superstore.utils.CrashlyticsUtils
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.security.auth.login.LoginException

class FirebaseAuthRepositoryImpl(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : FirebaseAuthRepository {

    // Example usage for email and password login
    override suspend fun loginWithEmailAndPassword(email: String, password: String) =
        login(AuthProvider.EMAIL) { auth.signInWithEmailAndPassword(email, password).await() }

    override suspend fun loginWithGoogle(idToken: String): Flow<Resource<UserDetailsModel>> {
        return login(AuthProvider.GOOGLE) {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential).await()
        }
    }

    override suspend fun loginWithFacebook(token: String): Flow<Resource<UserDetailsModel>> {
        return login(AuthProvider.FACEBOOK) {
            val credential = FacebookAuthProvider.getCredential(token)
            auth.signInWithCredential(credential).await()
        }
    }

    private suspend fun login(
        provider: AuthProvider,
        signInRequest: suspend () -> AuthResult,
    ): Flow<Resource<UserDetailsModel>> = flow {
        try {
            emit(Resource.Loading())
            // perform firebase auth sign in request
            val authResult = signInRequest()
            val userId = authResult.user?.uid

            if (userId == null) {
                logAuthIssueToCrashlytics(msg = "Sign in UserID not found", provider.name)
                emit(Resource.Error(Exception("Sign in UserID not found")))
                return@flow
            }

            if (authResult.user?.isEmailVerified == false) {
                authResult.user?.sendEmailVerification()?.await()
                val msg = "Email not verified, verification email sent to user"
                logAuthIssueToCrashlytics(msg, provider.name)
                emit(Resource.Error(Exception(msg)))
                return@flow
            }

            // get user details from firestore
            val userDoc = firestore.collection("users").document(userId).get().await()
            if (!userDoc.exists()) {
                val msg = "Logged in user not found in firestore"
                logAuthIssueToCrashlytics(msg, provider.name)
                emit(Resource.Error(Exception(msg)))
                return@flow
            }

            // map user details to UserDetailsModel
            val userDetails = userDoc.toObject(UserDetailsModel::class.java)
            userDetails?.let {
                emit(Resource.Success(userDetails))
            } ?: run {
                val msg = "Error mapping user details to UserDetailsModel, user id = $userId"
                logAuthIssueToCrashlytics(msg, provider.name)
                emit(Resource.Error(Exception(msg)))
            }
        } catch (e: Exception) {
            logAuthIssueToCrashlytics(
                e.message ?: "Unknown error from exception = ${e::class.java}", provider.name
            )
            emit(Resource.Error(e)) // Emit error
        }
    }

    override suspend fun registerWithEmailAndPassword(
        name: String,
        email: String,
        password: String
    ): Flow<Resource<UserDetailsModel>> {
        return flow {
            try {
                emit(Resource.Loading())
                // firebase auth sign up request
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                val userId = authResult.user?.uid

                if (userId == null) {
                    logAuthIssueToCrashlytics(
                        msg = "Sign up UserID not found",
                        provider = AuthProvider.EMAIL.name
                    )
                    emit(Resource.Error(Exception("Sign up UserID not found")))
                    return@flow
                }

                // create user in firestore
                val user = UserDetailsModel(
                    name = name,
                    email = email,
                    id = userId,
                    createdAt = System.currentTimeMillis()
                )
                firestore.collection("users").document(userId).set(user).await()

                // send verification email
                authResult.user?.sendEmailVerification()?.await()
                emit(Resource.Success(user))
            } catch (e: Exception) {
                logAuthIssueToCrashlytics(
                    e.message ?: "Unknown error from exception = ${e::class.java}",
                    AuthProvider.EMAIL.name
                )
                emit(Resource.Error(e)) // Emit error
            }
        }
    }

    override suspend fun registerWithGoogle(idToken: String): Flow<Resource<UserDetailsModel>> {
        return flow {
            try {
                emit(Resource.Loading())
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = auth.signInWithCredential(credential).await()
                val userId = authResult.user?.uid

                if (userId == null) {
                    logAuthIssueToCrashlytics(
                        msg = "Sign up UserID not found",
                        provider = AuthProvider.GOOGLE.name
                    )
                    emit(Resource.Error(Exception("Sign up UserID not found")))
                    return@flow
                }

                // create user in firestore
                val user = UserDetailsModel(
                    name = authResult.user?.displayName ?: "",
                    email = authResult.user?.email ?: "",
                    id = userId,
                    createdAt = System.currentTimeMillis()
                    )

                // Add user to firestore
                firestore.collection("users").document(userId).set(user).await()
                emit(Resource.Success(user))

            }catch (e: Exception){
                logAuthIssueToCrashlytics(
                    e.message ?: "Unknown error from exception = ${e::class.java}",
                    AuthProvider.GOOGLE.name
                )
                emit(Resource.Error(e)) // Emit error
            }
        }
    }

    override suspend fun registerWithFacebook(idToken: String): Flow<Resource<UserDetailsModel>> {
        return flow {
            try {
                emit(Resource.Loading())
                // firebase auth sign up request
                val credential = FacebookAuthProvider.getCredential(idToken)
                val authResult = auth.signInWithCredential(credential).await()
                val userId = authResult.user?.uid

                if (userId == null) {
                    logAuthIssueToCrashlytics(
                        msg = "Sign up UserID not found",
                        provider = AuthProvider.FACEBOOK.name
                    )
                    emit(Resource.Error(Exception("Sign up UserID not found")))
                    return@flow
                }

                // create user in firestore
                val user = UserDetailsModel(
                    name = authResult.user?.displayName ?: "",
                    email = authResult.user?.email ?: "",
                    id = userId,
                    createdAt = System.currentTimeMillis()
                )

                // Add user to firestore
                firestore.collection("users").document(userId).set(user).await()
                emit(Resource.Success(user))

            }catch (e: Exception){
                logAuthIssueToCrashlytics(
                    e.message ?: "Unknown error from exception = ${e::class.java}",
                    AuthProvider.FACEBOOK.name
                )
                emit(Resource.Error(e)) // Emit error
            }
        }
    }

    private fun logAuthIssueToCrashlytics(msg: String, provider: String) {
        CrashlyticsUtils.sendCustomLogToCrashlytics<LoginException>(
            msg,
            CrashlyticsUtils.LOGIN_KEY to msg,
            CrashlyticsUtils.LOGIN_PROVIDER to provider,
        )
    }

}


