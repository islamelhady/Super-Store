package com.elhady.superstore.ui.authentication.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.elhady.superstore.R
import com.elhady.superstore.data.model.Resource
import com.elhady.superstore.databinding.FragmentForgetPasswordBinding
import com.elhady.superstore.ui.authentication.viewmodel.ForgetPasswordViewModel
import com.elhady.superstore.ui.authentication.viewmodel.ForgetPasswordViewModerFactory
import com.elhady.superstore.ui.common.ProgressDialog
import com.elhady.superstore.ui.home.showLoginSuccessDialog
import com.elhady.superstore.ui.home.showSentEmailSuccessDialog
import com.elhady.superstore.ui.home.showSnakeBarError
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class ForgetPasswordFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentForgetPasswordBinding
    private val viewModel: ForgetPasswordViewModel by viewModels{ ForgetPasswordViewModerFactory() }
    private val progressDialog by lazy { ProgressDialog.createProgressDialog(requireActivity()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentForgetPasswordBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewmodel()
    }

    private fun initViewmodel() {
        lifecycleScope.launch {
            viewModel.forgetPassword.collect{ state ->
                when(state){
                    is Resource.Loading -> {
                     progressDialog.show()
                    }
                    is Resource.Success -> {
                        progressDialog.dismiss()
                        view?.showSentEmailSuccessDialog()
                    }
                    is Resource.Error -> {
                        progressDialog.dismiss()
                        val msg = state.exception?.message?: getString(R.string.generic_err_msg)
                        Log.d(TAG, "initViewmodel: ${state.exception}")
                        view?.showSnakeBarError(msg)
                    }
                }
            }
        }
    }

    companion object{
        const val TAG = "ForgetPasswordFragment"
    }
}