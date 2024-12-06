package com.example.individualproj3

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

/**
 * Composable function that displays the level selection screen for the matching game
 * Allows users to choose between different difficulty levels or return to the main screen
 *
 * @param navController Navigation controller used to handle screen navigation
 */
@Composable
fun LevelSelectionScreen(navController: NavController) {
    // Set up the main surface with a white background
    Surface (
        color = Color(0xFFFFFFFF)
    ){
        // Main column container for all screen elements
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Screen title
            Text("Select a Level")

            // Add vertical spacing between title and first button
            Spacer(modifier = Modifier.height(16.dp))

            // Level 1 button
            Button(
                onClick = { navController.navigate("matching_screen/1") },
                modifier = Modifier.width(200.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
            ) {
                Text("Level 1")
            }

            // Spacing between buttons
            Spacer(modifier = Modifier.height(8.dp))

            // Level 2 button
            Button(
                onClick = { navController.navigate("matching_screen/2") },
                modifier = Modifier.width(200.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
            ) {
                Text("Level 2")
            }

            // Spacing between buttons
            Spacer(modifier = Modifier.height(8.dp))

            // Level 3 button
            Button(
                onClick = { navController.navigate("matching_screen/3") },
                modifier = Modifier.width(200.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
            ) {
                Text("Level 3")
            }

            // Spacing between level 3 and return button
            Spacer(modifier = Modifier.height(8.dp))

            // Return to main screen button
            Button(
                onClick = { navController.navigate("main_screen") },
                modifier = Modifier.width(200.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
            ) {
                Text("Return to main screen")
            }
        }
    }
}