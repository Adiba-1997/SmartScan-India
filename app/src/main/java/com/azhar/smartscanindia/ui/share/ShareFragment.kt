package com.azhar.smartscanindia.ui.share

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.azhar.smartscanindia.R
import com.azhar.smartscanindia.databinding.FragmentShareBinding

class ShareFragment : Fragment(R.layout.fragment_share) {
    private var _binding: FragmentShareBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentShareBinding.bind(view)

        binding.btnShareWhatsapp.setOnClickListener { Toast.makeText(requireContext(), "Sharing via WhatsApp", Toast.LENGTH_SHORT).show() }
        binding.btnShareEmail.setOnClickListener { Toast.makeText(requireContext(), "Sharing via Email", Toast.LENGTH_SHORT).show() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
