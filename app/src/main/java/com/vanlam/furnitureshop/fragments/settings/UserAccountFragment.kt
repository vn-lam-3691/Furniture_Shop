package com.vanlam.furnitureshop.fragments.settings

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.vanlam.furnitureshop.R
import com.vanlam.furnitureshop.data.User
import com.vanlam.furnitureshop.databinding.FragmentUserAccountBinding
import com.vanlam.furnitureshop.dialog.setupBottomSheetDialog
import com.vanlam.furnitureshop.utils.Resource
import com.vanlam.furnitureshop.viewmodel.UserAccountViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class UserAccountFragment: Fragment() {
    private lateinit var binding: FragmentUserAccountBinding
    private val viewModel by viewModels<UserAccountViewModel>()
    private lateinit var imageActivityResultLauncher: ActivityResultLauncher<Intent>

    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            imageUri = it.data?.data
            Glide.with(this).load(imageUri).into(binding.imageUser)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)?.visibility = View.GONE
        binding = FragmentUserAccountBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenStarted {
            viewModel.user.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        showLoadingUser()
                    }
                    is Resource.Success -> {
                        hideLoadingUser()
                        showInfoUser(it.data!!)
                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.updateInfo.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.buttonSave.startAnimation()
                    }
                    is Resource.Success -> {
                        binding.buttonSave.revertAnimation()
                        findNavController().navigateUp()
                    }
                    is Resource.Error -> {
                        binding.buttonSave.revertAnimation()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.resetPassword.collect {
                when (it) {
                    is Resource.Success -> {
                        Snackbar.make(requireView(), "Reset link was send to your email", Snackbar.LENGTH_SHORT).show()
                    }
                    is Resource.Error -> {
                        Snackbar.make(requireView(), "Error: ${it.message}", Snackbar.LENGTH_SHORT).show()
                    }
                    is Resource.Loading -> { }
                    else -> Unit
                }
            }
        }

        binding.buttonSave.setOnClickListener {
            binding.apply {
                val firstName = edFirstName.text.trim().toString()
                val lastName = edLastName.text.trim().toString()
                val email = edEmail.text.trim().toString()
                val user = User(firstName, lastName, email)

                viewModel.updateUserInfo(user, imageUri)
            }
        }

        binding.imageEdit.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            imageActivityResultLauncher.launch(intent)
        }

        binding.tvUpdatePassword.setOnClickListener {
            setupAlertDialog()
        }
    }

    private fun setupAlertDialog() {
        setupBottomSheetDialog {  email ->
            viewModel.resetPassword(email)
        }
    }

    private fun showInfoUser(user: User) {
        binding.apply {
            Glide.with(this@UserAccountFragment).load(user.imagePath).error(ColorDrawable(Color.BLACK)).into(imageUser)
            edEmail.setText(user.email.toString())
            edFirstName.setText(user.firstName)
            edLastName.setText(user.lastName)
        }
    }

    private fun hideLoadingUser() {
        binding.apply {
            progressbarAccount.visibility = View.GONE
            imageUser.visibility = View.VISIBLE
            imageEdit.visibility = View.VISIBLE
            edFirstName.visibility = View.VISIBLE
            edLastName.visibility = View.VISIBLE
            edEmail.visibility = View.VISIBLE
            tvUpdatePassword.visibility = View.VISIBLE
            buttonSave.visibility = View.VISIBLE
        }
    }

    private fun showLoadingUser() {
        binding.apply {
            progressbarAccount.visibility = View.VISIBLE
            imageUser.visibility = View.INVISIBLE
            imageEdit.visibility = View.INVISIBLE
            edFirstName.visibility = View.INVISIBLE
            edLastName.visibility = View.INVISIBLE
            edEmail.visibility = View.INVISIBLE
            tvUpdatePassword.visibility = View.INVISIBLE
            buttonSave.visibility = View.INVISIBLE
        }
    }
}