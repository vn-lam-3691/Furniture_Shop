package com.vanlam.furnitureshop.data

sealed class Category(val categoryName: String) {
    object Chair: Category("Chair")
    object Accessory: Category("Accessory")
    object Cupboard: Category("Cupboard")
    object Furniture: Category("Furniture")
    object Table: Category("Table")
}