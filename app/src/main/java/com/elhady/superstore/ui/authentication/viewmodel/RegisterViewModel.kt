package com.elhady.superstore.ui.authentication.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.elhady.superstore.data.model.Resource
import com.elhady.superstore.data.model.UserDetailsModel
import com.elhady.superstore.data.repository.auth.FirebaseAuthRepository
import com.elhady.superstore.data.repository.auth.FirebaseAuthRepositoryImpl
import com.elhady.superstore.utils.isValidEmail
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.lang.Exception

class RegisterViewModel(private val firebaseAuthRepository: FirebaseAuthRepository) : ViewModel() {

    private val _registerState = MutableSharedFlow<Resource<UserDetailsModel>>()
    val registerState = _registerState.asSharedFlow()

    val name = MutableStateFlow("")
    val email = MutableStateFlow("")
    val password = MutableStateFlow("")
    val confirmPassword = MutableStateFlow("")

    private val isRegister =
        combine(name, email, password, confirmPassword) { name, email, password, confirmPassword ->
            name.isNotEmpty() && email.isValidEmail() && password.length >= 6 && password == confirmPassword && confirmPassword.isNotEmpty()
        }

    fun registerWithEmailAndPassword() {
        val name = name.value
        val email = email.value
        val password = password.value
        viewModelScope.launch(IO) {
            if (isRegister.first()) {
                firebaseAuthRepository.registerWithEmailAndPassword(name, email, password)
                    .collect {
                        _registerState.emit(it)
                    }
            } else {
                _registerState.emit(Resource.Error(message = Exception("Please check your inputs")))
            }
        }
    }

    fun registerWithGoogle(idToken: String) {
        viewModelScope.launch(IO) {
            firebaseAuthRepository.registerWithGoogle(idToken).collect {
                _registerState.emit(it)
            }
        }
    }
}

class RegisterViewModelFactory(context: Context) : ViewModelProvider.Factory {
    private val firebaseAuthRepository = FirebaseAuthRepositoryImpl()
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return RegisterViewModel(firebaseAuthRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}