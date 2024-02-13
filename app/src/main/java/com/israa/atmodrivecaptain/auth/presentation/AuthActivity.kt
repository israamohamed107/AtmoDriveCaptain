package com.israa.atmodrivecaptain.auth.presentation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.israa.atmodrivecaptain.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.auth_main)

//        val navHostFragment = supportFragmentManager.findFragmentById(R.id.auth_nav_host_fragment) as NavHostFragment
//        navController = navHostFragment.findNavController()
//        setSupportActionBar(findViewById(R.id.toolbar))
//        setupActionBarWithNavController(navController)

    }

//    override fun onSupportNavigateUp(): Boolean {
//        return navController.navigateUp()
//                || super.onSupportNavigateUp()
//    }
//


    }