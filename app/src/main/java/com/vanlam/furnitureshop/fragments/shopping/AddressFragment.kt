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
import androidx.navigation.fragment.navArgs
import com.vanlam.furnitureshop.R
import com.vanlam.furnitureshop.data.Address
import com.vanlam.furnitureshop.data.User
import com.vanlam.furnitureshop.databinding.FragmentAddressBinding
import com.vanlam.furnitureshop.utils.Resource
import com.vanlam.furnitureshop.viewmodel.AddressViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class AddressFragment : Fragment() {
    private lateinit var binding: FragmentAddressBinding
    private val viewModel by viewModels<AddressViewModel>()
    private val agrs by navArgs<AddressFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launchWhenStarted {
            viewModel.addAddress.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressbarAddress.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressbarAddress.visibility = View.GONE
                        findNavController().navigateUp()
                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.errorMsg.collectLatest {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val address = agrs.address
        if (address == null) {
            binding.buttonDelelte.visibility = View.GONE
            binding.buttonUpdate.visibility = View.GONE
            binding.buttonSave.visibility = View.VISIBLE
        }
        else {
            binding.buttonDelelte.visibility = View.VISIBLE
            binding.buttonUpdate.visibility = View.VISIBLE
            binding.buttonSave.visibility = View.GONE

            binding.apply {
                edAddressTitle.setText(address.addressTitle)
                edFullName.setText(address.fullName)
                edPhone.setText(address.phone)
                edStreet.setText(address.street)
                edCity.setText(address.city)
                edState.setText(address.state)
            }
        }

        binding.buttonSave.setOnClickListener {
            val newAddress = getAddressDataField()
            viewModel.addNewAddress(newAddress)
        }

        binding.buttonUpdate.setOnClickListener {
            val newAddress = getAddressDataField()
            viewModel.updateAddress(newAddress, address)
        }

        binding.buttonDelelte.setOnClickListener {
            viewModel.deleteAddress(address)
        }

        binding.imageAddressClose.setOnClickListener {
            findNavController().navigateUp()
        }

        lifecycleScope.launchWhenStarted {
            viewModel.updateAddress.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressbarAddress.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressbarAddress.visibility = View.GONE
                        Toast.makeText(requireContext(), "Update address success", Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Error -> {
                        binding.progressbarAddress.visibility = View.GONE
                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.deleteAddress.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressbarAddress.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressbarAddress.visibility = View.GONE
                        findNavController().navigateUp()
                    }
                    is Resource.Error -> {
                        binding.progressbarAddress.visibility = View.GONE
                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
    }

    fun getAddressDataField(): Address {
        binding.apply {
            val addressTitle = edAddressTitle.text.trim().toString()
            val fullName = edFullName.text.trim().toString()
            val phone = edPhone.text.trim().toString()
            val city = edCity.text.trim().toString()
            val street = edStreet.text.trim().toString()
            val state = edState.text.trim().toString()

            return Address(addressTitle, fullName, phone, city, street, state)
        }
    }
}