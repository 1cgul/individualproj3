package com.example.individualproj3

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun Navigation(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login_screen"){

        composable("login_screen"){
            LoginScreen(navController)
        }
        composable("register_screen"){
            RegisterScreen(navController)
        }
        composable("main_screen"){
            MainScreen(navController)
        }
        composable("matching_screen"){
            MatchingGameScreen(navController)
        }
        composable("math_screen"){
            MathGameScreen(navController)
        }

    }
}