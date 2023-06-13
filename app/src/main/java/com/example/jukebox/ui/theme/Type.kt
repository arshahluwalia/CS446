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

val NunitoFont = FontFamily(
    Font(R.font.nunito_regular, weight = FontWeight.Normal)
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
    ),
    titleMedium = TextStyle(
        fontFamily = MichromaFont,
        fontWeight = FontWeight.Normal,
        fontSize = 40.sp,
    ),
    titleSmall = TextStyle(
        fontFamily = MichromaFont,
        fontWeight = FontWeight.Normal,
        fontSize = 30.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = NunitoFont,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = NunitoFont,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = NunitoFont,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
    ),
)
