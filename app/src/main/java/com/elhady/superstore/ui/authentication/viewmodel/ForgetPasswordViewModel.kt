package com.elhady.superstore.ui.authentication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.elhady.superstore.data.model.Resource
import com.elhady.superstore.data.repository.auth.FirebaseAuthRepository
import com.elhady.superstore.data.repository.auth.FirebaseAuthRepositoryImpl
import com.elhady.superstore.utils.isValidEmail
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class ForgetPasswordViewModel(private val firebaseAuthRepository: FirebaseAuthRepository) : ViewModel() {

    private val _forgetPasswordState = MutableSharedFlow<Resource<String>>()
    val forgetPassword = _forgetPasswordState.asSharedFlow()

    val email = MutableStateFlow<String>("")


    fun sendPasswordResetEmail() {
        viewModelScope.launch(IO) {
            if (email.value.isValidEmail()) {
                firebaseAuthRepository.passwordResetEmailSend(email.value).collect {
                    _forgetPasswordState.emit(it)
                }
            } else {
                _forgetPasswordState.emit(Resource.Error(Exception("Email is not valid")))
            }
        }
    }

}

class ForgetPasswordViewModerFactory(private val firebaseAuthRepository: FirebaseAuthRepository = FirebaseAuthRepositoryImpl()) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ForgetPasswordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ForgetPasswordViewModel(firebaseAuthRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}