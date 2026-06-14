package com.example.presentation.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.School
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.presentation.viewmodel.LoomViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeScreen(
    viewModel: LoomViewModel,
    onNavigateNext: () -> Unit
) {
    var keyInput by remember { mutableStateOf("") }
    val savedKey by viewModel.apiKey.collectAsState()
    val availableModels by viewModel.availableModels.collectAsState()

    // Grab saved key if already entered
    LaunchedEffect(savedKey) {
        if (keyInput.isEmpty() && savedKey.isNotEmpty()) {
            keyInput = savedKey
        }
    }

    Scaffold(
        containerColor = CreamBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(CreamBackground),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .widthIn(max = 480.dp)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Header Visual Brand: Brutalist card with Orange-Red background
                BrutalistCard(
                    backgroundColor = BrutalistOrangeRed,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = "Loom Icon",
                            tint = Color.White,
                            modifier = Modifier
                                .size(56.dp)
                                .border(2.dp, Color.White, RoundedCornerShape(28.dp))
                                .padding(8.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "LOOM",
                            color = Color.White,
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            fontStyle = FontStyle.Italic,
                            letterSpacing = 4.sp
                        )
                        Text(
                            text = "MIYAMOTO SENSEMAKING INTERACTIVE",
                            color = BrutalistYellow,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                // Subtitle
                Text(
                    text = "Turn any self-directed learning goal into a gorgeous, interactive study environment with advanced multi-model intelligence routing.",
                    color = BrutalistDark,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                // API Key Panel Wrap in a white card with thick black borders
                BrutalistCard(
                    backgroundColor = Color.White,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "OPENROUTER GATEWAY CONNECTION",
                            color = BrutalistDark,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )

                        // Styling OutlinedTextField for heavy border
                        OutlinedTextField(
                            value = keyInput,
                            onValueChange = { keyInput = it },
                            placeholder = { Text("sk-or-v1-...", color = BrutalistMuted) },
                            leadingIcon = {
                                Icon(Icons.Default.Key, contentDescription = "Key Icon", tint = Color.Black)
                            },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = BrutalistDark,
                                unfocusedTextColor = BrutalistDark,
                                focusedContainerColor = CreamBackground,
                                unfocusedContainerColor = CreamBackground,
                                focusedBorderColor = Color.Black,
                                unfocusedBorderColor = Color.Black
                            ),
                            shape = RoundedCornerShape(BrutalistRoundness),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(2.dp, Color.Black, RoundedCornerShape(BrutalistRoundness))
                                .testTag("api_key_input"),
                            singleLine = true
                        )

                        Text(
                            text = "Credentials reside 100% on-device. Obtain from openrouter.ai/keys",
                            color = BrutalistMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 14.sp
                        )

                        BrutalistButton(
                            onClick = {
                                if (keyInput.isNotBlank()) {
                                    viewModel.saveApiKey(keyInput.trim())
                                }
                            },
                            enabled = keyInput.isNotBlank(),
                            backgroundColor = BrutalistYellow,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("save_api_key_button")
                        ) {
                            Text("CONNECT GATEWAY", color = BrutalistDark, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, fontSize = 13.sp)
                        }
                    }
                }

                // Active connection pill
                if (keyInput.isNotEmpty() && availableModels.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(BrutalistCyan)
                            .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "⚡ API ACTIVE • ${availableModels.size} MODELS RETRIEVED",
                            color = BrutalistDark,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Enter App Button (Primary green/mint CTA)
                BrutalistButton(
                    onClick = {
                        onNavigateNext()
                    },
                    enabled = keyInput.isNotBlank(),
                    backgroundColor = BrutalistGreen,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("enter_app_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "START STUDY EXPERIENCE",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp
                        )
                        Icon(Icons.Default.ArrowForward, contentDescription = "Go", tint = Color.White)
                    }
                }
            }
        }
    }
}
