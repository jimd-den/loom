package com.example.presentation.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.presentation.viewmodel.LoomViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalInterviewScreen(
    viewModel: LoomViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToCanvas: () -> Unit
) {
    val currentGoal by viewModel.currentGoal.collectAsState()
    val questions by viewModel.onboardingQuestions.collectAsState()
    val currIndex by viewModel.currentQuestionIndex.collectAsState()
    val isAiLoading by viewModel.isAiLoading.collectAsState()
    val aiMessage by viewModel.aiStateMessage.collectAsState()

    var textReply by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = "MIYAMOTO SENSEI INTERVIEW",
                            fontWeight = FontWeight.Black,
                            fontStyle = FontStyle.Italic,
                            fontSize = 16.sp,
                            fontFamily = FontFamily.Monospace,
                            color = Color.White
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier
                                .padding(start = 8.dp, end = 4.dp)
                                .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                                .background(Color.White, RoundedCornerShape(8.dp))
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Cancel", tint = Color.Black)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = BrutalistOrangeRed
                    )
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .background(Color.Black)
                )
            }
        },
        containerColor = CreamBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(CreamBackground)
        ) {
            if (questions.isNotEmpty() && currIndex < questions.size) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Section progress indicators
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "STAGE • SYSTEM ALIGNMENT",
                                color = BrutalistDark,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Question ${currIndex + 1} of ${questions.size}",
                                color = BrutalistMuted,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Progress indicator bar (brutalist high-contrast)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .background(Color.White, RoundedCornerShape(6.dp))
                                .border(2.dp, Color.Black, RoundedCornerShape(6.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth((currIndex + 1).toFloat() / questions.size)
                                    .background(BrutalistYellow, RoundedCornerShape(6.dp))
                                    .border(2.dp, Color.Black, RoundedCornerShape(6.dp))
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Question cardboard
                        BrutalistCard(
                            backgroundColor = Color.White,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        Icons.Default.ChatBubbleOutline,
                                        contentDescription = "Miyamoto Proj",
                                        tint = BrutalistPurple,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = "MIYAMOTO ALIGNMENT CORE",
                                        color = BrutalistPurple,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace,
                                        letterSpacing = 1.sp
                                    )
                                }

                                Text(
                                    text = questions[currIndex],
                                    color = BrutalistDark,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = 24.sp,
                                    modifier = Modifier.testTag("onboarding_question_text")
                                )
                            }
                        }
                    }

                    // Input replier area
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = textReply,
                            onValueChange = { textReply = it },
                            placeholder = { Text("Describe your background, learning constraints, or exact intentions here...", color = BrutalistMuted) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = BrutalistDark,
                                unfocusedTextColor = BrutalistDark,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = Color.Black,
                                unfocusedBorderColor = Color.Black,
                                cursorColor = Color.Black
                            ),
                            shape = RoundedCornerShape(BrutalistRoundness),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 120.dp)
                                .border(2.dp, Color.Black, RoundedCornerShape(BrutalistRoundness))
                                .testTag("interview_reply_input")
                        )

                        BrutalistButton(
                            onClick = {
                                if (textReply.isNotBlank()) {
                                    val currentAns = textReply.trim()
                                    textReply = ""
                                    viewModel.submitAnswer(currentAns) {
                                        onNavigateToCanvas()
                                    }
                                }
                            },
                            enabled = textReply.isNotBlank() && !isAiLoading,
                            backgroundColor = BrutalistGreen,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("submit_reply_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = if (currIndex + 1 == questions.size) "SYNTHESIZE STUDY PATHWAY" else "NEXT QUESTION",
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color.White
                                )
                                Icon(Icons.Default.ArrowForward, contentDescription = "Next", tint = Color.White)
                            }
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = BrutalistOrangeRed)
                }
            }

            // Universal AI Loader HUD Overlay
            if (isAiLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.85f)),
                    contentAlignment = Alignment.Center
                ) {
                    BrutalistCard(
                        backgroundColor = Color.White,
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .padding(24.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            CircularProgressIndicator(color = BrutalistOrangeRed, strokeWidth = 5.dp)
                            Text(
                                text = aiMessage,
                                color = BrutalistDark,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}
