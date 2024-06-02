package com.elhady.superstore.data.repository.user

import com.elhady.superstore.data.model.Resource
import com.elhady.superstore.data.model.UserDetailsModel
import kotlinx.coroutines.flow.Flow

interface UserFireStoreRepository {
      suspend fun getUserDetails(userId: String): Flow<Resource<UserDetailsModel>>

}