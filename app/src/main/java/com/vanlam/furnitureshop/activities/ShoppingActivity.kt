package com.vanlam.furnitureshop.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.vanlam.furnitureshop.R
import com.vanlam.furnitureshop.databinding.ActivityShoppingBinding
import com.vanlam.furnitureshop.utils.Resource
import com.vanlam.furnitureshop.viewmodel.CartViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ShoppingActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityShoppingBinding.inflate(layoutInflater)
    }

    private val viewModel by viewModels<CartViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val navHostManager = supportFragmentManager.findFragmentById(R.id.shopping_host_fragment) as NavHostFragment
        binding.bottomNav.setupWithNavController(navHostManager.navController)

        lifecycleScope.launchWhenStarted {
            viewModel.cartProducts.collectLatest {
                when (it) {
                    is Resource.Success -> {
                        val count = it.data?.size ?: 0
                        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
                        bottomNav.getOrCreateBadge(R.id.cartFragment).apply {
                            number = count
                            backgroundColor = resources.getColor(R.color.g_blue)
                        }
                    }
                    else -> Unit
                }
            }
        }
    }
}