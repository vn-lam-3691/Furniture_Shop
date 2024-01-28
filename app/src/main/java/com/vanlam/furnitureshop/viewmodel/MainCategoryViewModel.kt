package com.vanlam.furnitureshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.vanlam.furnitureshop.data.Product
import com.vanlam.furnitureshop.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainCategoryViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
): ViewModel() {

    private val _specialProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val specialProducts: StateFlow<Resource<List<Product>>> = _specialProducts

    private val _bestDealProduct = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestDealProduct: StateFlow<Resource<List<Product>>> = _bestDealProduct

    private val _bestProduct = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestProduct: StateFlow<Resource<List<Product>>> = _bestProduct

    init {
        fetchSpecialProducts()
        fetchBestDealProducts()
        fetchBestProducts()
    }

    fun fetchSpecialProducts() {
        viewModelScope.launch {
            _specialProducts.emit(Resource.Loading())
        }

        firestore.collection("products").whereEqualTo("category", "Special Products").get()
            .addOnSuccessListener { result ->
                val specialProductsList = result.toObjects(Product::class.java)
                viewModelScope.launch {
                    _specialProducts.emit(Resource.Success(specialProductsList))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _specialProducts.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun fetchBestDealProducts() {
        viewModelScope.launch {
            _bestDealProduct.emit(Resource.Loading())
        }

        firestore.collection("products").whereEqualTo("category", "Best Deals").get()
            .addOnSuccessListener { result ->
                val bestDealProductList = result.toObjects(Product::class.java)
                viewModelScope.launch {
                    _bestDealProduct.emit(Resource.Success(bestDealProductList))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _bestDealProduct.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun fetchBestProducts() {
        viewModelScope.launch {
            _bestProduct.emit(Resource.Loading())
        }

        firestore.collection("products").get()
            .addOnSuccessListener { result ->
                val bestProductList = result.toObjects(Product::class.java)
                viewModelScope.launch {
                    _bestProduct.emit(Resource.Success(bestProductList))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _bestProduct.emit(Resource.Error(it.message.toString()))
                }
            }
    }
}