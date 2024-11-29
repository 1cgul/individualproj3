package com.example.individualproj3

import android.content.pm.ActivityInfo
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import java.util.UUID

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
    DisposableEffect(context) {
        val activity = context as? android.app.Activity
        val originalOrientation = activity?.requestedOrientation ?: ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        onDispose {
            activity?.requestedOrientation = originalOrientation
        }
    }

    var cards by remember { mutableStateOf(generateCards(levelNumber)) }
    var selectedCards by remember { mutableStateOf(listOf<Card>()) }
    var remainingAttempts by remember { mutableStateOf(3) }

    // Back handler (currently does nothing)
    BackHandler {
        navController.navigate("level_selection_screen")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Level $levelNumber") },
                navigationIcon = {
                    IconButton(onClick = {
                        // No functionality added as per request
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
                "Remaining Attemps: $remainingAttempts",
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
                    // navController.navigate() navigate to celebration screen TODO
                }
            } else {
                // No match
                remainingAttempts--

                if (remainingAttempts <= 0) {
                    // Game Over
                    navController.navigate("level_selection_screen")
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