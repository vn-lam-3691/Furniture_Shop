package com.vanlam.furnitureshop.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.vanlam.furnitureshop.data.User
import com.vanlam.furnitureshop.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
): ViewModel() {
    private val _register = MutableStateFlow<Resource<FirebaseUser>>(Resource.Unspecified())
    val register: Flow<Resource<FirebaseUser>> = _register

    fun createAccountWithEmailPassword(user: User, password: String) {
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
}