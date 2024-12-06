package com.example.individualproj3

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import kotlin.random.Random
import android.content.Context
import android.media.MediaPlayer
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MathGameScreen(navController: NavController, levelNumber: Int) {
    val context = LocalContext.current
    var mediaPlayer: MediaPlayer? by remember { mutableStateOf(null) }
    var currentProblem by remember { mutableStateOf(1) }
    var correctAnswers by remember { mutableStateOf(0) }
    var num1 by remember { mutableStateOf(0) }
    var num2 by remember { mutableStateOf(0) }
    var operation by remember { mutableStateOf("") }
    var correctAnswer by remember { mutableStateOf(0) }
    var answerOptions by remember { mutableStateOf(listOf<Int>()) }
    var selectedAnswer by remember { mutableStateOf<Int?>(null) }
    var droppedAnswer by remember { mutableStateOf<Int?>(null) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    var showCompletionDialog by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
        }
    }
    fun playApplauseSound() {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(context, R.raw.applause)
        mediaPlayer?.start()
    }

    fun generateUniqueAnswers(correctAnswer: Int): List<Int> {
        val answers = mutableSetOf<Int>()
        answers.add(correctAnswer)
        val usedDifferences = mutableSetOf<Int>()
        while (answers.size < 3) {
            var difference: Int
            do {
                difference = Random.nextInt(-10, 11)
            } while (difference == 0 || usedDifferences.contains(difference))

            usedDifferences.add(difference)

            // Add new wrong answer
            val wrongAnswer = correctAnswer + difference
            if (wrongAnswer >= 0) {
                answers.add(wrongAnswer)
            }
        }

        return answers.toList().shuffled()
    }

    fun generateNewProblem() {
        // First clear all state
        selectedAnswer = null
        droppedAnswer = null
        offsetX = 0f
        offsetY = 0f
        isDragging = false
        answerOptions = emptyList()

        // Then generate new problem
        num1 = Random.nextInt(0, 100)
        num2 = Random.nextInt(0, 100)

        when (levelNumber) {
            1 -> {
                operation = "+"
                correctAnswer = num1 + num2
            }

            2 -> {
                operation = "-"
                correctAnswer = num1 - num2
            }

            3 -> {
                operation = if (Random.nextBoolean()) "+" else "-"
                correctAnswer = if (operation == "+") num1 + num2 else num1 - num2
            }
        }
        answerOptions = generateUniqueAnswers(correctAnswer)
    }

    LaunchedEffect(Unit) {
        generateNewProblem()
    }

// Completion Dialog
    if (showCompletionDialog) {
        LaunchedEffect(Unit) {
            playApplauseSound()
        }

        Dialog(onDismissRequest = { }) {
            Card(
                modifier = Modifier.padding(16.dp),
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "Level Complete!",
                        fontSize = 24.sp
                    )
                    Text(
                        "You got $correctAnswers out of 5 correct!",
                        fontSize = 18.sp
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            onClick = {
                                mediaPlayer?.stop()
                                currentProblem = 1
                                correctAnswers = 0
                                showCompletionDialog = false
                                generateNewProblem()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Blue
                            )
                        ) {
                            Text("Retry")
                        }
                        Button(
                            onClick = {
                                mediaPlayer?.stop()
                                navController.navigate("math_level_selection")
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Blue
                            )
                        ) {
                            Text("Back to Levels")
                        }
                    }
                }
            }
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Math Game") },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate("math_level_selection") {
                            popUpTo("levelSelect") { inclusive = true }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to level selection"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFFFFFF),
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                )
            )
        }
    ) { paddingValues ->
        // Main container
        Surface(
            color = Color(0xFFFFFFFF)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .wrapContentHeight(align = Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Level $levelNumber - Problem $currentProblem/5",
                    fontSize = 40.sp,
                    modifier = Modifier.padding(bottom = 64.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(bottom = 64.dp)
                ) {
                    Text(
                        "$num1 $operation $num2 = ",
                        fontSize = 30.sp
                    )

                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(Color.LightGray)
                    ) {
                        droppedAnswer?.let {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Blue),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = it.toString(),
                                    color = Color.White,
                                    fontSize = 24.sp
                                )
                            }
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(32.dp),
                    modifier = Modifier.padding(bottom = 64.dp)
                ) {
                    answerOptions.forEachIndexed { index, answer ->
                        key(answer) {
                            Box(
                                modifier = Modifier
                                    .offset {
                                        androidx.compose.ui.unit.IntOffset(
                                            if (isDragging && selectedAnswer == answer) offsetX.toInt() else 0,
                                            if (isDragging && selectedAnswer == answer) offsetY.toInt() else 0
                                        )
                                    }
                                    .size(80.dp)
                                    .background(Color.Blue)
                                    .pointerInput(key1 = answer) {
                                        detectDragGestures(
                                            onDragStart = {
                                                selectedAnswer = answer
                                                isDragging = true
                                                droppedAnswer = null
                                            },
                                            onDrag = { change, dragAmount ->
                                                offsetX += dragAmount.x
                                                offsetY += dragAmount.y
                                            },
                                            onDragEnd = {
                                                if (offsetY < -150) {
                                                    droppedAnswer = selectedAnswer
                                                }
                                                isDragging = false
                                                offsetX = 0f
                                                offsetY = 0f
                                            }
                                        )
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = answer.toString(),
                                    color = Color.White,
                                    fontSize = 24.sp
                                )
                            }
                        }
                    }
                }

                if (droppedAnswer != null) {
                    Button(
                        onClick = {
                            if (droppedAnswer != null) {
                                if (droppedAnswer == correctAnswer) {
                                    correctAnswers++
                                }

                                if (currentProblem == 5) {
                                    showCompletionDialog = true
                                } else {
                                    currentProblem++
                                    droppedAnswer = null
                                    generateNewProblem()
                                }
                            }
                        },
                        modifier = Modifier.padding(top = 32.dp),
                        enabled = droppedAnswer != null,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Blue,
                            disabledContainerColor = Color.Blue.copy(alpha = 0.5f)
                        )
                    ) {
                        Text(
                            "Confirm Answer",
                            fontSize = 20.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            color = if (droppedAnswer != null) Color.White else Color.White.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
        }
