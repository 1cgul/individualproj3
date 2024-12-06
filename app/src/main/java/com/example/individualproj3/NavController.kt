package com.example.individualproj3

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

/**
 * Main navigation composable that handles routing throughout the application
 * Implements persistent login state and defines all available screen routes
 */
@Composable
fun Navigation() {
    // Initialize context and session management
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val navController = rememberNavController()

    // Set up navigation host with conditional start destination based on login state
    NavHost(
        navController = navController,
        startDestination = if (sessionManager.isLoggedIn()) "main_screen" else "login_screen"
    ) {
        // Authentication screens
        composable("login_screen") {
            LoginScreen(navController, sessionManager)
        }
        composable("register_screen") {
            RegisterScreen(navController)
        }

        // Main application screen
        composable("main_screen") {
            MainScreen(navController)
        }

        // Matching game screens - Three difficulty levels
        composable("matching_screen/1") {
            MatchingGameScreen(navController, 1)  // Easy difficulty
        }
        composable("matching_screen/2") {
            MatchingGameScreen(navController, 2)  // Medium difficulty
        }
        composable("matching_screen/3") {
            MatchingGameScreen(navController, 3)  // Hard difficulty
        }

        // Math game screens - Three difficulty levels
        composable("math_screen/1") {
            MathGameScreen(navController, 1)  // Addition only
        }
        composable("math_screen/2") {
            MathGameScreen(navController, 2)  // Subtraction only
        }
        composable("math_screen/3") {
            MathGameScreen(navController, 3)  // Mixed operations
        }

        // Level selection screens for both games
        composable("matching_level_selection_screen") {
            LevelSelectionScreen(navController)  // Matching game level selection
        }
        composable("math_level_selection") {
            MathLevelSelection(navController)    // Math game level selection
        }
    }
}