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
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import com.example.domain.model.Difficulty
import com.example.domain.model.Source
import com.example.domain.model.SourceStatus
import com.example.presentation.viewmodel.LoomViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SourceLaneScreen(
    viewModel: LoomViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToReader: () -> Unit
) {
    val currentGoal by viewModel.currentGoal.collectAsState()
    val allSources by viewModel.currentSources.collectAsState()
    val isAiLoading by viewModel.isAiLoading.collectAsState()
    val aiMessage by viewModel.aiStateMessage.collectAsState()

    var selectedTab by remember { mutableStateOf(0) } // 0 = Accepted/Active, 1 = Suggestions

    val activeSources = allSources.filter { it.status == SourceStatus.ACCEPTED }
    val suggestedSources = allSources.filter { it.status == SourceStatus.SUGGESTED }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "CURATION DECKS: ${currentGoal?.title ?: "LEARNING FEED"}",
                                fontWeight = FontWeight.Black,
                                fontStyle = FontStyle.Italic,
                                fontSize = 15.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "SQ5R TEXTBOOK CHANNELS",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = BrutalistYellow
                            )
                        }
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
            Column(modifier = Modifier.fillMaxSize()) {
                // Brutalist styled custom tab row
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.White,
                    contentColor = Color.Black,
                    indicator = { tabPositions ->
                        Box(
                            modifier = Modifier
                                .tabIndicatorOffset(tabPositions[selectedTab])
                                .height(5.dp)
                                .background(BrutalistOrangeRed)
                                .border(1.5.dp, Color.Black)
                        )
                    },
                    modifier = Modifier.border(width = 2.5.dp, color = Color.Black)
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                "ACTIVE DECK (${activeSources.size})",
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                color = if (selectedTab == 0) BrutalistOrangeRed else BrutalistDark
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                "LEADS & CUES (${suggestedSources.size})",
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                color = if (selectedTab == 1) BrutalistOrangeRed else BrutalistDark
                            )
                        }
                    )
                }

                // Curation list
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
                ) {
                    if (selectedTab == 0) {
                        // Accepted active items
                        if (activeSources.isEmpty()) {
                            item {
                                BrutalistCard(
                                    backgroundColor = Color.White,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Icon(Icons.Default.MenuBook, contentDescription = "Empty", tint = BrutalistMuted, modifier = Modifier.size(44.dp))
                                        Text(
                                            text = "Your study desk is currently clear.\nSwitch to 'LEADS & CUES' to accept recommended books, articles, or wikipedia sources.",
                                            color = BrutalistMuted,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            textAlign = TextAlign.Center,
                                            lineHeight = 18.sp
                                        )
                                    }
                                }
                            }
                        } else {
                            items(activeSources) { source ->
                                ActiveSourceCard(
                                    source = source,
                                    onClick = {
                                        viewModel.openSource(source)
                                        onNavigateToReader()
                                    }
                                )
                            }
                        }
                    } else {
                        // AI Recommended suggestions
                        if (suggestedSources.isEmpty()) {
                            item {
                                BrutalistCard(
                                    backgroundColor = Color.White,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Icon(Icons.Default.CompassCalibration, contentDescription = "Empty", tint = BrutalistMuted, modifier = Modifier.size(44.dp))
                                        Text(
                                            text = "No newly recommended pathways available yet.",
                                            color = BrutalistMuted,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        } else {
                            items(suggestedSources) { source ->
                                SuggestedSourceCard(
                                    source = source,
                                    onAccept = { viewModel.acceptSource(source.id) },
                                    onDecline = { viewModel.archiveSource(source.id) }
                                )
                            }
                        }
                    }
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

@Composable
fun ActiveSourceCard(
    source: Source,
    onClick: () -> Unit
) {
    BrutalistCard(
        backgroundColor = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
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
                        .background(BrutalistCyan)
                        .border(2.dp, Color.Black, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.MenuBook, contentDescription = "Book", tint = Color.Black)
                }

                Column {
                    Text(
                        text = source.title,
                        color = BrutalistDark,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = source.url ?: "Educational textbook",
                        color = BrutalistMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Icon(Icons.Default.ChevronRight, contentDescription = "Read Now", tint = Color.Black)
        }
    }
}

@Composable
fun SuggestedSourceCard(
    source: Source,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    val levelColor = when (source.difficulty) {
        Difficulty.BEGINNER -> BrutalistCyan
        Difficulty.INTERMEDIATE -> BrutalistYellow
        Difficulty.ADVANCED -> BrutalistOrangeRed
    }

    BrutalistCard(
        backgroundColor = Color.White,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header: Title and Effort
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = source.title.uppercase(),
                        color = BrutalistDark,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 16.sp
                    )
                    Text(
                        text = source.url ?: "Reference book",
                        color = BrutalistOrangeRed,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(BrutalistYellow)
                        .border(2.dp, Color.Black, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = source.readingEffort.uppercase(),
                        color = BrutalistDark,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(Color.Black)
            )

            // Details blocks
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "▼ WHY IT MATTERS",
                    color = BrutalistDark,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = source.whyItMatters,
                    color = BrutalistDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )

                Text(
                    text = "▼ WHAT IT UNLOCKS",
                    color = BrutalistDark,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = source.whatItUnlocks,
                    color = BrutalistMuted,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                )
            }

            // Level & action controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Difficulty Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(levelColor)
                        .border(2.dp, Color.Black, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = source.difficulty.name,
                        color = if (source.difficulty == Difficulty.ADVANCED) Color.White else BrutalistDark,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black
                    )
                }

                // Control buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onDecline,
                        modifier = Modifier
                            .size(36.dp)
                            .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                            .background(Color.White, RoundedCornerShape(8.dp))
                            .testTag("decline_source_${source.id}")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Archive", tint = BrutalistOrangeRed, modifier = Modifier.size(16.dp))
                    }

                    Button(
                        onClick = onAccept,
                        colors = ButtonDefaults.buttonColors(containerColor = BrutalistGreen),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .height(36.dp)
                            .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                            .testTag("accept_source_${source.id}")
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Check", tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("ACCEPT", fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = Color.White)
                    }
                }
            }
        }
    }
}
