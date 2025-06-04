package com.example.refreshdriverapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.refreshdriverapp.data.api.LoginResponse
import com.example.refreshdriverapp.data.repository.RefreshRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = RefreshRepository()

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null)

            repository.login(email, password)
                .onSuccess { response ->
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        loginResponse = response,
                        error = null
                    )
                }
                .onFailure { error ->
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        error = "로그인 실패: ${error.message}"
                    )
                }
        }
    }

    fun register(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null)

            repository.register(email, password)
                .onSuccess { response ->
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        registerSuccess = true,
                        error = null
                    )
                }
                .onFailure { error ->
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        error = "회원가입 실패: ${error.message}"
                    )
                }
        }
    }

    fun clearError() {
        _authState.value = _authState.value.copy(error = null)
    }

    fun clearLoginResponse() {
        _authState.value = _authState.value.copy(loginResponse = null)
    }

    fun clearRegisterSuccess() {
        _authState.value = _authState.value.copy(registerSuccess = null)
    }
}

data class AuthState(
    val isLoading: Boolean = false,
    val loginResponse: LoginResponse? = null,
    val registerSuccess: Boolean? = null,
    val error: String? = null
)