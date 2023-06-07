package com.example.jukebox.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.jukebox.R

val MichromaFont = FontFamily(
    Font(R.font.michroma)
)

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    titleLarge = TextStyle(
        fontFamily = MichromaFont,
        fontWeight = FontWeight.Normal,
        fontSize = 60.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
)
