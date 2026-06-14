package com.example.presentation.ui

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.example.domain.model.IdeaCard
import com.example.domain.model.IdeaCategory
import com.example.presentation.viewmodel.LoomViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun IdeaCanvasScreen(
    viewModel: LoomViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToDiscovery: () -> Unit
) {
    val currentGoal by viewModel.currentGoal.collectAsState()
    val ideaCards by viewModel.currentIdeaCards.collectAsState()
    val isAiLoading by viewModel.isAiLoading.collectAsState()
    val aiMessage by viewModel.aiStateMessage.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var dialogText by remember { mutableStateOf("") }
    var dialogCategory by remember { mutableStateOf(IdeaCategory.CONCEPT) }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = (currentGoal?.title ?: "Idea Canvas").uppercase(),
                                fontWeight = FontWeight.Black,
                                fontStyle = FontStyle.Italic,
                                fontSize = 16.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "CONCEPT RESOLVING CANVAS",
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
                    actions = {
                        Button(
                            onClick = onNavigateToDiscovery,
                            colors = ButtonDefaults.buttonColors(containerColor = BrutalistCyan),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                                .testTag("open_sources_button")
                        ) {
                            Icon(Icons.Default.Book, contentDescription = "Sources", tint = Color.Black, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("SOURCES", color = Color.Black, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
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
        floatingActionButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // AI chime button (Purple)
                FloatingActionButton(
                    onClick = { viewModel.chimInCanvas() },
                    containerColor = BrutalistPurple,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .border(2.5.dp, Color.Black, RoundedCornerShape(12.dp))
                        .testTag("ai_chime_button")
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = "Miyamoto Suggestion", tint = Color.White)
                }

                // Add manual card button (Yellow!)
                FloatingActionButton(
                    onClick = { showDialog = true },
                    containerColor = BrutalistYellow,
                    contentColor = Color.Black,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .border(2.5.dp, Color.Black, RoundedCornerShape(12.dp))
                        .testTag("add_card_fab")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Card", tint = Color.Black)
                }
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Intro instruction banner in white brutalist card
                BrutalistCard(
                    backgroundColor = Color.White,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(BrutalistYellow, RoundedCornerShape(6.dp))
                                .border(2.dp, Color.Black, RoundedCornerShape(6.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Lightbulb, contentDescription = "Guide", tint = Color.Black, modifier = Modifier.size(20.dp))
                        }
                        Text(
                            text = "Tap node cells to resolve fuzzy knowledge. Tap (✦) code to let Sensai seed missing vocabularies, requirements, and deeper inquiries.",
                            color = BrutalistDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                }

                if (ideaCards.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        BrutalistCard(
                            backgroundColor = Color.White,
                            modifier = Modifier.widthIn(max = 300.dp)
                        ) {
                            Text(
                                text = "CANVAS VOID\nAdd your custom concept card or execute a Chime In.",
                                color = BrutalistMuted,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(20.dp)
                            )
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 165.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(ideaCards) { card ->
                            IdeaCardItem(
                                card = card,
                                onClick = { viewModel.toggleCardFuzzy(card.id, !card.isFuzzy) },
                                onDelete = { viewModel.removeCanvasCard(card.id) }
                            )
                        }
                    }
                }
            }

            // Dialog for adding manual cards
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { 
                        Text(
                            "COMPOSE CONCEPT NODE", 
                            color = BrutalistDark, 
                            fontFamily = FontFamily.Monospace, 
                            fontWeight = FontWeight.Black, 
                            fontSize = 16.sp
                        ) 
                    },
                    text = {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            OutlinedTextField(
                                value = dialogText,
                                onValueChange = { dialogText = it },
                                placeholder = { Text("Compose concept vocabulary or specific formula query...", color = BrutalistMuted) },
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
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(2.dp, Color.Black, RoundedCornerShape(BrutalistRoundness))
                                    .testTag("idea_dialog_input"),
                                singleLine = true
                            )

                            // Category Selector
                            Column {
                                Text(
                                    "CATEGORIZE ELEMENT", 
                                    color = BrutalistDark, 
                                    fontSize = 11.sp, 
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Black
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    IdeaCategory.values().forEach { cat ->
                                        val active = dialogCategory == cat
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (active) BrutalistCyan else Color.White)
                                                .border(
                                                    width = 2.dp,
                                                    color = Color.Black,
                                                    shape = RoundedCornerShape(8.dp)
                                                )
                                                .clickable { dialogCategory = cat }
                                                .padding(vertical = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = cat.name.take(5),
                                                color = BrutalistDark,
                                                fontSize = 10.sp,
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Black
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (dialogText.isNotBlank()) {
                                    viewModel.addCanvasCard(dialogText.trim(), dialogCategory)
                                    dialogText = ""
                                    showDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BrutalistGreen),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                        ) {
                            Text("DEPLOY NODE", color = Color.White, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("ABNEGATE", color = BrutalistDark, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                        }
                    },
                    containerColor = Color.White,
                    modifier = Modifier.border(3.dp, Color.Black, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp)
                )
            }

            // Loader HUD
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
fun IdeaCardItem(
    card: IdeaCard,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val categoryColor = when (card.category) {
        IdeaCategory.CONCEPT -> BrutalistCyan
        IdeaCategory.QUESTION -> BrutalistPurple
        IdeaCategory.VOCABULARY -> BrutalistYellow
        IdeaCategory.EXAMPLE -> BrutalistGreen
        IdeaCategory.LINK -> Color.White
    }

    val finalCategoryTextColor = if (card.category == IdeaCategory.QUESTION || card.category == IdeaCategory.CONCEPT) Color.White else BrutalistDark

    // Inner card border/accent represents fuzzy query doubts
    val activeBorderColor = if (card.isFuzzy) BrutalistOrangeRed else Color.Black
    val activeShadowColor = if (card.isFuzzy) BrutalistOrangeRed else Color.Black

    BrutalistCard(
        backgroundColor = Color.White,
        borderColor = activeBorderColor,
        shadowColor = activeShadowColor,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 135.dp)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Row (Category Badge & Delete)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(categoryColor)
                        .border(1.5.dp, Color.Black, RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = card.category.name,
                        color = finalCategoryTextColor,
                        fontSize = 8.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(24.dp)
                        .testTag("delete_card_${card.id}")
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove", tint = BrutalistOrangeRed, modifier = Modifier.size(16.dp))
                }
            }

            // Body content Text
            Text(
                text = card.content,
                color = BrutalistDark,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 16.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(vertical = 6.dp)
            )

            // Status indication
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = if (card.isFuzzy) Icons.Default.Warning else Icons.Default.Done,
                    contentDescription = "Status",
                    tint = if (card.isFuzzy) BrutalistOrangeRed else BrutalistGreen,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = if (card.isFuzzy) "FUZZY DOUBT" else "RESOLVED Node",
                    color = if (card.isFuzzy) BrutalistOrangeRed else BrutalistGreen,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}
