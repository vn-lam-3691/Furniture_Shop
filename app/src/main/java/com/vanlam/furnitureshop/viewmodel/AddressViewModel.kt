package com.vanlam.furnitureshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.vanlam.furnitureshop.data.Address
import com.vanlam.furnitureshop.data.CartProduct
import com.vanlam.furnitureshop.data.User
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
    private val _updateAddress = MutableStateFlow<Resource<Address>>(Resource.Unspecified())
    val updateAddress = _updateAddress.asStateFlow()
    private val _deleteAddress = MutableSharedFlow<Resource<Address>>()
    val deleteAddress = _deleteAddress.asSharedFlow()
    private val _userAddress = MutableStateFlow<Resource<List<Address>>>(Resource.Unspecified())
    private val userAddress = _userAddress.asStateFlow()

    private var addressDocument = emptyList<DocumentSnapshot>()

    init {
        getUserAddress()
    }

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

    fun getUserAddress() {
        firestore.collection("user").document(auth.uid!!).collection("address")
            .addSnapshotListener { value, error ->
                if (error != null || value == null) {
                    viewModelScope.launch { _userAddress.emit(Resource.Error(error?.message.toString())) }
                }
                else {
                    addressDocument = value.documents
                    val userAddress = value.toObjects(Address::class.java)
                    viewModelScope.launch { _userAddress.emit(Resource.Success(userAddress)) }
                }
            }
    }

    fun updateAddress(address: Address, oldAddress: Address?) {
        // Need validation data before update

        val index = userAddress.value.data?.indexOf(oldAddress)

        if (index != null || index != -1) {
            val documentId = addressDocument[index!!].id
            viewModelScope.launch { _updateAddress.emit(Resource.Loading()) }

            firestore.runTransaction { transaction ->
                val documentRef = firestore.collection("user").document(auth.uid!!)
                    .collection("address").document(documentId)

                transaction.set(documentRef, address)
            }.addOnSuccessListener {
                viewModelScope.launch {
                    _updateAddress.emit(Resource.Success(address))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _updateAddress.emit(Resource.Error(it.message.toString()))
                }
            }
        }
    }

    fun deleteAddress(address: Address?) {
        viewModelScope.launch { _deleteAddress.emit(Resource.Loading()) }

        val index = userAddress.value.data?.indexOf(address)
        if (index != null || index != -1) {
            val documentId = addressDocument[index!!].id
            firestore.collection("user").document(auth.uid!!)
                .collection("address").document(documentId)
                .delete()
                .addOnSuccessListener {
                    viewModelScope.launch { _deleteAddress.emit(Resource.Success(address!!)) }
                }
                .addOnFailureListener {
                    viewModelScope.launch { _deleteAddress.emit(Resource.Error(it.message.toString())) }
                }
        }
    }
}