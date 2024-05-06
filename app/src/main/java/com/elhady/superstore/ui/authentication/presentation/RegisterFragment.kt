package com.elhady.superstore.ui.authentication.presentation

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.elhady.superstore.R
import com.elhady.superstore.data.model.Resource
import com.elhady.superstore.databinding.FragmentRegisterBinding
import com.elhady.superstore.ui.authentication.viewmodel.RegisterViewModel
import com.elhady.superstore.ui.authentication.viewmodel.RegisterViewModelFactory
import com.elhady.superstore.ui.common.ProgressDialog
import com.elhady.superstore.ui.home.showLoginSuccessDialog
import com.elhady.superstore.ui.home.showSnakeBarError
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel: RegisterViewModel by viewModels{ RegisterViewModelFactory(context = requireActivity()) }
    private val progressDialog by lazy { ProgressDialog.createProgressDialog(requireActivity()) }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListener()
        initViewModel()
    }

    private fun initViewModel() {
        lifecycleScope.launch {
            viewModel.registerState.collect{ resources ->
                when (resources){
                    is Resource.Loading -> {
                        progressDialog.show()
                    }
                    is Resource.Success -> {
                        progressDialog.dismiss()
                        view?.showLoginSuccessDialog()
                    }
                    is Resource.Error -> {
                        progressDialog.dismiss()
                        val msg = resources.exception?.message ?: getString(R.string.generic_err_msg)
                        Log.e(TAG, "initViewModel: $msg")
                        view?.showSnakeBarError(msg)
                    }
                }
            }
        }
    }

    private fun initListener() {
        binding.signInTv.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    companion object{
        private const val TAG = "RegisterFragment"
    }
}