package com.vanlam.furnitureshop.fragments.categories

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vanlam.furnitureshop.R
import com.vanlam.furnitureshop.adapters.BestProductAdapter
import com.vanlam.furnitureshop.databinding.FragmentBaseCategoryBinding
import com.vanlam.furnitureshop.databinding.FragmentMainCategoryBinding

open class BaseCategoryFragment : Fragment(R.layout.fragment_base_category) {
    private lateinit var binding: FragmentBaseCategoryBinding
    protected val bestProductAdapter by lazy { BestProductAdapter() }
    protected val offerProductAdapter by lazy { BestProductAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBaseCategoryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupOfferProductRv()
        setupBestProductRv()

//        binding.rvOffer.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//
//                if (!recyclerView.canScrollVertically(1) && dx != 0) {
//                    onOfferProductPagingRequest()
//                }
//            }
//        })

        binding.nestedScrollCate.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
            if (v.getChildAt(0).bottom <= v.height + scrollY) {
                onBestProductPagingRequest()
            }
        })
    }

//    open fun onOfferProductPagingRequest() { }

    open fun onBestProductPagingRequest() { }

    private fun setupBestProductRv() {
        binding.rvBestProduct.apply {
            layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = bestProductAdapter
        }
    }

    private fun setupOfferProductRv() {
        binding.rvOffer.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = offerProductAdapter
        }
    }

    protected fun showOfferLoading() {
        binding.progressLoadOffer.visibility = View.VISIBLE
    }

    protected fun hideOfferLoading() {
        binding.progressLoadOffer.visibility = View.GONE
    }

    protected fun showBestLoading() {
        binding.progressLoadBestProduct.visibility = View.VISIBLE
    }

    protected fun hideBestLoading() {
        binding.progressLoadBestProduct.visibility = View.GONE
    }
}