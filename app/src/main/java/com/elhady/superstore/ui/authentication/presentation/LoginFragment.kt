package com.elhady.superstore.ui.authentication.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.elhady.superstore.MainActivity
import com.elhady.superstore.R
import com.elhady.superstore.data.model.Resource
import com.elhady.superstore.databinding.FragmentLoginBinding
import com.elhady.superstore.ui.authentication.viewmodel.LoginViewModel
import com.elhady.superstore.ui.authentication.viewmodel.LoginViewModelFactory
import com.elhady.superstore.ui.common.ProgressDialog.Companion.createProgressDialog
import com.elhady.superstore.ui.home.showSnakeBarError
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {


    private val loginViewModel: LoginViewModel by viewModels(){
        LoginViewModelFactory(requireContext())
    }
    private lateinit var binding: FragmentLoginBinding
    private val progressDialog by lazy { createProgressDialog(requireActivity()) }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater,container,false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = loginViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()
    }

    private fun initViewModel() {
        lifecycleScope.launch {
            loginViewModel.loginState.collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        progressDialog.show()
                    }

                    is Resource.Success -> {
                        progressDialog.dismiss()
                        goToHome()
                    }

                    is Resource.Error -> {
                        progressDialog.dismiss()
                        val msg = resource.exception?.message ?: getString(R.string.generic_err_msg)
                        Log.d(TAG, "initViewModelError: $msg")
                        view?.showSnakeBarError(msg)
                    }
                }
            }
        }
    }

    private fun goToHome() {
        requireActivity().startActivity(Intent(activity, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        requireActivity().finish()
    }


    companion object {
        private const val TAG = "LoginFragment"
    }
}