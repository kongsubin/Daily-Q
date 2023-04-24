package com.kongsub.dailyq.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.kongsub.dailyq.AuthManager
import com.kongsub.dailyq.R
import com.kongsub.dailyq.ui.base.BaseActivity
import com.kongsub.dailyq.ui.login.LoginActivity
import com.kongsub.dailyq.ui.main.MainActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        lifecycleScope.launch {
            delay(1000)

            if(AuthManager.accessToken.isNullOrEmpty()) {
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            } else {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            }

            finish()
        }
    }
}