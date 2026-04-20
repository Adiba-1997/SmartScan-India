package com.azhar.smartscanindia.ui.security

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.azhar.smartscanindia.R
import com.azhar.smartscanindia.biometric.BiometricHelper
import com.azhar.smartscanindia.databinding.FragmentSecurityBinding
import com.azhar.smartscanindia.utils.SecurityManager

class SecurityFragment : Fragment(R.layout.fragment_security) {
    private var _binding: FragmentSecurityBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSecurityBinding.bind(view)

        val securityManager = SecurityManager(requireContext())
        binding.switchAppLock.isChecked = securityManager.isAppLockEnabled()
        binding.switchAppLock.setOnCheckedChangeListener { _, checked -> securityManager.setAppLockEnabled(checked) }
        binding.btnSetPin.setOnClickListener {
            val pin = binding.etNewPin.text.toString().trim()
            if (pin.length == 4) {
                securityManager.savePin(pin)
                binding.etNewPin.text?.clear()
            }
        }
        binding.btnTestUnlock.setOnClickListener {
            startActivity(Intent(requireContext(), AppLockActivity::class.java))
        }
        binding.tvBiometricStatus.text = if (BiometricHelper.isBiometricAvailable(requireContext())) {
            getString(R.string.biometric_available)
        } else getString(R.string.biometric_not_available)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
