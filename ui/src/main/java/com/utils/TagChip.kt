package com.unibo.android.ui.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unibo.android.domain.models.TagModel

@Composable
fun TagChip(
    tag: TagModel,
    selected: Boolean = false,
    onClick: () -> Unit = {}
) {
    val color = runCatching { Color(android.graphics.Color.parseColor(tag.color)) }
        .getOrDefault(Color(0xFF6200EE))
    val onColor = if (color.luminance() > 0.4f) Color.Black else Color.White
    val shape = RoundedCornerShape(50)

    Text(
        text = tag.name,
        fontSize = 11.sp,
        color = if (selected) onColor else color,
        modifier = Modifier
            .clip(shape)
            .background(if (selected) color else Color.Transparent)
            .then(if (!selected) Modifier.border(1.dp, color, shape) else Modifier)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}
