package com.vanlam.furnitureshop.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Address(
    val addressTitle: String,
    val fullName: String,
    val phone: String,
    val city: String,
    val street: String,
    val state: String
) : Parcelable {
    constructor(): this("", "", "", "", "", "")
}
