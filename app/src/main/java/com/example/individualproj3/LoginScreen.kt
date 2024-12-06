package com.example.individualproj3

// Import necessary Compose and Android components
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalContext

/**
 * LoginScreen composable function that handles user authentication
 *
 * @param navController Navigation controller for handling screen transitions
 * @param sessionManager Manages user login state persistence
 * @param modifier Optional modifier for customizing the layout
 */
@Composable
fun LoginScreen(
    navController: NavController,
    sessionManager: SessionManager,
    modifier: Modifier = Modifier
) {
    // Get local context for accessing stored credentials
    val context = LocalContext.current

    // State variables for form fields and validation
    var emailOrUsername by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    // Error state variables for form validation
    var emailOrUsernameError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }

    // Regular expressions for input validation
    val emailPattern = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$")
    val usernamePattern = Regex("^[a-zA-Z0-9_]{3,20}$")  // 3-20 chars, alphanumeric and underscore
    val passwordPattern = Regex("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$")  // Min 8 chars, 1 letter, 1 number

    // Main surface container with white background
    Surface(
        color = Color(0xFFFFFFFF)
    ) {
        // Main column for vertical layout
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App title
            Text(text = "Kid Games", fontSize = 32.sp, modifier = Modifier.padding(20.dp))

            // Screen title
            Text(
                text = "Login Screen",
                fontSize = 24.sp,
                modifier = Modifier.padding(20.dp)
            )

            // Email/Username input field
            TextField(
                value = emailOrUsername,
                onValueChange = {
                    emailOrUsername = it
                    emailOrUsernameError = !it.matches(emailPattern) && !it.matches(usernamePattern)
                },
                label = { Text("Enter your email/username...") },
                isError = emailOrUsernameError,
                supportingText = {
                    if (emailOrUsernameError) {
                        Text("Please enter a valid email or username (3-20 characters)")
                    }
                },
                modifier = Modifier
                    .width(400.dp)
                    .padding(20.dp)
            )

            // Password input field
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

            // Error message display
            if (showError) {
                Text(
                    text = "Invalid credentials. Please try again.",
                    color = Color.Red,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // Login button
            Button(
                onClick = {
                    if (!emailOrUsernameError && !passwordError &&
                        emailOrUsername.isNotEmpty() && password.isNotEmpty()) {
                        // Verify credentials against stored user data
                        val storedCredentials = readUserCredentials(context)
                        if (storedCredentials != null &&
                            (emailOrUsername == storedCredentials.username ||
                                    emailOrUsername == storedCredentials.email) &&
                            password == storedCredentials.password) {
                            showError = false
                            sessionManager.saveLoginState(true)
                            // Navigate to main screen and remove login screen from back stack
                            navController.navigate("main_screen") {
                                popUpTo("login_screen") { inclusive = true }
                            }
                        } else {
                            showError = true
                        }
                    }
                },
                modifier = Modifier.width(150.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                enabled = !emailOrUsernameError && !passwordError &&
                        emailOrUsername.isNotEmpty() && password.isNotEmpty()
            ) {
                Text("Login", color = Color.White)
            }

            // Registration link row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(text = "Don't have an account?", modifier = Modifier.padding(end = 4.dp))

                Text(
                    text = "Register",
                    color = Color.Blue,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .clickable(onClick = { navController.navigate("register_screen") })
                        .padding(start = 4.dp),
                )
            }
        }
    }
}