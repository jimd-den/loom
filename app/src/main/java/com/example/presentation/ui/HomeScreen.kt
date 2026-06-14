package com.example.presentation.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.Goal
import com.example.domain.model.GoalStatus
import com.example.presentation.viewmodel.LoomViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: LoomViewModel,
    onNavigateToInterview: () -> Unit,
    onNavigateToCanvas: () -> Unit,
    onNavigateToRecallQueue: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val goals by viewModel.goals.collectAsState()
    val recallQueue by viewModel.recallQueue.collectAsState()
    val isAiLoading by viewModel.isAiLoading.collectAsState()
    val aiMessage by viewModel.aiStateMessage.collectAsState()

    var newGoalText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = "LOOM // Study Canvas",
                            fontWeight = FontWeight.Black,
                            fontStyle = FontStyle.Italic,
                            fontSize = 20.sp,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp,
                            color = Color.White
                        )
                    },
                    actions = {
                        IconButton(
                            onClick = onNavigateToSettings,
                            modifier = Modifier
                                .testTag("settings_button")
                                .padding(end = 8.dp)
                                .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                                .background(BrutalistYellow, RoundedCornerShape(8.dp))
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.Black)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = BrutalistOrangeRed
                    )
                )
                // Bold black separator line at bottom of the App Bar
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
                .background(CreamBackground)
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                contentPadding = PaddingValues(top = 20.dp, bottom = 100.dp)
            ) {
                // Section 1: Plant a new Goal
                item {
                    Text(
                        text = "▼ PLANT NEW STUDY ENVIRONMENT",
                        color = Color.Black,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.5.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    BrutalistCard(
                        backgroundColor = Color.White,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "What topic, book, design guideline, or complex skill do you want to master today?",
                                color = BrutalistDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )

                            OutlinedTextField(
                                value = newGoalText,
                                onValueChange = { newGoalText = it },
                                placeholder = { Text("e.g. Clean Architecture, Android Canvas, SQ5R studying...", color = BrutalistMuted) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = BrutalistDark,
                                    unfocusedTextColor = BrutalistDark,
                                    focusedContainerColor = CreamBackground,
                                    unfocusedContainerColor = CreamBackground,
                                    focusedBorderColor = Color.Black,
                                    unfocusedBorderColor = Color.Black,
                                    cursorColor = Color.Black
                                ),
                                shape = RoundedCornerShape(BrutalistRoundness),
                                textStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(2.dp, Color.Black, RoundedCornerShape(BrutalistRoundness))
                                    .testTag("new_goal_input"),
                                singleLine = true
                            )

                            BrutalistButton(
                                onClick = {
                                    if (newGoalText.isNotBlank()) {
                                        viewModel.plantGoal(newGoalText.trim()) {
                                            newGoalText = ""
                                            onNavigateToInterview()
                                        }
                                    }
                                },
                                enabled = newGoalText.isNotBlank() && !isAiLoading,
                                backgroundColor = BrutalistCyan,
                                modifier = Modifier
                                    .align(Alignment.End)
                                    .testTag("plant_goal_button")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.Black)
                                    Text("PLANT GOAL", fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = Color.Black)
                                }
                            }
                        }
                    }
                }

                // Section 2: Spaced Recall Queue
                if (recallQueue.isNotEmpty()) {
                    item {
                        Text(
                            text = "▼ ACTIVE RETRIEVALS REQUISITE",
                            color = BrutalistOrangeRed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.5.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        BrutalistCard(
                            backgroundColor = BrutalistYellow,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToRecallQueue() }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color.White)
                                            .border(2.dp, Color.Black, RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.HourglassEmpty, contentDescription = "Time", tint = Color.Black)
                                    }
                                    Column {
                                        Text(
                                            text = "${recallQueue.size} Cards to Recite",
                                            color = BrutalistDark,
                                            fontWeight = FontWeight.Black,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 15.sp
                                        )
                                        Text(
                                            text = "Strengthen active synapses and defeat decay.",
                                            color = BrutalistDark,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                                Icon(Icons.Default.ArrowForward, contentDescription = "Recall Now", tint = Color.Black)
                            }
                        }
                    }
                }

                // Section 3: Active Maps
                item {
                    Text(
                        text = "▼ ACTIVE STUDY PATHWAYS",
                        color = Color.Black,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.5.sp
                    )
                }

                if (goals.isEmpty()) {
                    item {
                        BrutalistCard(
                            backgroundColor = Color.White,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Map, contentDescription = "Empty Maps", tint = BrutalistMuted, modifier = Modifier.size(48.dp))
                                Text(
                                    text = "No study paths active. Plant a learning goal above to get started!",
                                    color = BrutalistMuted,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    items(goals) { goal ->
                        BrutalistCard(
                            backgroundColor = Color.White,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.selectGoal(goal)
                                    if (goal.status == GoalStatus.INTERVIEWING) {
                                        onNavigateToInterview()
                                    } else {
                                        onNavigateToCanvas()
                                    }
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                if (goal.status == GoalStatus.INTERVIEWING) BrutalistPurple else BrutalistCyan
                                            )
                                            .border(2.dp, Color.Black, RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (goal.status == GoalStatus.INTERVIEWING) Icons.Default.ChatBubbleOutline else Icons.Default.MenuBook,
                                            contentDescription = "Status Icon",
                                            tint = if (goal.status == GoalStatus.INTERVIEWING) Color.White else Color.Black
                                        )
                                    }

                                    Column {
                                        Text(
                                            text = goal.title.uppercase(),
                                            color = BrutalistDark,
                                            fontWeight = FontWeight.Black,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 15.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = if (goal.status == GoalStatus.INTERVIEWING) "Clarifying interview in progress..." else "Workspace fully active",
                                            color = BrutalistMuted,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    IconButton(
                                        onClick = { viewModel.removeGoal(goal.id) },
                                        modifier = Modifier.testTag("delete_goal_${goal.id}")
                                    ) {
                                        Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = BrutalistOrangeRed)
                                    }
                                    Icon(Icons.Default.ChevronRight, contentDescription = "View", tint = Color.Black)
                                }
                            }
                        }
                    }
                }
            }

            // Universal AI Loading Hud overlays
            if (isAiLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.85f))
                        .clickable(enabled = false) {},
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
