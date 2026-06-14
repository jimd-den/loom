package com.example.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Save
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.presentation.viewmodel.LoomViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: LoomViewModel,
    onNavigateBack: () -> Unit
) {
    val savedKey by viewModel.apiKey.collectAsState()
    val availableModels by viewModel.availableModels.collectAsState()

    // Observe specific task preference flows
    val mOnboarding by viewModel.modelOnboarding.collectAsState("")
    val mCanvas by viewModel.modelCanvas.collectAsState("")
    val mSource by viewModel.modelSource.collectAsState("")
    val mTutor by viewModel.modelTutor.collectAsState("")
    val mQuestions by viewModel.modelQuestions.collectAsState("")
    val mEvaluation by viewModel.modelEvaluation.collectAsState("")

    var localKeyInput by remember { mutableStateOf("") }

    LaunchedEffect(savedKey) {
        if (localKeyInput.isEmpty()) {
            localKeyInput = savedKey
        }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = "SETTINGS & ROUTER",
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Key Configuration
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "▼ OPENROUTER API AUTHORIZATION KEY",
                        color = BrutalistDark,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )

                    OutlinedTextField(
                        value = localKeyInput,
                        onValueChange = { localKeyInput = it },
                        placeholder = { Text("sk-or-v1-...", color = BrutalistMuted) },
                        leadingIcon = {
                            Icon(Icons.Default.Key, contentDescription = "Key Icon", tint = BrutalistDark)
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
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
                            .border(2.dp, Color.Black, RoundedCornerShape(BrutalistRoundness))
                            .testTag("settings_api_key_field"),
                        singleLine = true
                    )

                    BrutalistButton(
                        onClick = {
                            viewModel.saveApiKey(localKeyInput.trim())
                        },
                        backgroundColor = BrutalistCyan,
                        modifier = Modifier
                            .align(Alignment.End)
                            .testTag("settings_save_key_btn")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Save, contentDescription = "Save", tint = Color.Black, modifier = Modifier.size(16.dp))
                            Text("SAVE SECURE KEY", fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = Color.Black)
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(Color.Black)
                )

                // Task Specific Routing policies
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "▼ TASK-SPECIFIC MODEL CORES",
                        color = BrutalistDark,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )

                    BrutalistCard(
                        backgroundColor = Color.White,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Divide work between deep reasoning models for clarification, and cost-effective ultra-fast models for reading simplifications and vocabulary compilation.",
                            color = BrutalistMuted,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }

                    // Task selection cards
                    TaskModelDropdownItem(
                        taskName = "GOAL CLARIFY INTERVIEW",
                        currentModel = mOnboarding,
                        availableModels = availableModels,
                        onModelSelected = { viewModel.saveTaskModel("onboarding", it) }
                    )

                    TaskModelDropdownItem(
                        taskName = "IDEA CANVAS CHIME IN",
                        currentModel = mCanvas,
                        availableModels = availableModels,
                        onModelSelected = { viewModel.saveTaskModel("canvas", it) }
                    )

                    TaskModelDropdownItem(
                        taskName = "RECOMMENDED STUDY SOURCES",
                        currentModel = mSource,
                        availableModels = availableModels,
                        onModelSelected = { viewModel.saveTaskModel("source", it) }
                    )

                    TaskModelDropdownItem(
                        taskName = "PASSAGE REWRITE TUTOR",
                        currentModel = mTutor,
                        availableModels = availableModels,
                        onModelSelected = { viewModel.saveTaskModel("tutor", it) }
                    )

                    TaskModelDropdownItem(
                        taskName = "SQ5R RECALL ENGINES",
                        currentModel = mQuestions,
                        availableModels = availableModels,
                        onModelSelected = { viewModel.saveTaskModel("questions", it) }
                    )

                    TaskModelDropdownItem(
                        taskName = "RETRIEVAL GRADE EVALUATORS",
                        currentModel = mEvaluation,
                        availableModels = availableModels,
                        onModelSelected = { viewModel.saveTaskModel("evaluation", it) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskModelDropdownItem(
    taskName: String,
    currentModel: String,
    availableModels: List<com.example.domain.usecase.OpenRouterClientModel>,
    onModelSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    BrutalistCard(
        backgroundColor = Color.White,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = taskName,
                color = BrutalistOrangeRed,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(CreamBackground)
                    .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                    .clickable { expanded = true }
                    .padding(vertical = 10.dp, horizontal = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = currentModel.ifBlank { "google/gemini-2.5-flash" },
                        color = BrutalistDark,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                    Icon(
                        Icons.Default.ArrowDropDown, 
                        contentDescription = "Choose", 
                        tint = Color.Black, 
                        modifier = Modifier.size(24.dp)
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .background(Color.White)
                        .border(2.dp, Color.Black)
                ) {
                    if (availableModels.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("google/gemini-2.5-flash (Curated)", color = BrutalistDark, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                            onClick = {
                                onModelSelected("google/gemini-2.5-flash")
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("google/gemini-2.5-pro (Curated)", color = BrutalistDark, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                            onClick = {
                                onModelSelected("google/gemini-2.5-pro")
                                expanded = false
                            }
                        )
                    } else {
                        availableModels.forEach { model ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(model.name, color = BrutalistDark, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                                        Text(model.id, color = BrutalistMuted, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                                    }
                                },
                                onClick = {
                                    onModelSelected(model.id)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
