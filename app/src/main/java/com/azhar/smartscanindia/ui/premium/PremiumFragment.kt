package com.azhar.smartscanindia.ui.premium

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.azhar.smartscanindia.R
import com.azhar.smartscanindia.databinding.FragmentPremiumBinding

class PremiumFragment : Fragment(R.layout.fragment_premium) {
    private var _binding: FragmentPremiumBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPremiumBinding.bind(view)
        binding.tvPremium.text = getString(R.string.premium_description)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
