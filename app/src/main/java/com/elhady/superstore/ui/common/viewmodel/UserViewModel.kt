package com.elhady.superstore.ui.common.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.elhady.superstore.data.datastore.AppPreferencesDataSource
import com.elhady.superstore.data.repository.UserPreferenceRepository
import com.elhady.superstore.data.repository.UserPreferenceRepositoryImpl
import com.elhady.superstore.data.repository.common.AppDataStoreRepositoryImpl
import com.elhady.superstore.data.repository.common.AppPreferenceRepository

class UserViewModel(
    private val userPreferencesRepository: UserPreferenceRepository,
    private val appPreferenceRepository: AppPreferenceRepository
) : ViewModel() {

    suspend fun saveLoginState(isLoggedIn: Boolean) {
    }

    suspend fun isUserLoggedIn() = appPreferenceRepository.isLoggedIn()

}

class UserViewModelFactory(
    context: Context
) : ViewModelProvider.Factory {

    private val appPreferenceRepository = AppDataStoreRepositoryImpl(AppPreferencesDataSource(context))
    private val userPreferencesRepository = UserPreferenceRepositoryImpl(context)
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return UserViewModel(
                userPreferencesRepository, appPreferenceRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
