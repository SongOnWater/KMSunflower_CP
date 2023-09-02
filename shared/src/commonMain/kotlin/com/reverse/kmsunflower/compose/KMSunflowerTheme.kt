package com.reverse.kmsunflower.compose

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Typography
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun KMSunflowerTheme(
    content: @Composable () -> Unit
) {

    val typography = Typography(
        body1 = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        )
    )
    val shapes = Shapes(
        small = RoundedCornerShape(4.dp),
        medium = RoundedCornerShape(4.dp),
        large = RoundedCornerShape(0.dp)
    )

    MaterialTheme(
        colors = MaterialTheme.colors.copy(
            primary = Color(0xFF49bb79),
            primaryVariant = Color(0xFF005d2b),
            onPrimary = Color(0xFFfafafa),
            onSurface = Color(0xFF000000),
            surface  = Color(0xFFfafafa),
            secondary = Color(0xFFffff63),
            background = Color(0xFF49bb79)
        ),
        typography = typography,
        shapes = shapes,
        content = content
    )
}
