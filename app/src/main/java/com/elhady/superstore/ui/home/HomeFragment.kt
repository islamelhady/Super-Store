package com.elhady.superstore.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.elhady.superstore.databinding.FragmentHomeBinding
import com.elhady.superstore.utils.AddToCartException
import com.elhady.superstore.utils.CrashlyticsUtils

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        textView.setOnClickListener {
            CrashlyticsUtils.sendLogToCrashlytics("Crash", "No Internet")
            CrashlyticsUtils.sendCustomLogToCrashlytics<AddToCartException>(
                "Crash",
                Pair(CrashlyticsUtils.ADD_TO_CART_KEY, "Cart key"),
                Pair(CrashlyticsUtils.CUSTOM_KEY, "custom key")
            )
            throw AddToCartException("Crash")
//            throw RuntimeException("Test Crash") // Force a crash
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}