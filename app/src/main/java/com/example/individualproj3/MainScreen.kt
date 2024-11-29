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

@Composable
fun MainScreen(navController: NavController) {
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
                    onClick = { /* TODO: Add navigation to Matching Game */ }
                )

                // Math Game
                GameIcon(
                    iconResourceId = R.drawable.mathgame,
                    gameName = "Math Game",
                    onClick = { /* TODO: Add navigation to Math Game */ }
                )
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