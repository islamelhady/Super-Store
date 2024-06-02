package com.elhady.superstore.ui.authentication.presentation

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.elhady.superstore.R
import com.elhady.superstore.ui.authentication.viewmodel.ForgetPasswordViewModel

class ForgetPasswordFragment : Fragment() {

    companion object {
        fun newInstance() = ForgetPasswordFragment()
    }

    private lateinit var viewModel: ForgetPasswordViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_forget_password, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ForgetPasswordViewModel::class.java)
        // TODO: Use the ViewModel
    }

}