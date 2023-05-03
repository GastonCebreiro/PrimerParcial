package com.example.primerparcial.activities

import android.app.AlertDialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.example.primerparcial.R
import com.example.primerparcial.fragments.HomeFragment
import com.example.primerparcial.fragments.LoginFragment
import com.example.primerparcial.fragments.NewUserFragment

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    override fun onBackPressed() {
        val navHost =
            supportFragmentManager.findFragmentById(R.id.login_nav_host) as NavHostFragment
        navHost?.let { navFragment ->
            navFragment.childFragmentManager.primaryNavigationFragment?.let { fragment ->
                if (fragment is NewUserFragment)
                    super.onBackPressed()
            }
        }
    }
}