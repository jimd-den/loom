package com.example.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val BrutalistBorderWidth = 2.5.dp
val BrutalistShadowOffset = 5.dp
val BrutalistRoundness = 12.dp

val CreamBackground = Color(0xFFFDFCF8)
val BrutalistOrangeRed = Color(0xFFFF4B2B)
val BrutalistCyan = Color(0xFF00D1FF)
val BrutalistYellow = Color(0xFFFFE600)
val BrutalistGreen = Color(0xFF10B981)
val BrutalistPurple = Color(0xFFB55FE6)
val BrutalistDark = Color(0xFF121212)
val BrutalistMuted = Color(0xFF6E6E66)

@Composable
fun BrutalistCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White,
    borderColor: Color = Color.Black,
    shadowColor: Color = Color.Black,
    shadowOffset: Dp = BrutalistShadowOffset,
    shape: Shape = RoundedCornerShape(BrutalistRoundness),
    borderWidth: Dp = BrutalistBorderWidth,
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier.padding(bottom = shadowOffset, end = shadowOffset)) {
        // Shadow Layer
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = shadowOffset, y = shadowOffset)
                .background(shadowColor, shape)
                .border(borderWidth, borderColor, shape)
        )
        // Main Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(backgroundColor, shape)
                .border(borderWidth, borderColor, shape)
        ) {
            content()
        }
    }
}

@Composable
fun BrutalistButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    backgroundColor: Color = BrutalistCyan,
    borderColor: Color = Color.Black,
    shadowColor: Color = Color.Black,
    shadowOffset: Dp = BrutalistShadowOffset,
    shape: Shape = RoundedCornerShape(BrutalistRoundness),
    borderWidth: Dp = BrutalistBorderWidth,
    content: @Composable RowScope.() -> Unit
) {
    val finalBgColor = if (enabled) backgroundColor else Color(0xFFD4D4D0)
    val finalShadowColor = if (enabled) shadowColor else Color(0xFF8E8E8A)

    Box(
        modifier = modifier
            .padding(bottom = shadowOffset, end = shadowOffset)
            .clickable(enabled = enabled, onClick = onClick)
    ) {
        // Shadow
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = shadowOffset, y = shadowOffset)
                .background(finalShadowColor, shape)
                .border(borderWidth, borderColor, shape)
        )
        // Button surface
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(finalBgColor, shape)
                .border(borderWidth, borderColor, shape)
                .padding(vertical = 12.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            content()
        }
    }
}

@Composable
fun BrutalistTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    singleLine: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = BrutalistMuted) },
        leadingIcon = leadingIcon,
        singleLine = singleLine,
        shape = RoundedCornerShape(BrutalistRoundness),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = BrutalistDark,
            unfocusedTextColor = BrutalistDark,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
            focusedBorderColor = Color.Black,
            unfocusedBorderColor = Color.Black,
            cursorColor = Color.Black
        ),
        modifier = modifier
            .fillMaxWidth()
            .border(BrutalistBorderWidth, Color.Black, RoundedCornerShape(BrutalistRoundness))
    )
}
