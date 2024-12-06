package com.example.individualproj3

// Import necessary Android and Compose components
import android.content.Context
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
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Data class representing a card in the matching game
 *
 * @param id Unique identifier for the card
 * @param value String value representing the card's face value
 * @param isRevealed Whether the card is currently face-up
 * @param isMatched Whether the card has been matched with its pair
 */
data class Card(
    val id: Int,
    val value: String,
    var isRevealed: Boolean = false,
    var isMatched: Boolean = false
)

/**
 * Main game screen composable for the matching card game
 *
 * @param navController Navigation controller for screen transitions
 * @param levelNumber Current level being played (1-3)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchingGameScreen(navController: NavController, levelNumber: Int) {
    // Context and media player initialization
    val context = LocalContext.current
    var flipMediaPlayer: MediaPlayer? by remember { mutableStateOf(null) }
    var applauseMediaPlayer: MediaPlayer? by remember { mutableStateOf(null) }

    // Force landscape orientation for the game
    DisposableEffect(context) {
        val activity = context as? android.app.Activity
        val originalOrientation = activity?.requestedOrientation ?: ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        onDispose {
            activity?.requestedOrientation = originalOrientation
        }
    }

    // Clean up media players when screen is disposed
    DisposableEffect(Unit) {
        onDispose {
            flipMediaPlayer?.release()
            applauseMediaPlayer?.release()
        }
    }

    // Game state variables
    var cards by remember { mutableStateOf(generateCards(levelNumber)) }
    var selectedCards by remember { mutableStateOf(listOf<Card>()) }
    var remainingAttempts by remember { mutableStateOf(5) }
    var showCompletionPopup by remember { mutableStateOf(false) }
    var showFailurePopup by remember { mutableStateOf(false) }

    // Sound effect functions
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

    // Main game UI scaffold
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
        // Main game content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Attempts counter
            Text(
                "Remaining Attempts: $remainingAttempts",
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Dynamic grid setup based on level
            val gridColumns = when(levelNumber) {
                1 -> 3  // 6 cards (3 pairs)
                2 -> 4  // 8 cards (4 pairs)
                3 -> 5  // 10 cards (5 pairs)
                else -> 3
            }

            // Card grid display
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

        // Victory popup dialog
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

        // Game over popup dialog
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

    /**
     * Logs the game score to a file
     *
     * @param context Application context for file access
     * @param level Current level number
     * @param remainingAttempts Number of attempts remaining at completion
     */
    fun logScore(context: Context, level: Int, remainingAttempts: Int) {
        try {
            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            val logEntry = "Matching Game - Level: $level, Remaining Attempts: $remainingAttempts, Completed: $timestamp\n"

            val file = File(context.filesDir, "game_scores.txt")
            file.appendText(logEntry)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Core game logic
    LaunchedEffect(selectedCards) {
        if (selectedCards.size == 2) {
            if (selectedCards[0].value == selectedCards[1].value) {
                // Handle matching cards
                cards = cards.map { card ->
                    if (card.value == selectedCards[0].value) card.copy(isMatched = true)
                    else card
                }
                selectedCards = emptyList()

                // Check for level completion
                if (cards.all { it.isMatched }) {
                    logScore(context, levelNumber, remainingAttempts)
                    showCompletionPopup = true
                }
            } else {
                // Handle non-matching cards
                remainingAttempts--

                if (remainingAttempts <= 0) {
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

/**
 * Composable for individual card display
 *
 * @param card Card data to display
 * @param onClick Callback for card click events
 */
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

/**
 * Generates a list of card pairs for the current level
 *
 * @param levelNumber Current level number (1-3)
 * @return Shuffled list of card pairs
 */
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

/**
 * Reusable grid layout composable
 *
 * @param items List of items to display in grid
 * @param columns Number of columns in grid
 * @param modifier Optional modifier for the layout
 * @param content Composable content for each grid item
 */
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

/**
 * Maps card value strings to corresponding drawable resources
 *
 * @param cardValue String value of the card
 * @return Resource ID for the card image
 */
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