package com.vanlam.furnitureshop.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.vanlam.furnitureshop.data.User
import com.google.firebase.storage.StorageReference
import com.vanlam.furnitureshop.FurnitureApplication
import com.vanlam.furnitureshop.utils.RegisterValidation
import com.vanlam.furnitureshop.utils.Resource
import com.vanlam.furnitureshop.utils.validateEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class UserAccountViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storage: StorageReference,
    app: Application
): AndroidViewModel(app) {

    private val _user = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val user = _user.asStateFlow()
    private val _updateInfo = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val updateInfo = _updateInfo.asStateFlow()

    init {
        getUser()
    }

    fun getUser() {
        viewModelScope.launch { _user.emit(Resource.Loading()) }

        firestore.collection("user").document(auth.uid!!).get()
            .addOnSuccessListener {
                val user = it.toObject(User::class.java)
                user?.let {
                    viewModelScope.launch { _user.emit(Resource.Success(user)) }
                }
            }
            .addOnFailureListener {
                viewModelScope.launch { _user.emit(Resource.Error(it.message.toString())) }
            }
    }

    fun updateUserInfo(user: User, imageUri: Uri?) {
        val areInputValidate = validateEmail(user.email) is RegisterValidation.Success  &&
                user.firstName.trim().isNotEmpty() &&
                user.lastName.trim().isNotEmpty()

        if (!areInputValidate) {
            viewModelScope.launch { _updateInfo.emit(Resource.Error("Please check your input!")) }
            return
        }

        viewModelScope.launch { _updateInfo.emit(Resource.Loading()) }

        if (imageUri == null) {
            saveUserInfoWithoutImage(user, true)
        }
        else {
            saveUserInfoWithNewImage(user, imageUri)
        }
    }

    private fun saveUserInfoWithNewImage(user: User, imageUri: Uri) {
        viewModelScope.launch {
            try {
                val imageBitmap = MediaStore.Images.Media.getBitmap(getApplication<FurnitureApplication>().contentResolver, imageUri)
                val byteArrayOutputStream = ByteArrayOutputStream()
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 96, byteArrayOutputStream)
                val imageByteArray = byteArrayOutputStream.toByteArray()
                val imageDirectory = storage.child("profileImages/${auth.uid}/${UUID.randomUUID()}")
                val result = imageDirectory.putBytes(imageByteArray).await()
                val imageUrl = result.storage.downloadUrl.await().toString()

                saveUserInfoWithoutImage(user.copy(imagePath = imageUrl), false)
            } catch (e: Exception) {
                viewModelScope.launch {
                    _updateInfo.emit(Resource.Error(e.message.toString()))
                }
            }
        }
    }

    private fun saveUserInfoWithoutImage(user: User, shouldRetrivedOldImage: Boolean) {
        firestore.runTransaction { transition ->
            val documentRef = firestore.collection("user").document(auth.uid!!)
            if (shouldRetrivedOldImage) {
                val currentUser = transition.get(documentRef).toObject(User::class.java)
                val newUser = User(user.firstName, user.lastName, user.email, currentUser?.imagePath ?: "")
                transition.set(documentRef, newUser)
            }
            else {
                transition.set(documentRef, user)
            }
        }.addOnSuccessListener {
            viewModelScope.launch {
                _updateInfo.emit(Resource.Success(user))
            }
        }.addOnFailureListener {
            viewModelScope.launch {
                _updateInfo.emit(Resource.Error(it.message.toString()))
            }
        }
    }
}