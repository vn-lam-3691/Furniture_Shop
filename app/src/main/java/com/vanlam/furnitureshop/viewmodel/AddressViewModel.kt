package com.vanlam.furnitureshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.vanlam.furnitureshop.data.Address
import com.vanlam.furnitureshop.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
): ViewModel() {

    private val _addAddress = MutableStateFlow<Resource<Address>>(Resource.Unspecified())
    val addAddress = _addAddress.asStateFlow()
    private val _errorMsg = MutableSharedFlow<String>()
    val errorMsg = _errorMsg.asSharedFlow()

    fun addNewAddress(address: Address) {
        val validateInput = validateInputData(address)

        if (validateInput) {
            viewModelScope.launch { _addAddress.emit(Resource.Loading()) }

            firestore.collection("user").document(auth.uid!!)
                .collection("address").document()
                .set(address)
                .addOnSuccessListener {
                    viewModelScope.launch { _addAddress.emit(Resource.Success(address)) }
                }
                .addOnFailureListener {
                    viewModelScope.launch { _addAddress.emit(Resource.Error(it.message.toString())) }
                }
        }
        else {
            viewModelScope.launch {
                _errorMsg.emit("All fields are require")
            }
        }
    }

    private fun validateInputData(address: Address): Boolean {
        return address.addressTitle.trim().isNotEmpty() &&
                address.fullName.trim().isNotEmpty() &&
                address.phone.trim().isNotEmpty() &&
                address.city.trim().isNotEmpty() &&
                address.street.trim().isNotEmpty() &&
                address.state.trim().isNotEmpty()
    }
}