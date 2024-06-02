package com.elhady.superstore.ui.common.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.elhady.superstore.data.datastore.AppPreferencesDataSource
import com.elhady.superstore.data.repository.common.AppDataStoreRepositoryImpl
import com.elhady.superstore.data.repository.common.AppPreferenceRepository
import com.elhady.superstore.data.repository.user.UserFireStoreRepository
import com.elhady.superstore.data.repository.user.UserFireStoreRepositoryImpl

class UserViewModel(
    private val appPreferenceRepository: AppPreferenceRepository,
    private val userFireStoreRepository: UserFireStoreRepository
) : ViewModel() {

    suspend fun saveLoginState(isLoggedIn: Boolean) {
    }

    suspend fun isUserLoggedIn() = false

}

class UserViewModelFactory(
    context: Context
) : ViewModelProvider.Factory {

    private val appPreferenceRepository = AppDataStoreRepositoryImpl(AppPreferencesDataSource(context))
    private val userFireStoreRepository = UserFireStoreRepositoryImpl()
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return UserViewModel(
                appPreferenceRepository, userFireStoreRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
