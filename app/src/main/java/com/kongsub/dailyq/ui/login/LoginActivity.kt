package com.kongsub.dailyq.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.kongsub.dailyq.AuthManager
import com.kongsub.dailyq.R
import com.kongsub.dailyq.databinding.ActivityLoginBinding
import com.kongsub.dailyq.ui.base.BaseActivity
import com.kongsub.dailyq.ui.main.MainActivity
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginActivity : BaseActivity() {

    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.password.setOnEditorActionListener { _, actionId,
            event ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> {
                        login()
                        return@setOnEditorActionListener true
                    }
                    EditorInfo.IME_ACTION_UNSPECIFIED -> {
                        if(event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER){
                            login()
                            return@setOnEditorActionListener true
                        }
                    }
                }
                false
        }

        binding.login.setOnClickListener {
            login()
        }
    }

    fun login() {
        if(binding.progress.isVisible){
            return
        }
        val uid = binding.userId.text?.trim().toString()
        val password = binding.password.text?.trim().toString()

        if(validateUidAndPassword(uid, password)){
            binding.progress.isVisible = true

            lifecycleScope.launch {
                val authTokenResponse = api.login(uid, password)
                if(authTokenResponse.isSuccessful) {
                    val authToken = authTokenResponse.body()

                    AuthManager.uid = uid
                    AuthManager.accessToken = authToken?.accessToken
                    AuthManager.refreshToken = authToken?.refreshToken

                    // 토큰 가져오기
                    val messagingToken = FirebaseMessaging.getInstance().token.await()
                    // API 서버로 전달
                    api.registerPushToken(messagingToken)

                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                } else {
                    binding.progress.isVisible = false
                    Toast.makeText(
                        this@LoginActivity,
                        R.string.error_login_failed,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    fun validateUidAndPassword(uid: String, password: String): Boolean{
        binding.userIdLayout.error = null
        binding.passwordLayout.error = null

        if(uid.length < 5) {
            binding.userIdLayout.error = getString(R.string.error_uid_too_short)
            return false
        }
        if(password.length < 8) {
            binding.passwordLayout.error = getString(R.string.error_password_too_short)
            return false
        }
        val numberRegex = "[0-9]".toRegex()
        if(!numberRegex.containsMatchIn(password)){
            binding.passwordLayout.error = getString(R.string.error_password_must_contain_number)
            return false
        }
        return true
    }
}