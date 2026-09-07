package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.data.AppDatabase
import com.example.data.AppSettings
import com.example.data.DatabaseProvider
import com.example.ui.screens.AuthViewModel
import com.example.ui.screens.HomeViewModel
import com.example.ui.screens.NoteEditViewModel
import com.example.ui.screens.SettingsViewModel

object AppViewModelProvider {
    val Factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            val application = checkNotNull(extras[APPLICATION_KEY])
            val db = DatabaseProvider.getDatabase(application)
            val settings = AppSettings(application)

            return when {
                modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                    AuthViewModel(db.userDao(), settings) as T
                }
                modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                    HomeViewModel(db.noteDao(), settings) as T
                }
                modelClass.isAssignableFrom(NoteEditViewModel::class.java) -> {
                    NoteEditViewModel(db.noteDao(), settings) as T
                }
                modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                    SettingsViewModel(settings, db.userDao()) as T
                }
                else -> throw IllegalArgumentException("Unknown ViewModel class: \${modelClass.name}")
            }
        }
    }
}
