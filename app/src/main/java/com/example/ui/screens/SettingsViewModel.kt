package com.example.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppSettings
import com.example.data.UserDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val appSettings: AppSettings,
    private val userDao: UserDao
) : ViewModel() {

    val isDarkMode = appSettings.isDarkMode.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentUser = appSettings.currentUserId.flatMapLatest { id ->
        if (id != null) {
            userDao.getUserFlow(id)
        } else {
            flowOf(null)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun toggleDarkMode(isDark: Boolean) {
        viewModelScope.launch {
            appSettings.setDarkMode(isDark)
        }
    }

    fun logout() {
        viewModelScope.launch {
            appSettings.setUserId(null)
        }
    }

    fun updateProfilePic(url: String) {
        viewModelScope.launch {
            val user = currentUser.value
            if (user != null) {
                userDao.updateUser(user.copy(profilePic = url))
            }
        }
    }
}
