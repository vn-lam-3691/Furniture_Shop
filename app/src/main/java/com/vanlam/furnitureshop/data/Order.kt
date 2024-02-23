package com.vanlam.furnitureshop.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random.Default.nextLong

data class Order(
    val orderStatus: String = "",
    val address: Address = Address(),
    val products: List<CartProduct> = emptyList(),
    val totalPrice: Float = 0f,
    val dateOrder: String = SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH).format(Date()),
    val orderId: Long = nextLong(0, 100_000_000_000) + totalPrice.toLong()
)