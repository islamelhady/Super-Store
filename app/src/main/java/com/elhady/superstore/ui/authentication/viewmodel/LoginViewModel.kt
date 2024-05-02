package com.elhady.superstore.ui.authentication.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.elhady.superstore.data.datastore.AppPreferencesDataSource
import com.elhady.superstore.data.model.Resource
import com.elhady.superstore.data.model.UserDetailsModel
import com.elhady.superstore.data.repository.auth.FirebaseAuthRepository
import com.elhady.superstore.data.repository.auth.FirebaseAuthRepositoryImpl
import com.elhady.superstore.data.repository.common.AppDataStoreRepositoryImpl
import com.elhady.superstore.data.repository.common.AppPreferenceRepository
import com.elhady.superstore.utils.isValidEmail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LoginViewModel(
    private val appPreferenceRepository: AppPreferenceRepository,
    private val authRepository: FirebaseAuthRepository
) : ViewModel() {

    private val _loginState = MutableSharedFlow<Resource<UserDetailsModel>>()
    val loginState: SharedFlow<Resource<UserDetailsModel>> = _loginState.asSharedFlow()

    val email = MutableStateFlow("")
    val password = MutableStateFlow("")

    private val isLoginIsValid: Flow<Boolean> = combine(email, password) { email, password ->
        email.isValidEmail() && password.length >= 6
    }

    fun loginWithEmailAndPassword() = viewModelScope.launch(Dispatchers.IO) {
        val email = email.value
        val password = password.value
        if (isLoginIsValid.first()) {
            handleLoginFlow { authRepository.loginWithEmailAndPassword(email, password) }
        } else {
            _loginState.emit(Resource.Error(Exception("Invalid email or password")))
        }
    }

    fun loginWithGoogle(idToken: String) = viewModelScope.launch(Dispatchers.IO) {
        handleLoginFlow { authRepository.loginWithGoogle(idToken) }
    }

    private fun handleLoginFlow(loginFlow: suspend () -> Flow<Resource<UserDetailsModel>>) =
        viewModelScope.launch(Dispatchers.IO) {
            loginFlow().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        savePreferenceData(resource.data!!)
                        Log.d("success id", resource.data.id.toString())
                        _loginState.emit(Resource.Success(resource.data))
                    }

                    else -> _loginState.emit(resource)
                }
            }
        }

    private suspend fun savePreferenceData(userDetailsModel: UserDetailsModel) {
        appPreferenceRepository.saveLoginState(true)
//        userPreferenceRepository.updateUserDetails(userDetailsModel.toUserDetailsPreferences())
    }
}

// create viewmodel factory class
class LoginViewModelFactory(
    private val contextValue: Context
) : ViewModelProvider.Factory {

    private val appPreferenceRepository = AppDataStoreRepositoryImpl(AppPreferencesDataSource(contextValue))
    private val authRepository = FirebaseAuthRepositoryImpl()

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return LoginViewModel(
                appPreferenceRepository,
                authRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}