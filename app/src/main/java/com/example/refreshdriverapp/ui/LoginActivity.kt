package com.example.refreshdriverapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.refreshdriverapp.databinding.ActivityLoginBinding
import com.example.refreshdriverapp.ui.viewmodel.AuthViewModel
import com.example.refreshdriverapp.utils.PreferencesHelper
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var preferencesHelper: PreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferencesHelper = PreferencesHelper(this)

        // 이미 로그인되어 있으면 메인으로 이동
        if (preferencesHelper.isLoggedIn) {
            startMainActivity()
            return
        }

        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        binding.apply {
            btnLogin.setOnClickListener {
                val email = editTextEmail.text.toString().trim()
                val password = editTextPassword.text.toString().trim()

                if (validateInput(email, password)) {
                    viewModel.login(email, password)
                }
            }

            textSignup.setOnClickListener {
                startActivity(Intent(this@LoginActivity, SignupActivity::class.java))
            }

            textForgotPassword.setOnClickListener {
                Toast.makeText(this@LoginActivity, "비밀번호 찾기 기능은 준비 중입니다", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.authState.collect { state ->
                binding.apply {
                    // 로딩 상태
                    progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                    btnLogin.isEnabled = !state.isLoading

                    // 에러 처리
                    state.error?.let { error ->
                        Toast.makeText(this@LoginActivity, error, Toast.LENGTH_LONG).show()
                        viewModel.clearError()
                    }

                    // 로그인 성공
                    state.loginResponse?.let { response ->
                        preferencesHelper.saveLoginData(
                            token = response.token,
                            email = response.user.email,
                            userId = response.user.id,
                            userRole = response.user.role
                        )

                        Toast.makeText(this@LoginActivity, "로그인 성공", Toast.LENGTH_SHORT).show()
                        startMainActivity()
                    }
                }
            }
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        binding.apply {
            when {
                email.isEmpty() -> {
                    editTextEmail.error = "이메일을 입력해주세요"
                    editTextEmail.requestFocus()
                    return false
                }
                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    editTextEmail.error = "올바른 이메일 형식을 입력해주세요"
                    editTextEmail.requestFocus()
                    return false
                }
                password.isEmpty() -> {
                    editTextPassword.error = "비밀번호를 입력해주세요"
                    editTextPassword.requestFocus()
                    return false
                }
                password.length < 6 -> {
                    editTextPassword.error = "비밀번호는 6자 이상이어야 합니다"
                    editTextPassword.requestFocus()
                    return false
                }
            }
        }
        return true
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}