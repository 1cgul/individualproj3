package com.example.individualproj3

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun Navigation() {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = if (sessionManager.isLoggedIn()) "main_screen" else "login_screen"
    ) {
        composable("login_screen") {
            LoginScreen(navController, sessionManager)
        }
        composable("register_screen"){
            RegisterScreen(navController)
        }
        composable("main_screen"){
            MainScreen(navController)
        }
        composable("matching_screen/1"){
            MatchingGameScreen(navController, 1)
        }
        composable("matching_screen/2"){
            MatchingGameScreen(navController, 2)
        }
        composable("matching_screen/3"){
            MatchingGameScreen(navController, 3)
        }

        composable("math_screen/1"){
            MathGameScreen(navController, 1)
        }
        composable("math_screen/2"){
            MathGameScreen(navController, 2)
        }
        composable("math_screen/3"){
            MathGameScreen(navController,3 )
        }

        composable("matching_level_selection_screen"){
            LevelSelectionScreen(navController)
        }
        composable("math_level_selection"){
            MathLevelSelection(navController)
        }

    }
}