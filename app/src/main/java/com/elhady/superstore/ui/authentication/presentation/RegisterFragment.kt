package com.elhady.superstore.ui.authentication.presentation

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.elhady.superstore.R
import com.elhady.superstore.data.model.AuthProvider
import com.elhady.superstore.data.model.Resource
import com.elhady.superstore.databinding.FragmentRegisterBinding
import com.elhady.superstore.ui.authentication.getGoogleRequestIntent
import com.elhady.superstore.ui.authentication.viewmodel.RegisterViewModel
import com.elhady.superstore.ui.authentication.viewmodel.RegisterViewModelFactory
import com.elhady.superstore.ui.common.ProgressDialog
import com.elhady.superstore.ui.home.showLoginSuccessDialog
import com.elhady.superstore.ui.home.showSnakeBarError
import com.elhady.superstore.utils.CrashlyticsUtils
import com.elhady.superstore.utils.RegisterException
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel: RegisterViewModel by viewModels { RegisterViewModelFactory(context = requireActivity()) }
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
            viewModel.registerState.collect { resources ->
                when (resources) {
                    is Resource.Loading -> {
                        progressDialog.show()
                    }

                    is Resource.Success -> {
                        progressDialog.dismiss()
                        view?.showLoginSuccessDialog()
                    }

                    is Resource.Error -> {
                        progressDialog.dismiss()
                        val msg =
                            resources.exception?.message ?: getString(R.string.generic_err_msg)
                        Log.e(TAG, "initViewModel: $msg")
                        view?.showSnakeBarError(msg)
                    }
                }
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        viewModel.registerWithGoogle(idToken)
    }

    private fun signInWithGoogleRequest() {
        val signInIntent = getGoogleRequestIntent(requireActivity())
        launcher.launch(signInIntent)
    }

    // ActivityResultLauncher for the sign-in intent launched by Google SignIn API
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(activityResult.data)
            handelSignInResult(task)
        }else{
            view?.showSnakeBarError(getString(R.string.google_sign_in_field_msg))
        }
    }

    fun handelSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: Exception) {
            Log.e(TAG, "handelSignInResult: ${e.message}")
            view?.showSnakeBarError(e.message ?: getString(R.string.generic_err_msg))
            val msg = e.message ?: getString(R.string.generic_err_msg)
            logAuthIssueToCrashlytics(msg, AuthProvider.GOOGLE.name)
        }
    }

    private fun logAuthIssueToCrashlytics(msg: String, provide: String) {
        CrashlyticsUtils.sendCustomLogToCrashlytics<RegisterException>(
            msg,
            CrashlyticsUtils.REGISTER_KEY to msg
        )
        Log.e(TAG, "registerWithGoogle: $msg")
    }

    private fun initListener() {
        binding.signInTv.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.googleRegisterBtn.setOnClickListener {
            signInWithGoogleRequest()
        }
    }

    companion object {
        private const val TAG = "RegisterFragment"
    }
}