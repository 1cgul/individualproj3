package com.example.individualproj3

import android.content.Context
import android.view.Surface
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Surface
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.io.File

data class UserCredentials(
    val username: String,
    val email: String,
    val password: String,
    val parentPin: String
)

fun saveUserCredentials(context: Context, credentials: UserCredentials) {
    try {
        val file = File(context.filesDir, "user_credentials.txt")
        file.writeText("${credentials.username}\n${credentials.email}\n${credentials.password}\n${credentials.parentPin}")
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun readUserCredentials(context: Context): UserCredentials? {
    return try {
        val file = File(context.filesDir, "user_credentials.txt")
        if (file.exists()) {
            val lines = file.readLines()
            if (lines.size >= 4) {
                UserCredentials(
                    username = lines[0],
                    email = lines[1],
                    password = lines[2],
                    parentPin = lines[3]
                )
            } else null
        } else null
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@Composable
fun RegisterScreen(navController: NavController, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var parentPin by remember { mutableStateOf("") }

    // Error states
    var usernameError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var pinError by remember { mutableStateOf(false) }

    // Regex patterns
    val usernamePattern = Regex("^[a-zA-Z0-9_]{3,20}$") // 3-20 characters, alphanumeric and underscore
    val emailPattern = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$")
    val passwordPattern = Regex("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$") // At least 8 chars, 1 letter and 1 number
    val pinPattern = Regex("^\\d{4,6}$") // 4-6 digit pin

    Surface(
        color = Color(0xFFFFFFFF)
    ){
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Kid Games", fontSize = 32.sp, modifier = Modifier.padding(20.dp))

            Text(
                text = "Register Screen",
                fontSize = 24.sp,
                modifier = Modifier.padding(20.dp)
            )

            TextField(
                value = username,
                onValueChange = {
                    username = it
                    usernameError = !it.matches(usernamePattern)
                },
                label = { Text("Enter your username...") },
                isError = usernameError,
                supportingText = {
                    if (usernameError) {
                        Text("Username must be 3-20 characters long and contain only letters, numbers, or underscore")
                    }
                },
                modifier = Modifier
                    .width(400.dp)
                    .padding(20.dp)
            )

            TextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = !it.matches(emailPattern)
                },
                label = { Text("Enter parent/guardian email...") },
                isError = emailError,
                supportingText = {
                    if (emailError) {
                        Text("Please enter a valid email address")
                    }
                },
                modifier = Modifier
                    .width(400.dp)
                    .padding(20.dp)
            )

            TextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = !it.matches(passwordPattern)
                },
                visualTransformation = PasswordVisualTransformation(),
                label = { Text("Enter your password...") },
                isError = passwordError,
                supportingText = {
                    if (passwordError) {
                        Text("Password must be at least 8 characters with at least 1 letter and 1 number")
                    }
                },
                modifier = Modifier
                    .width(400.dp)
                    .padding(20.dp)
            )

            TextField(
                value = parentPin,
                onValueChange = {
                    if (it.length <= 6) {
                        parentPin = it
                        pinError = !it.matches(pinPattern)
                    }
                },
                visualTransformation = PasswordVisualTransformation(),
                label = { Text("Enter parent PIN (4-6 digits)...") },
                isError = pinError,
                supportingText = {
                    if (pinError) {
                        Text("PIN must be between 4-6 digits")
                    }
                },
                modifier = Modifier
                    .width(400.dp)
                    .padding(20.dp)
            )
            Button(
                onClick = {
                    if (!usernameError && !emailError && !passwordError && !pinError &&
                        username.isNotEmpty() && email.isNotEmpty() &&
                        password.isNotEmpty() && parentPin.isNotEmpty()) {

                        // Save credentials when creating account
                        val credentials = UserCredentials(
                            username = username,
                            email = email,
                            password = password,
                            parentPin = parentPin
                        )
                        saveUserCredentials(context, credentials)
                        navController.navigate("login_screen")
                    }
                },
                modifier = Modifier
                    .width(150.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                enabled = !usernameError && !emailError && !passwordError && !pinError &&
                        username.isNotEmpty() && email.isNotEmpty() &&
                        password.isNotEmpty() && parentPin.isNotEmpty()
            ) {
                Text("Create Account", color = Color.White)
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(text = "Already have an account?", modifier = Modifier.padding(end = 4.dp))

                Text(
                    text = "Login",
                    color = Color.Blue,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .clickable(onClick = { navController.navigate("login_screen") })
                        .padding(start = 4.dp),
                )
            }
        }
    }
}