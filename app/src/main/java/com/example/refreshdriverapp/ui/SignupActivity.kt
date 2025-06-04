package com.example.refreshdriverapp.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.refreshdriverapp.databinding.ActivitySignupBinding
import com.example.refreshdriverapp.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        binding.apply {
            btnSignup.setOnClickListener {
                val email = editTextEmail.text.toString().trim()
                val password = editTextPassword.text.toString().trim()
                val confirmPassword = editTextConfirmPassword.text.toString().trim()

                if (validateInput(email, password, confirmPassword)) {
                    viewModel.register(email, password)
                }
            }

            textLogin.setOnClickListener {
                finish()
            }

            btnBack.setOnClickListener {
                finish()
            }
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.authState.collect { state ->
                binding.apply {
                    // 로딩 상태
                    progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                    btnSignup.isEnabled = !state.isLoading

                    // 에러 처리
                    state.error?.let { error ->
                        Toast.makeText(this@SignupActivity, error, Toast.LENGTH_LONG).show()
                        viewModel.clearError()
                    }

                    // 회원가입 성공
                    state.registerSuccess?.let { success ->
                        if (success) {
                            Toast.makeText(this@SignupActivity, "회원가입이 완료되었습니다. 로그인해주세요.", Toast.LENGTH_LONG).show()
                            finish()
                        }
                    }
                }
            }
        }
    }

    private fun validateInput(email: String, password: String, confirmPassword: String): Boolean {
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
                confirmPassword.isEmpty() -> {
                    editTextConfirmPassword.error = "비밀번호 확인을 입력해주세요"
                    editTextConfirmPassword.requestFocus()
                    return false
                }
                password != confirmPassword -> {
                    editTextConfirmPassword.error = "비밀번호가 일치하지 않습니다"
                    editTextConfirmPassword.requestFocus()
                    return false
                }
            }
        }
        return true
    }
}