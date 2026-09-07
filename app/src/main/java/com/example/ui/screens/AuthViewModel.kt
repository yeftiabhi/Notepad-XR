package com.example.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppSettings
import com.example.data.User
import com.example.data.UserDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AuthViewModel(
    private val userDao: UserDao,
    private val appSettings: AppSettings
) : ViewModel() {

    val currentUserId = appSettings.currentUserId.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun login(username: String, pass: String) {
        viewModelScope.launch {
            if (username.isBlank() || pass.isBlank()) {
                _error.value = "Username and password cannot be empty"
                return@launch
            }
            val user = userDao.getUserByUsername(username)
            if (user == null) {
                _error.value = "Username not found. Please sign up."
            } else if (user.passwordHash != pass) {
                _error.value = "Incorrect password."
            } else {
                appSettings.setUserId(user.id)
                _error.value = null
            }
        }
    }

    fun signup(username: String, pass: String, repeatPass: String) {
        viewModelScope.launch {
            if (username.isBlank() || pass.isBlank()) {
                _error.value = "Fields cannot be empty"
                return@launch
            }
            if (pass.length < 8) {
                _error.value = "Password must be at least 8 characters"
                return@launch
            }
            if (pass != repeatPass) {
                _error.value = "Passwords do not match"
                return@launch
            }
            val existing = userDao.getUserByUsername(username)
            if (existing != null) {
                _error.value = "Username is already occupied. Try \${username}_1"
                return@launch
            }
            
            val newUser = User(username = username, passwordHash = pass)
            val id = userDao.insertUser(newUser)
            appSettings.setUserId(id.toInt())
            _error.value = null
        }
    }

    fun clearError() {
        _error.value = null
    }
}
