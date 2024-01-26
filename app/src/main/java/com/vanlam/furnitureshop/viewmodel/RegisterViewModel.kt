package com.vanlam.furnitureshop.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.vanlam.furnitureshop.data.User
import com.vanlam.furnitureshop.utils.Constants.USER_COLLECTION
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
    private val firebaseAuth: FirebaseAuth,
    private val db: FirebaseFirestore
): ViewModel() {
    private val _register = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val register: Flow<Resource<User>> = _register

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
                        saveUserInfo(it.uid, user)
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

    private fun saveUserInfo(userUid: String, user: User) {
        db.collection(USER_COLLECTION)
            .document(userUid)
            .set(user)
            .addOnSuccessListener {
                _register.value = Resource.Success(user)
            }
            .addOnFailureListener {
                _register.value = Resource.Error(it.message.toString())
            }
    }

    private fun validateData(user: User, password: String): Boolean {
        val emailValidation = validateEmail(user.email)
        val passwordValidation = validatePassword(password)

        return (emailValidation is RegisterValidation.Success) && (passwordValidation is RegisterValidation.Success)
    }
}