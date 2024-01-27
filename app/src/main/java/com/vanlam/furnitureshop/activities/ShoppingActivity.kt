package com.vanlam.furnitureshop.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.vanlam.furnitureshop.R
import com.vanlam.furnitureshop.databinding.ActivityShoppingBinding

class ShoppingActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityShoppingBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val navHostManager = supportFragmentManager.findFragmentById(R.id.shopping_host_fragment) as NavHostFragment
        binding.bottomNav.setupWithNavController(navHostManager.navController)
    }
}