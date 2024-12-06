package com.example.individualproj3

import android.content.pm.ActivityInfo
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import java.util.UUID
import android.media.MediaPlayer

data class Card(
    val id: Int,
    val value: String,
    var isRevealed: Boolean = false,
    var isMatched: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchingGameScreen(navController: NavController, levelNumber: Int) {
    // Lock screen to landscape
    val context = LocalContext.current
    var flipMediaPlayer: MediaPlayer? by remember { mutableStateOf(null) }
    var applauseMediaPlayer: MediaPlayer? by remember { mutableStateOf(null) }

    DisposableEffect(context) {
        val activity = context as? android.app.Activity
        val originalOrientation = activity?.requestedOrientation ?: ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        onDispose {
            activity?.requestedOrientation = originalOrientation
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            flipMediaPlayer?.release()
            applauseMediaPlayer?.release()
        }
    }

    var cards by remember { mutableStateOf(generateCards(levelNumber)) }
    var selectedCards by remember { mutableStateOf(listOf<Card>()) }
    var remainingAttempts by remember { mutableStateOf(5) }
    var showCompletionPopup by remember { mutableStateOf(false) }
    var showFailurePopup by remember { mutableStateOf(false) }

    fun playFlipSound() {
        flipMediaPlayer?.release()
        flipMediaPlayer = MediaPlayer.create(context, R.raw.flip)
        flipMediaPlayer?.start()
    }

    fun playApplauseSound() {
        applauseMediaPlayer?.release()
        applauseMediaPlayer = MediaPlayer.create(context, R.raw.applause)
        applauseMediaPlayer?.start()
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Level $levelNumber") },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate("matching_level_selection_screen")
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Remaining Attempts: $remainingAttempts",
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Card Grid
            val gridColumns = when(levelNumber) {
                1 -> 3
                2 -> 4
                3 -> 5
                else -> 3
            }

            GridLayout(
                items = cards,
                columns = gridColumns,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) { card ->
                CardItem(
                    card = card,
                    onClick = {
                        if (!card.isRevealed && !card.isMatched && selectedCards.size < 2) {
                            playFlipSound()
                            cards = cards.map {
                                if (it.id == card.id) it.copy(isRevealed = true)
                                else it
                            }
                            selectedCards = selectedCards + card
                        }
                    }
                )
            }
        }

        // Completion Popup
        if (showCompletionPopup) {
            LaunchedEffect(Unit) {
                playApplauseSound()
            }
            AlertDialog(
                onDismissRequest = { /* Prevents dismissing by clicking outside */ },
                title = { Text("Congratulations!", textAlign = TextAlign.Center) },
                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("You completed Level $levelNumber!", textAlign = TextAlign.Center)
                        Text("Remaining Attempts: $remainingAttempts", textAlign = TextAlign.Center)
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            applauseMediaPlayer?.stop()
                            navController.navigate("matching_level_selection_screen")
                            showCompletionPopup = false
                        }
                    ) {
                        Text("Return to Levels")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            applauseMediaPlayer?.stop()
                            cards = generateCards(levelNumber)
                            selectedCards = emptyList()
                            remainingAttempts = 5
                            showCompletionPopup = false
                        }
                    ) {
                        Text("Retry Level")
                    }
                }
            )
        }

        // Failure Popup
        if (showFailurePopup) {
            AlertDialog(
                onDismissRequest = { /* Prevents dismissing by clicking outside */ },
                title = { Text("Game Over", textAlign = TextAlign.Center) },
                text = { Text("You've run out of attempts.", textAlign = TextAlign.Center) },
                confirmButton = {
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                        onClick = {
                            navController.navigate("matching_level_selection_screen")
                            showFailurePopup = false
                        }
                    ) {
                        Text("Return to Levels")
                    }
                },
                dismissButton = {
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                        onClick = {
                            // Restart the same level
                            cards = generateCards(levelNumber)
                            selectedCards = emptyList()
                            remainingAttempts = 5
                            showFailurePopup = false
                        }
                    ) {
                        Text("Retry Level")
                    }
                }
            )
        }
    }

    // Game Logic
    LaunchedEffect(selectedCards) {
        if (selectedCards.size == 2) {
            if (selectedCards[0].value == selectedCards[1].value) {
                // Matched cards
                cards = cards.map { card ->
                    if (card.value == selectedCards[0].value) card.copy(isMatched = true)
                    else card
                }
                selectedCards = emptyList()

                // Check for level completion
                if (cards.all { it.isMatched }) {
                    showCompletionPopup = true
                }
            } else {
                // No match
                remainingAttempts--

                if (remainingAttempts <= 0) {
                    // Game Over
                    showFailurePopup = true
                } else {
                    delay(1000) // Show cards briefly
                    cards = cards.map {
                        if (!it.isMatched) it.copy(isRevealed = false)
                        else it
                    }
                    selectedCards = emptyList()
                }
            }
        }
    }
}

@Composable
fun CardItem(card: Card, onClick: () -> Unit) {
    val imageResource = if (card.isRevealed || card.isMatched) {
        getCardResourceId(card.value)
    } else {
        R.drawable.question_mark_card
    }

    Image(
        painter = painterResource(id = imageResource),
        contentDescription = "Card ${card.value}",
        modifier = Modifier
            .size(100.dp)
            .padding(4.dp)
            .clickable(enabled = !card.isRevealed && !card.isMatched, onClick = onClick)
    )
}

fun generateCards(levelNumber: Int): List<Card> {
    val cardValues = when(levelNumber) {
        1 -> listOf("2_of_clubs", "3_of_clubs", "4_of_clubs")
        2 -> listOf("2_of_clubs", "3_of_clubs", "4_of_clubs", "5_of_clubs")
        3 -> listOf("2_of_clubs", "3_of_clubs", "4_of_clubs", "5_of_clubs", "6_of_clubs")
        else -> listOf()
    }

    return (cardValues.flatMap { value ->
        listOf(
            Card(UUID.randomUUID().hashCode(), value),
            Card(UUID.randomUUID().hashCode(), value)
        )
    }).shuffled()
}

@Composable
fun <T> GridLayout(
    items: List<T>,
    columns: Int,
    modifier: Modifier = Modifier,
    content: @Composable (T) -> Unit
) {
    Column(modifier = modifier) {
        items.chunked(columns).forEach { rowItems ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                rowItems.forEach { item ->
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        content(item)
                    }
                }
            }
        }
    }
}

// Only using a few cards for simplicity
fun getCardResourceId(cardValue: String): Int {
    return when(cardValue) {
        "2_of_clubs" -> R.drawable.two_of_clubs
        "3_of_clubs" -> R.drawable.three_of_clubs
        "4_of_clubs" -> R.drawable.four_of_clubs
        "5_of_clubs" -> R.drawable.five_of_clubs
        "6_of_clubs" -> R.drawable.six_of_clubs
        else -> R.drawable.question_mark_card
    }
}
