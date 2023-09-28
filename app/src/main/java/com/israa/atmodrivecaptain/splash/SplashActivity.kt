package com.israa.atmodrivecaptain.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.israa.atmodrivecaptain.R
import com.israa.atmodrivecaptain.auth.AuthActivity
import com.israa.atmodrivecaptain.home.HomeActivity
import com.israa.atmodrivecaptain.utils.MySharedPreference
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.myLooper()!!).postDelayed({
            if(MySharedPreference.getCaptain().mobile != null){
                startActivity(Intent(this,HomeActivity::class.java))
                finish()
            }
            else{
                startActivity(Intent(this,AuthActivity::class.java))
                finish()
            }
        },5000)


        }
}