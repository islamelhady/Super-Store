package com.elhady.superstore.ui.common.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.elhady.superstore.data.repository.UserPreferenceRepository

class UserViewModel(
    private val userPreferencesRepository: UserPreferenceRepository
) : ViewModel() {

    suspend fun saveLoginState(isLoggedIn: Boolean) {
    }

}

class UserViewModelFactory(private val userPreferencesRepository: UserPreferenceRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return UserViewModel(userPreferencesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
