package com.vanlam.furnitureshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.vanlam.furnitureshop.data.Category
import com.vanlam.furnitureshop.data.Product
import com.vanlam.furnitureshop.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CategoryViewModel(
    private val firestore: FirebaseFirestore,
    private val category: Category
): ViewModel() {

    private val _offerProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val offerProducts = _offerProducts.asStateFlow()

    private val _bestProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestProducts = _bestProducts.asStateFlow()

    private val pageInfo = PageInfo()

    init {
        fetchOfferProducts()
        fetchBestProducts()
    }

    fun fetchOfferProducts() {
        viewModelScope.launch {
            _offerProducts.emit(Resource.Loading())
        }

        firestore.collection("products")
            .whereEqualTo("category", category.categoryName)
            .whereNotEqualTo("offerPercentage", null).get()
            .addOnSuccessListener {
                val productList = it.toObjects(Product::class.java)
                viewModelScope.launch {
                    _offerProducts.emit(Resource.Success(productList))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _offerProducts.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun fetchBestProducts() {
        if (!pageInfo.isPagingEnd) {
            viewModelScope.launch {
                _bestProducts.emit(Resource.Loading())
            }

            firestore.collection("products")
                .whereEqualTo("category", category.categoryName)
//                .whereEqualTo("offerPercentage", null)
                .limit(pageInfo.bestProductPage * 10).get()
                .addOnSuccessListener {
                    val productList = it.toObjects(Product::class.java)
                    pageInfo.isPagingEnd = productList == pageInfo.oldBestProduct
                    pageInfo.oldBestProduct = productList
                    viewModelScope.launch {
                        _bestProducts.emit(Resource.Success(productList))
                    }
                }
                .addOnFailureListener {
                    viewModelScope.launch {
                        _bestProducts.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
    }

    internal data class PageInfo(
        var bestProductPage: Long = 1,
        var oldBestProduct: List<Product> = emptyList(),
        var isPagingEnd: Boolean = false
    )
}