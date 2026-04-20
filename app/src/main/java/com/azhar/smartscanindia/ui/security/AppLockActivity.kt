package com.azhar.smartscanindia.ui.security

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.azhar.smartscanindia.databinding.ActivityAppLockBinding
import com.azhar.smartscanindia.utils.SecurityManager

class AppLockActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAppLockBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppLockBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val manager = SecurityManager(this)
        binding.btnUnlock.setOnClickListener {
            val pin = binding.etPin.text.toString().trim()
            if (manager.verifyPin(pin)) {
                Toast.makeText(this, "Unlocked", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Invalid PIN", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
