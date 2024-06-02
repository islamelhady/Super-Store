package com.elhady.superstore.data.repository.user

import com.elhady.superstore.data.model.Resource
import com.elhady.superstore.data.model.UserDetailsModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class UserFireStoreRepositoryImpl(
    private val fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : UserFireStoreRepository {

    override suspend fun getUserDetails(userId: String): Flow<Resource<UserDetailsModel>> {
        return callbackFlow {
            send(Resource.Loading())
            val documentPath = "users/$userId"
            val document = fireStore.document(documentPath)
            val listener = document.addSnapshotListener { value, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                value?.toObject(UserDetailsModel::class.java)?.let {
                    if (it.disabled == true) {
                        close(AccountDisabledException("Account Disabled"))
                        return@addSnapshotListener
                    }
                    trySend(Resource.Success(it))
                } ?: run {
                    close(UserNotFoundException("User not found"))
                }
            }
            awaitClose {
                listener.remove()
            }
        }
    }
}

class UserNotFoundException(message: String) : Exception(message)
class AccountDisabledException(message: String) : Exception(message)