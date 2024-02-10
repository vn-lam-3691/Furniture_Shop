package com.vanlam.furnitureshop.utils

import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.vanlam.furnitureshop.R
import com.vanlam.furnitureshop.activities.ShoppingActivity

fun Fragment.hideBottomNavigationView() {
    val bottomNavigationView = (activity as ShoppingActivity).findViewById<BottomNavigationView>(R.id.bottom_nav)
    bottomNavigationView.visibility = View.GONE
}

fun Fragment.showBottomNavigationView() {
    val bottomNavigationView = (activity as ShoppingActivity).findViewById<BottomNavigationView>(R.id.bottom_nav)
    bottomNavigationView.visibility = View.VISIBLE
}