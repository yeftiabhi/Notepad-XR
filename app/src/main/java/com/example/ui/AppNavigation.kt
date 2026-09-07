package com.example.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.AuthViewModel
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.HomeViewModel
import com.example.ui.screens.NoteEditScreen
import com.example.ui.screens.NoteEditViewModel
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SettingsViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    
    val authViewModel: AuthViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val userId by authViewModel.currentUserId.collectAsStateWithLifecycle()

    val startDestination = if (userId != null) "home" else "auth"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("auth") {
            AuthScreen(
                viewModel = authViewModel,
                onAuthSuccess = {
                    navController.navigate("home") {
                        popUpTo("auth") { inclusive = true }
                    }
                }
            )
        }
        composable("home") {
            val homeViewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
            HomeScreen(
                viewModel = homeViewModel,
                onNavigateToNote = { noteId ->
                    navController.navigate("note_edit/${noteId ?: "new"}")
                },
                onNavigateToSettings = {
                    navController.navigate("settings")
                }
            )
        }
        composable("note_edit/{noteId}") { backStackEntry ->
            val noteIdStr = backStackEntry.arguments?.getString("noteId")
            val noteId = if (noteIdStr == "new") null else noteIdStr?.toIntOrNull()
            
            val noteEditViewModel: NoteEditViewModel = viewModel(factory = AppViewModelProvider.Factory)
            NoteEditScreen(
                viewModel = noteEditViewModel,
                noteId = noteId,
                onBack = { navController.navigateUp() }
            )
        }
        composable("settings") {
            val settingsViewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)
            SettingsScreen(
                viewModel = settingsViewModel,
                onBack = { navController.navigateUp() },
                onLogout = {
                    navController.navigate("auth") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
    }
}
