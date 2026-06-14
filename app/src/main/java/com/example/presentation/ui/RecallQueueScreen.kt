package com.example.presentation.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.domain.model.RecallQuestion
import com.example.domain.model.RecallStatus
import com.example.presentation.viewmodel.LoomViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecallQueueScreen(
    viewModel: LoomViewModel,
    onNavigateBack: () -> Unit
) {
    val recallList by viewModel.recallQueue.collectAsState()
    val isAiLoading by viewModel.isAiLoading.collectAsState()
    val aiMessage by viewModel.aiStateMessage.collectAsState()
    val rawFeedback by viewModel.recallFeedback.collectAsState()

    var activeIndex by remember { mutableStateOf(0) }
    var typedAnswer by remember { mutableStateOf("") }

    // Safe index bounds
    val currentQuestion: RecallQuestion? = if (recallList.isNotEmpty() && activeIndex < recallList.size) {
        recallList[activeIndex]
    } else null

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = "ACTIVE RECALL CHANNELS",
                            fontWeight = FontWeight.Black,
                            fontStyle = FontStyle.Italic,
                            fontSize = 15.sp,
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
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
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
            if (recallList.isEmpty()) {
                // Queue complete/empty state styled in beautiful brutalist format
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    BrutalistCard(
                        backgroundColor = Color.White,
                        modifier = Modifier.fillMaxWidthInBrutalist()
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .background(BrutalistYellow, RoundedCornerShape(12.dp))
                                    .border(2.5.dp, Color.Black, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.School, contentDescription = "Completed", tint = Color.Black, modifier = Modifier.size(36.dp))
                            }

                            Text(
                                text = "ACTIVE SYNAPSES SECURED",
                                color = BrutalistDark,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = "Every study node is resolved or fully integrated. Revisit later to satisfy scheduled spaced recall queues.",
                                color = BrutalistMuted,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                lineHeight = 18.sp
                            )

                            BrutalistButton(
                                onClick = onNavigateBack,
                                backgroundColor = BrutalistCyan,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("GO TO WORKSPACE", fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, color = Color.Black)
                            }
                        }
                    }
                }
            } else if (currentQuestion != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Question Card Header & Progress
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ACTIVE RETRIEVAL TARGET",
                                color = BrutalistOrangeRed,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Probe ${activeIndex + 1} of ${recallList.size}",
                                color = BrutalistMuted,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Chunky progress meter
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
                                    .fillMaxWidth((activeIndex + 1).toFloat() / recallList.size)
                                    .background(BrutalistOrangeRed, RoundedCornerShape(6.dp))
                                    .border(2.dp, Color.Black, RoundedCornerShape(6.dp))
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

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
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.HourglassEmpty,
                                        contentDescription = "Verify",
                                        tint = BrutalistPurple,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "RETRIEVAL INQUIRY",
                                        color = BrutalistPurple,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace,
                                        letterSpacing = 1.sp
                                    )
                                }

                                Text(
                                    text = currentQuestion.questionText,
                                    color = BrutalistDark,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = 24.sp,
                                    modifier = Modifier.testTag("recall_question_text")
                                )
                            }
                        }
                    }

                    // Content Feedback Pane or Input Zone
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (rawFeedback == null) {
                            // Typing prompt input
                            OutlinedTextField(
                                value = typedAnswer,
                                onValueChange = { typedAnswer = it },
                                placeholder = { Text("Paraphrase the answer from memory. Miyamoto parses key intent and keywords...", color = BrutalistMuted) },
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
                                    .heightIn(min = 125.dp)
                                    .border(2.dp, Color.Black, RoundedCornerShape(BrutalistRoundness))
                                    .testTag("recall_answer_input")
                            )

                            BrutalistButton(
                                onClick = {
                                    if (typedAnswer.isNotBlank()) {
                                        viewModel.submitRecallAnswer(currentQuestion, typedAnswer.trim())
                                    }
                                },
                                enabled = typedAnswer.isNotBlank() && !isAiLoading,
                                backgroundColor = BrutalistGreen,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("submit_recall_answer_button")
                            ) {
                                Text("VERIFY RETRIEVAL ACCURACY", fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, color = Color.White)
                            }
                        } else {
                            // Feedback Display
                            val (status, text) = rawFeedback!!
                            val statusColor = if (status == RecallStatus.MASTERED) BrutalistGreen else BrutalistOrangeRed

                            BrutalistCard(
                                backgroundColor = Color.White,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(12.dp)
                                                .background(statusColor, RoundedCornerShape(3.dp))
                                                .border(1.dp, Color.Black, RoundedCornerShape(3.dp))
                                        )
                                        Text(
                                            text = "INTELLIGENT GRADE: ${status.name}",
                                            color = statusColor,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Black,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(2.dp)
                                            .background(Color.Black)
                                    )

                                    Text(
                                        text = text,
                                        color = BrutalistDark,
                                        fontSize = 13.sp,
                                        lineHeight = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.testTag("recall_feedback_text")
                                    )
                                }
                            }

                            BrutalistButton(
                                onClick = {
                                    viewModel.clearRecallFeedback()
                                    typedAnswer = ""
                                    // Advance index
                                    if (activeIndex + 1 < recallList.size) {
                                        activeIndex += 1
                                    } else {
                                        // End of queue
                                        activeIndex = 0
                                    }
                                },
                                backgroundColor = BrutalistCyan,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("recall_continue_button")
                            ) {
                                Text("CONTINUE STUDY PATHWAY", fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, color = Color.Black)
                            }
                        }
                    }
                }
            }

            // Universal Loader HUD
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

// Helper extension to bound width cleanly on large screens
fun Modifier.fillMaxWidthInBrutalist(): Modifier = this.fillMaxWidth().widthIn(max = 340.dp)
