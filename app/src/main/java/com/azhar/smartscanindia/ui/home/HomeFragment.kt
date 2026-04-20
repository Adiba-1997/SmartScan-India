package com.azhar.smartscanindia.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.azhar.smartscanindia.R
import com.azhar.smartscanindia.databinding.FragmentHomeBinding
import com.google.android.gms.ads.AdRequest

class HomeFragment : Fragment(R.layout.fragment_home) {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        binding.cardAadhaar.setOnClickListener { findNavController().navigate(R.id.aadhaarFragment) }
        binding.cardPan.setOnClickListener { findNavController().navigate(R.id.panFragment) }
        binding.cardDocument.setOnClickListener { findNavController().navigate(R.id.documentScannerFragment) }
        binding.cardOcr.setOnClickListener { findNavController().navigate(R.id.ocrFragment) }
        binding.cardPdfTools.setOnClickListener { findNavController().navigate(R.id.pdfToolsFragment) }
        binding.cardSignature.setOnClickListener { findNavController().navigate(R.id.signatureFragment) }
        binding.adView.loadAd(AdRequest.Builder().build())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
