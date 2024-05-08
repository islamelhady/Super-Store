package com.elhady.superstore.ui.authentication.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.elhady.superstore.databinding.FragmentForgetPasswordBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ForgetPasswordFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentForgetPasswordBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentForgetPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }


}