package com.vanlam.furnitureshop.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.vanlam.furnitureshop.data.User
import com.vanlam.furnitureshop.utils.RegisterFieldState
import com.vanlam.furnitureshop.utils.RegisterValidation
import com.vanlam.furnitureshop.utils.Resource
import com.vanlam.furnitureshop.utils.validateEmail
import com.vanlam.furnitureshop.utils.validatePassword
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
): ViewModel() {
    private val _register = MutableStateFlow<Resource<FirebaseUser>>(Resource.Unspecified())
    val register: Flow<Resource<FirebaseUser>> = _register

    private val _validation = Channel<RegisterFieldState>()
    val validation = _validation.receiveAsFlow()

    fun createAccountWithEmailPassword(user: User, password: String) {
        if (validateData(user, password)) {
            runBlocking {
                _register.emit(Resource.Loading())
            }

            firebaseAuth.createUserWithEmailAndPassword(user.email, password)
                .addOnSuccessListener { authResult ->
                    // Handle if register success
                    authResult.user?.let {
                        _register.value = Resource.Success(it)
                    }
                }
                .addOnFailureListener {
                    // Handle if register failed
                    _register.value = Resource.Error(it.message.toString())
                }
        }
        else {
            val registerFieldState = RegisterFieldState(
                validateEmail(user.email),
                validatePassword(password)
            )
            runBlocking {
                _validation.send(registerFieldState)
            }
        }
    }

    private fun validateData(user: User, password: String): Boolean {
        val emailValidation = validateEmail(user.email)
        val passwordValidation = validatePassword(password)

        return (emailValidation is RegisterValidation.Success) && (passwordValidation is RegisterValidation.Success)
    }
}