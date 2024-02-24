package com.vanlam.furnitureshop.fragments.shopping

import android.content.Intent
import android.content.pm.VersionedPackage
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.intuit.sdp.BuildConfig
import com.vanlam.furnitureshop.R
import com.vanlam.furnitureshop.activities.LoginRegisterActivity
import com.vanlam.furnitureshop.databinding.FragmentProfileBinding
import com.vanlam.furnitureshop.utils.Resource
import com.vanlam.furnitureshop.utils.showBottomNavigationView
import com.vanlam.furnitureshop.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment: Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val viewModel by viewModels<ProfileViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.linearAllOrders.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_ordersFragment)
        }

        binding.constraintProfile.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_userAccountFragment)
        }

        binding.linearAddress.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_userAddressFragment)
        }

        binding.linearLogOut.setOnClickListener {
            viewModel.logout()
            Intent(requireContext(), LoginRegisterActivity::class.java).also {
                startActivity(it)
            }
            requireActivity().finish()
        }

        lifecycleScope.launchWhenStarted {
            viewModel.user.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressbarSettings.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressbarSettings.visibility = View.INVISIBLE
                        Glide.with(requireView()).load(it.data!!.imagePath).error(ColorDrawable(Color.BLACK)).into(binding.imageUser)
                        binding.tvUserName.text = "${it.data.firstName} ${it.data.lastName}"
                    }
                    is Resource.Error -> {
                        binding.progressbarSettings.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        showBottomNavigationView()
    }
}