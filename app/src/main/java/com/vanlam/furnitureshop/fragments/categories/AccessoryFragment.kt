package com.vanlam.furnitureshop.fragments.categories

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.vanlam.furnitureshop.data.Category
import com.vanlam.furnitureshop.utils.Resource
import com.vanlam.furnitureshop.viewmodel.CategoryViewModel
import com.vanlam.furnitureshop.viewmodel.factory.BaseCategoryViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class AccessoryFragment: BaseCategoryFragment() {
    @Inject
    lateinit var firestore: FirebaseFirestore

    private val viewModel by viewModels<CategoryViewModel> {
        BaseCategoryViewModelFactory(firestore, Category.Accessory)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenStarted {
            viewModel.bestProducts.collectLatest {
                when (it) {
                    is Resource.Success -> {
                        bestProductAdapter.differ.submitList(it.data)
                        hideBestLoading()
                    }
                    is Resource.Loading -> {
                        showBestLoading()
                    }
                    is Resource.Error -> {
                        Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_SHORT).show()
                        hideBestLoading()
                    }
                    else -> Unit
                }
            }
        }
    }

    override fun onBestProductPagingRequest() {
        super.onBestProductPagingRequest()
        viewModel.fetchBestProducts()
    }
}