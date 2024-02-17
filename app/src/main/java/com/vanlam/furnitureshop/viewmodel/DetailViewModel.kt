package com.vanlam.furnitureshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.vanlam.furnitureshop.data.CartProduct
import com.vanlam.furnitureshop.firebase.FirebaseCommon
import com.vanlam.furnitureshop.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseCommon: FirebaseCommon
): ViewModel() {

    private val _addToCart = MutableStateFlow<Resource<CartProduct>>(Resource.Unspecified())
    val addToCart = _addToCart.asStateFlow()

    fun addUpdateProductInCart(cartProduct: CartProduct) {
        firestore.collection("user").document(auth.uid!!)
            .collection("cart").whereEqualTo("product.id", cartProduct.product.id).get()
            .addOnSuccessListener {
                it.documents.let {
                    if (it.isEmpty()) {     // Add new product
                        addNewProductToCart(cartProduct)
                    }
                    else {
                        val product = it.first().toObject(CartProduct::class.java)
                        if (product == cartProduct) {   // Increase quantity
                            val documentID = it.first().id
                            increaseQuantity(documentID, cartProduct)
                        }
                        else {
                            addNewProductToCart(cartProduct)
                        }
                    }
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _addToCart.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    private fun addNewProductToCart(cartProduct: CartProduct) {
        firebaseCommon.addProductToCart(cartProduct) { cartProduct, exception ->
            viewModelScope.launch {
                if (exception == null) {
                    _addToCart.emit(Resource.Success(cartProduct!!))
                }
                else {
                    _addToCart.emit(Resource.Error(exception.message.toString()))
                }
            }
        }
    }

    private fun increaseQuantity(documentId: String, cartProduct: CartProduct) {
        firebaseCommon.increaseQuantity(documentId) { _, exception ->
            viewModelScope.launch {
                if (exception == null) {
                    _addToCart.emit(Resource.Success(cartProduct))
                }
                else {
                    _addToCart.emit(Resource.Error(exception.message.toString()))
                }
            }
        }
    }
}
