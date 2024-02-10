package com.vanlam.furnitureshop.fragments.shopping

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.vanlam.furnitureshop.R
import com.vanlam.furnitureshop.activities.ShoppingActivity
import com.vanlam.furnitureshop.adapters.ColorProductAdapter
import com.vanlam.furnitureshop.adapters.SizeProductAdapter
import com.vanlam.furnitureshop.adapters.ViewPager2Image
import com.vanlam.furnitureshop.databinding.FragmentProductDetailBinding
import com.vanlam.furnitureshop.utils.hideBottomNavigationView

class ProductDetailFragment : Fragment() {
    private lateinit var binding: FragmentProductDetailBinding
    private val viewPagerAdapter by lazy { ViewPager2Image() }
    private val colorAdapter by lazy { ColorProductAdapter() }
    private val sizeAdapter by lazy { SizeProductAdapter() }
    private val agrs by navArgs<ProductDetailFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Hide bottom nav when display detail product
        hideBottomNavigationView()

        binding = FragmentProductDetailBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val product = agrs.product

        setupSizeRv()
        setupColorRv()
        setupViewPagerImage()

        binding.imgClose.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.apply {
            tvProductName.text = product.name
            tvProductPrice.text = "$ ${product.price}"
            tvProductDesc.text = product.description

            if (product.colors.isNullOrEmpty()) {
                tvProductColors.visibility = View.INVISIBLE
            }

            if (product.sizes.isNullOrEmpty()) {
                tvProductSizes.visibility = View.INVISIBLE
            }
        }

        viewPagerAdapter.differ.submitList(product.images)
        product.colors?.let {
            colorAdapter.differ.submitList(it)
        }
        product.sizes?.let {
            sizeAdapter.differ.submitList(it)
        }
    }

    private fun setupViewPagerImage() {
        binding.apply {
            viewPagerProductImage.adapter = viewPagerAdapter
        }
    }

    private fun setupColorRv() {
        binding.rvProductColors.apply {
            adapter = colorAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun setupSizeRv() {
        binding.rvProductSize.apply {
            adapter = sizeAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }
}