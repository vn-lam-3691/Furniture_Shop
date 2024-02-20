package com.vanlam.furnitureshop.helper

fun Float?.getProductPrice(price: Float): Float {
    // this --> offerPercent
    if (this == null) {
        return price
    }
    val remainingPricePercentage = 1f - this
    return remainingPricePercentage * price
}