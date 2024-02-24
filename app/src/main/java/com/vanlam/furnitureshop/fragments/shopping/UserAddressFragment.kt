package com.vanlam.furnitureshop.fragments.shopping

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.vanlam.furnitureshop.R
import com.vanlam.furnitureshop.adapters.AllAddressAdapter
import com.vanlam.furnitureshop.databinding.FragmentUserAddressBinding
import com.vanlam.furnitureshop.utils.Resource
import com.vanlam.furnitureshop.viewmodel.UserAddressViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserAddressFragment : Fragment() {
    private lateinit var binding: FragmentUserAddressBinding
    private val viewModel by viewModels<UserAddressViewModel>()
    private val allAddressAdapter by lazy { AllAddressAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserAddressBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAddressesRv()

        lifecycleScope.launch {
            viewModel.addresses.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressbarAddress.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressbarAddress.visibility = View.GONE
                        allAddressAdapter.differ.submitList(it.data)
                    }
                    is Resource.Error -> {
                        binding.progressbarAddress.visibility = View.GONE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        binding.imageCloseUserAccount.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.imageAddNew.setOnClickListener {
            val action = UserAddressFragmentDirections.actionUserAddressFragmentToAddressFragment(null)
            findNavController().navigate(action)
        }

        allAddressAdapter.onClick = {
            val action = UserAddressFragmentDirections.actionUserAddressFragmentToAddressFragment(it)
            findNavController().navigate(action)
        }
    }

    private fun setupAddressesRv() {
        binding.rvAllAddress.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = allAddressAdapter
        }
    }
}
