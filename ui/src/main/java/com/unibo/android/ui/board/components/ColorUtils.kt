package com.unibo.android.ui.board.components

import androidx.compose.ui.graphics.Color

fun String.toColorOrDefault(default: Color = Color.Gray): Color =
    try {
        Color(android.graphics.Color.parseColor(this))
    } catch (e: IllegalArgumentException) {
        default
    }

fun Color.contrastingTextColor(): Color {
    val luminance = 0.299 * red + 0.587 * green + 0.114 * blue
    return if (luminance > 0.5f) Color.Black else Color.White
}