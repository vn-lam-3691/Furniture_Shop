package com.vanlam.furnitureshop.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.vanlam.furnitureshop.adapters.HomeViewPagerAdapter
import com.vanlam.furnitureshop.databinding.FragmentHomeBinding
import com.vanlam.furnitureshop.fragments.categories.AccessoryFragment
import com.vanlam.furnitureshop.fragments.categories.ChairFragment
import com.vanlam.furnitureshop.fragments.categories.CupboardFragment
import com.vanlam.furnitureshop.fragments.categories.FurnitureFragment
import com.vanlam.furnitureshop.fragments.categories.MainCategoryFragment
import com.vanlam.furnitureshop.fragments.categories.TableFragment

class HomeFragment: Fragment() {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoryFragments = arrayListOf<Fragment>(
            MainCategoryFragment(),
            ChairFragment(),
            AccessoryFragment(),
            CupboardFragment(),
            FurnitureFragment(),
            TableFragment()
        )

        val viewPager2Adapter = HomeViewPagerAdapter(categoryFragments, childFragmentManager, lifecycle)
        binding.viewpagerHome.adapter = viewPager2Adapter
        TabLayoutMediator(binding.tabLayout, binding.viewpagerHome) { tab, position ->
            when (position) {
                0 -> tab.text = "Main"
                1 -> tab.text = "Chair"
                2 -> tab.text = "Accessory"
                3 -> tab.text = "Cupboard"
                4 -> tab.text = "Furniture"
                5 -> tab.text = "Table"
            }
        }.attach()
    }
}