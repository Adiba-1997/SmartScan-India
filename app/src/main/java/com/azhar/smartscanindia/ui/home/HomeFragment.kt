package com.azhar.smartscanindia.ui.home

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.azhar.smartscanindia.R
import com.azhar.smartscanindia.ads.AdManager
import com.azhar.smartscanindia.databinding.FragmentHomeBinding
import com.google.android.gms.ads.AdRequest

class HomeFragment : Fragment(R.layout.fragment_home) {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        val reveal = AnimationUtils.loadAnimation(requireContext(), R.anim.home_card_in)
        listOf(binding.cardAadhaar, binding.cardPan, binding.cardDocument, binding.cardOcr, binding.cardPdfTools, binding.cardSignature)
            .forEach { it.startAnimation(reveal) }

        binding.cardAadhaar.setOnClickListener { navigateWithAd(R.id.aadhaarFragment) }
        binding.cardPan.setOnClickListener { navigateWithAd(R.id.panFragment) }
        binding.cardDocument.setOnClickListener { navigateWithAd(R.id.documentScannerFragment) }
        binding.cardOcr.setOnClickListener { navigateWithAd(R.id.ocrFragment) }
        binding.cardPdfTools.setOnClickListener { navigateWithAd(R.id.pdfToolsFragment) }
        binding.cardSignature.setOnClickListener { navigateWithAd(R.id.signatureFragment) }

        binding.adView.loadAd(AdRequest.Builder().build())
    }

    private fun navigateWithAd(destination: Int) {
        AdManager.maybeShowInterstitial(requireActivity())
        findNavController().navigate(destination)
    }


    override fun onResume() {
        super.onResume()
        binding.adView.resume()
    }

    override fun onPause() {
        binding.adView.pause()
        super.onPause()
    }

    override fun onDestroyView() {
        binding.adView.destroy()
        super.onDestroyView()
        _binding = null
    }
}
