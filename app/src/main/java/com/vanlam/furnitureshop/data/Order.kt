package com.vanlam.furnitureshop.data

data class Order(
    val orderStatus: String,
    val address: Address,
    val products: List<CartProduct>,
    val totalPrice: Float
)