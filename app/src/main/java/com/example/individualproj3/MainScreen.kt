package com.example.individualproj3

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.window.Dialog
import java.io.File

@Composable
fun MainScreen(navController: NavController) {
    var showPinDialog by remember { mutableStateOf(false) }
    var showScores by remember { mutableStateOf(false) }
    var parentPin by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

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

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFFFFFFF)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            // App Title
            Text(
                text = "Kid Games",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Logout Button (New)
            Button(
                onClick = {
                    sessionManager.clearLoginState()
                    navController.navigate("login_screen") {
                        popUpTo(0) { inclusive = true }  // Clear the back stack
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text("Logout", color = Color.White)
            }

            // Parent Section Button
            Button(
                onClick = { showPinDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Text("Parent Section", color = Color.White)
            }

            // Prompt to Select a Game
            Text(
                text = "Select a Game",
                fontSize = 24.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Game Selection Row
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Matching Game
                GameIcon(
                    iconResourceId = R.drawable.matchinggame,
                    gameName = "Matching Game",
                    onClick = { navController.navigate("matching_level_selection_screen") }
                )

                // Math Game
                GameIcon(
                    iconResourceId = R.drawable.mathgame,
                    gameName = "Math Game",
                    onClick = { navController.navigate("math_screen/1") }
                )
            }
        }

        // PIN Dialog
        if (showPinDialog) {
            Dialog(onDismissRequest = {
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

                        TextField(
                            value = parentPin,
                            onValueChange = { newPin ->
                                // Only allow digits and limit length to 6
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

        // Scores Dialog
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

                        // Read and display scores
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