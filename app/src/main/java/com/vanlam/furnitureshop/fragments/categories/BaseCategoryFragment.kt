package com.vanlam.furnitureshop.fragments.categories

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.widget.NestedScrollView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vanlam.furnitureshop.R
import com.vanlam.furnitureshop.adapters.BestProductAdapter
import com.vanlam.furnitureshop.databinding.FragmentBaseCategoryBinding
import com.vanlam.furnitureshop.databinding.FragmentMainCategoryBinding
import com.vanlam.furnitureshop.utils.GridSpacingItemDecoration
import com.vanlam.furnitureshop.utils.showBottomNavigationView

open class BaseCategoryFragment : Fragment(R.layout.fragment_base_category) {
    private lateinit var binding: FragmentBaseCategoryBinding
    protected val bestProductAdapter by lazy { BestProductAdapter() }

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

        setupBestProductRv()

        bestProductAdapter.onClick = {
            val bundle = Bundle().apply { putParcelable("product", it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailFragment, bundle)
        }

        binding.nestedScrollCate.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
            if (v.getChildAt(0).bottom <= v.height + scrollY) {
                onBestProductPagingRequest()
            }
        })
    }


    open fun onBestProductPagingRequest() { }

    private fun setupBestProductRv() {
        binding.rvBestProduct.apply {
            layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = bestProductAdapter

            val spacingInPixels = resources.getDimensionPixelSize(R.dimen.gridSpacing)
            addItemDecoration(GridSpacingItemDecoration(spacingInPixels, 2))
        }
    }

    protected fun showBestLoading() {
        binding.progressLoadBestProduct.visibility = View.VISIBLE
    }

    protected fun hideBestLoading() {
        binding.progressLoadBestProduct.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        showBottomNavigationView()
    }
}