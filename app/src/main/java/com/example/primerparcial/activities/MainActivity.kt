package com.example.primerparcial.activities

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.primerparcial.R
import com.example.primerparcial.fragments.HomeFragment
import com.example.primerparcial.fragments.UserFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavView : BottomNavigationView
    private lateinit var navHostFragment : NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
        bottomNavView = findViewById(R.id.bottom_bar)
        NavigationUI.setupWithNavController(bottomNavView, navHostFragment.navController)
    }


    override fun onBackPressed() {
        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
        navHost?.let { navFragment ->
            navFragment.childFragmentManager.primaryNavigationFragment?.let {fragment->
                when(fragment){
                    is HomeFragment-> {
                        val builder = AlertDialog.Builder(this)
                        builder.setTitle(getString(R.string.activity_main_logout_title))
                        builder.setMessage(getString(R.string.activity_main_logout_message))
                        builder.setPositiveButton(getString(R.string.activity_main_logout_yes)) { _, _ ->
                            super.onBackPressed()
                        }
                        builder.setNegativeButton(getString(R.string.activity_main_logout_no)) { _, _ -> }
                        builder.create().show()
                    }
                    else -> {
                        super.onBackPressed()
                    }
                }
            }
        }
    }

}