package com.azhar.smartscanindia

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.azhar.smartscanindia.databinding.ActivityMainBinding
import com.azhar.smartscanindia.ui.security.AppLockActivity
import com.azhar.smartscanindia.utils.SecurityManager

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHost.navController
        binding.bottomNav.setupWithNavController(navController)
    }

    override fun onResume() {
        super.onResume()
        val manager = SecurityManager(this)
        if (manager.isAppLockEnabled()) {
            startActivity(Intent(this, AppLockActivity::class.java))
        }
    }
}
