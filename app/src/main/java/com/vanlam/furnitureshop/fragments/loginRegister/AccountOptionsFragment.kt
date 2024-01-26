package com.vanlam.furnitureshop.fragments.loginRegister

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.vanlam.furnitureshop.R
import com.vanlam.furnitureshop.databinding.FragmentAccountOptionsBinding

class AccountOptionsFragment : Fragment() {
    private lateinit var binding: FragmentAccountOptionsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountOptionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btnRegister.setOnClickListener {
                findNavController().navigate(R.id.action_accountOptionsFragment_to_registerFragment)
            }

            btnLogin.setOnClickListener {
                findNavController().navigate(R.id.action_accountOptionsFragment_to_loginFragment)
            }
        }
    }
}