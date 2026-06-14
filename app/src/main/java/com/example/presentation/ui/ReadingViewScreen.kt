package com.example.presentation.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.presentation.viewmodel.LoomViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadingViewScreen(
    viewModel: LoomViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToRecallQueue: () -> Unit
) {
    val activeSource by viewModel.activeSource.collectAsState()
    val fullText by viewModel.sourceText.collectAsState()
    val isAiLoading by viewModel.isAiLoading.collectAsState()
    val aiMessage by viewModel.aiStateMessage.collectAsState()

    // Tutor / notes panel inputs
    val tutorFeedback by viewModel.tutorFeedback.collectAsState()
    val activeNotes by viewModel.activeSourceNotes.collectAsState()
    val activeQuestions by viewModel.activeSourceQuestions.collectAsState()

    var showTutorBottomSheet by remember { mutableStateOf(false) }
    var tutorResultText by remember { mutableStateOf("") }
    
    var showNoteDialog by remember { mutableStateOf(false) }
    var noteHeadingInput by remember { mutableStateOf("") }
    var noteContentInput by remember { mutableStateOf("") }

    var showQuestionDialog by remember { mutableStateOf(false) }
    var questionInput by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    // Update bottom sheet if tutor feedback returns
    LaunchedEffect(tutorFeedback) {
        if (tutorFeedback.isNotEmpty()) {
            tutorResultText = tutorFeedback
            showTutorBottomSheet = true
        }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = (activeSource?.title ?: "SQ5R TEXTBOOK").uppercase(),
                            fontWeight = FontWeight.Black,
                            fontStyle = FontStyle.Italic,
                            fontSize = 15.sp,
                            fontFamily = FontFamily.Monospace,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
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
                    actions = {
                        // Navigate to Recall button styled with thick outline
                        IconButton(
                            onClick = onNavigateToRecallQueue,
                            modifier = Modifier
                                .testTag("go_to_recall_button")
                                .padding(end = 8.dp)
                                .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                                .background(BrutalistYellow, RoundedCornerShape(8.dp))
                        ) {
                            Icon(Icons.Default.HourglassEmpty, contentDescription = "Recall", tint = Color.Black)
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
                
                // Miyamoto tutor shelf toolbar styled as a chunky shelf row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SENSEI TUTOR SHELF:",
                        color = BrutalistDark,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(end = 4.dp)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Small styled brutalist help buttons
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(BrutalistCyan)
                                .border(1.5.dp, Color.Black, RoundedCornerShape(6.dp))
                                .clickable { viewModel.requestTutorAid("SIMPLIFY", fullText.take(2000)) }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("tutor_simplify_button")
                        ) {
                            Text("SIMPLIFY", fontSize = 10.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, color = BrutalistDark)
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(BrutalistPurple)
                                .border(1.5.dp, Color.Black, RoundedCornerShape(6.dp))
                                .clickable { viewModel.requestTutorAid("ANALOGY", fullText.take(2000)) }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("tutor_analogy_button")
                        ) {
                            Text("ANALOGY", fontSize = 10.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, color = Color.White)
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(BrutalistYellow)
                                .border(1.5.dp, Color.Black, RoundedCornerShape(6.dp))
                                .clickable { viewModel.requestTutorAid("CHALLENGE", fullText.take(2000)) }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("tutor_challenge_button")
                        ) {
                            Text("CHALLENGE", fontSize = 10.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, color = BrutalistDark)
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.5.dp)
                        .background(Color.Black)
                )

                // Scrollable workspace content split pane
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(scrollState)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Textbook study manuscript (brutalist paper card)
                    BrutalistCard(
                        backgroundColor = Color.White,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "STUDY MANUSCRIPT",
                                    color = BrutalistOrangeRed,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    letterSpacing = 1.sp
                                )

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(BrutalistYellow)
                                        .border(1.5.dp, Color.Black, RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "SQ5R PARADIGM",
                                        color = BrutalistDark,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 8.sp,
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

                            // Reading text output
                            Text(
                                text = if (fullText.isNotBlank()) fullText else "Initializing digital study textbook...",
                                color = BrutalistDark,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                lineHeight = 24.sp,
                                modifier = Modifier.testTag("source_text_display")
                            )
                        }
                    }

                    // Log notes and active questions buttons styled as chunky buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        BrutalistButton(
                            onClick = { showNoteDialog = true },
                            backgroundColor = BrutalistCyan,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("add_note_button")
                        ) {
                            Icon(Icons.Default.EditNote, contentDescription = "Note", tint = Color.Black)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("RECORD NOTES", fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = Color.Black)
                        }

                        BrutalistButton(
                            onClick = { showQuestionDialog = true },
                            backgroundColor = BrutalistPurple,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("add_question_button")
                        ) {
                            Icon(Icons.Default.AddTask, contentDescription = "Question", tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("ACTIVE PROBING", fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = Color.White)
                        }
                    }

                    // Saved notes lists
                    if (activeNotes.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "▼ LOGGED STUDY NOTES (${activeNotes.size})",
                                color = BrutalistDark,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.sp
                            )

                            activeNotes.forEach { note ->
                                BrutalistCard(
                                    backgroundColor = Color.White,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = note.heading.uppercase(),
                                            color = BrutalistOrangeRed,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Black,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(note.content, color = BrutalistDark, fontWeight = FontWeight.Medium, fontSize = 13.sp)
                                    }
                                }
                            }
                        }
                    }

                    // Show Active Recall Questions list
                    if (activeQuestions.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "▼ ACTIVE RECALL PROBES DEPLOYED (${activeQuestions.size})",
                                color = BrutalistDark,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.sp
                            )

                            activeQuestions.forEach { question ->
                                BrutalistCard(
                                    backgroundColor = Color.White,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = if (question.isUserGenerated) "USER INQUIRY" else "MIYAMOTO SYNAPSE PROBE",
                                                color = if (question.isUserGenerated) BrutalistGreen else BrutalistPurple,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Black,
                                                fontFamily = FontFamily.Monospace
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(question.questionText, color = BrutalistDark, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        }

                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(
                                                    when (question.recallStatus) {
                                                        com.example.domain.model.RecallStatus.UNTESTED -> Color(0xFFE2E8F0)
                                                        com.example.domain.model.RecallStatus.MASTERED -> BrutalistGreen
                                                        com.example.domain.model.RecallStatus.RETRY -> BrutalistOrangeRed
                                                    }
                                                )
                                                .border(1.5.dp, Color.Black, RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = question.recallStatus.name,
                                                color = if (question.recallStatus == com.example.domain.model.RecallStatus.UNTESTED) BrutalistDark else Color.White,
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Black,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Custom Dialog for saving notes
            if (showNoteDialog) {
                AlertDialog(
                    onDismissRequest = { showNoteDialog = false },
                    title = { Text("LOG STUDY HEADING NOTE", color = BrutalistDark, fontSize = 15.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Black) },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            OutlinedTextField(
                                value = noteHeadingInput,
                                onValueChange = { noteHeadingInput = it },
                                label = { Text("Heading / Source Section") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = BrutalistDark,
                                    unfocusedTextColor = BrutalistDark,
                                    focusedBorderColor = Color.Black,
                                    unfocusedBorderColor = Color.Black
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                                    .testTag("note_heading_input")
                            )

                            OutlinedTextField(
                                value = noteContentInput,
                                onValueChange = { noteContentInput = it },
                                label = { Text("Summarized Paraphrase") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = BrutalistDark,
                                    unfocusedTextColor = BrutalistDark,
                                    focusedBorderColor = Color.Black,
                                    unfocusedBorderColor = Color.Black
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                                    .testTag("note_content_input")
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (noteContentInput.isNotBlank()) {
                                    viewModel.saveHeadingNote(noteHeadingInput.ifBlank { "General" }, noteContentInput.trim())
                                    noteHeadingInput = ""
                                    noteContentInput = ""
                                    showNoteDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BrutalistGreen),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                        ) {
                            Text("RECORD", color = Color.White, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showNoteDialog = false }) {
                            Text("CANCEL", color = BrutalistDark, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                        }
                    },
                    containerColor = Color.White,
                    modifier = Modifier.border(3.dp, Color.Black, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp)
                )
            }

            // Custom Dialog for creating active recall questions
            if (showQuestionDialog) {
                AlertDialog(
                    onDismissRequest = { showQuestionDialog = false },
                    title = { Text("FORMULATE RECALL PROBE", color = BrutalistDark, fontSize = 15.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Black) },
                    text = {
                        OutlinedTextField(
                            value = questionInput,
                            onValueChange = { questionInput = it },
                            placeholder = { Text("Develop custom retention inquiry or synapse quiz...", color = BrutalistMuted) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = BrutalistDark,
                                unfocusedTextColor = BrutalistDark,
                                focusedBorderColor = Color.Black,
                                unfocusedBorderColor = Color.Black
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp)
                                .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                                .testTag("question_text_input")
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (questionInput.isNotBlank()) {
                                    viewModel.createCustomRecallQuestion(questionInput.trim())
                                    questionInput = ""
                                    showQuestionDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BrutalistPurple),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                        ) {
                            Text("INTEGRATE", color = Color.White, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showQuestionDialog = false }) {
                            Text("CANCEL", color = BrutalistDark, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                        }
                    },
                    containerColor = Color.White,
                    modifier = Modifier.border(3.dp, Color.Black, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp)
                )
            }

            // Tutor Bottom Sheet explanation drawer styled as a brutalist custom sheet
            if (showTutorBottomSheet) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable { showTutorBottomSheet = false }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                            .border(width = 3.dp, color = Color.Black, shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                            .align(Alignment.BottomCenter)
                            .heightIn(max = 450.dp)
                            .clickable(enabled = false) {}
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(20.dp)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .background(BrutalistYellow, RoundedCornerShape(6.dp))
                                            .border(1.5.dp, Color.Black, RoundedCornerShape(6.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.AutoAwesome, contentDescription = "Tutor Output", tint = Color.Black, modifier = Modifier.size(16.dp))
                                    }
                                    Text("MIYAMOTO TUTOR OUTPUT", color = BrutalistDark, fontSize = 12.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                                }
                                IconButton(
                                    onClick = { showTutorBottomSheet = false },
                                    modifier = Modifier
                                        .size(28.dp)
                                        .border(2.dp, Color.Black, RoundedCornerShape(6.dp))
                                        .background(Color.White)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Black, modifier = Modifier.size(14.dp))
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(2.dp)
                                    .background(Color.Black)
                            )

                            Text(
                                text = tutorResultText,
                                color = BrutalistDark,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 22.sp
                            )
                        }
                    }
                }
            }

            // Loader overlay
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
