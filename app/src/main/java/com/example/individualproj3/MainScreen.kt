package com.example.individualproj3

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.window.Dialog
import java.io.File

/**
 * MainScreen composable that serves as the primary interface after login
 * Contains game selection, parent section, and logout functionality
 *
 * @param navController Navigation controller for handling screen transitions
 */
@Composable
fun MainScreen(navController: NavController) {
    // State variables for dialogs and PIN handling
    var showPinDialog by remember { mutableStateOf(false) }
    var showScores by remember { mutableStateOf(false) }
    var parentPin by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Context and session management
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    // Retrieve stored PIN from credentials file
    val storedPin = remember {
        try {
            val file = File(context.filesDir, "user_credentials.txt")
            if (file.exists()) {
                val lines = file.readLines()
                lines.find { it.startsWith("pin:") }?.substring(4) ?: ""
            } else ""
        } catch (e: Exception) {
            ""
        }
    }

    // Main surface container
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFFFFFFF)
    ) {
        // Main content column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            // App title display
            Text(
                text = "Kid Games",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Logout button
            Button(
                onClick = {
                    sessionManager.clearLoginState()
                    navController.navigate("login_screen") {
                        popUpTo(0) { inclusive = true }  // Clear navigation back stack
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text("Logout", color = Color.White)
            }

            // Parent section access button
            Button(
                onClick = { showPinDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Text("Parent Section", color = Color.White)
            }

            // Game selection prompt
            Text(
                text = "Select a Game",
                fontSize = 24.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Game selection options
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Matching game icon and navigation
                GameIcon(
                    iconResourceId = R.drawable.matchinggame,
                    gameName = "Matching Game",
                    onClick = { navController.navigate("matching_level_selection_screen") }
                )

                // Math game icon and navigation
                GameIcon(
                    iconResourceId = R.drawable.mathgame,
                    gameName = "Math Game",
                    onClick = { navController.navigate("math_screen/1") }
                )
            }
        }

        // Parent PIN verification dialog
        if (showPinDialog) {
            Dialog(onDismissRequest = {
                // Reset PIN dialog state on dismiss
                showPinDialog = false
                parentPin = ""
                pinError = false
                errorMessage = ""
            }) {
                Card(
                    modifier = Modifier.padding(16.dp),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text("Enter Parent PIN", fontSize = 20.sp)

                        // PIN input field with validation
                        TextField(
                            value = parentPin,
                            onValueChange = { newPin ->
                                // Validate input: digits only, max length 6
                                if (newPin.length <= 6 && newPin.all { it.isDigit() }) {
                                    parentPin = newPin
                                    pinError = false
                                }
                            },
                            label = { Text("PIN (4-6 digits)") },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            isError = pinError,
                            supportingText = {
                                if (pinError) {
                                    Text(errorMessage, color = Color.Red)
                                }
                            }
                        )

                        // PIN verification button
                        Button(
                            onClick = {
                                when {
                                    parentPin.length < 4 -> {
                                        pinError = true
                                        errorMessage = "PIN must be at least 4 digits"
                                    }
                                    parentPin != storedPin -> {
                                        pinError = true
                                        errorMessage = "Incorrect PIN"
                                    }
                                    else -> {
                                        // Success: show scores and reset dialog
                                        showPinDialog = false
                                        showScores = true
                                        parentPin = ""
                                        pinError = false
                                        errorMessage = ""
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
                        ) {
                            Text("Submit")
                        }
                    }
                }
            }
        }

        // Game scores display dialog
        if (showScores) {
            Dialog(onDismissRequest = { showScores = false }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "Game Scores",
                            fontSize = 24.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Load and display scores from file
                        val scores = try {
                            val file = File(context.filesDir, "game_scores.txt")
                            if (file.exists()) file.readText() else "No scores recorded yet"
                        } catch (e: Exception) {
                            "Error reading scores: ${e.message}"
                        }

                        Text(
                            text = scores,
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 8.dp)
                        )

                        // Close button for scores dialog
                        Button(
                            onClick = { showScores = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Close")
                        }
                    }
                }
            }
        }
    }
}

/**
 * Reusable game icon composable for displaying game options
 *
 * @param iconResourceId Resource ID for the game icon
 * @param gameName Name of the game to display
 * @param onClick Callback function when the icon is clicked
 */
@Composable
fun GameIcon(
    iconResourceId: Int,
    gameName: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Image(
            painter = painterResource(id = iconResourceId),
            contentDescription = gameName,
            modifier = Modifier
                .size(120.dp)
                .padding(bottom = 8.dp)
        )
        Text(
            text = gameName,
            fontSize = 16.sp
        )
    }
}