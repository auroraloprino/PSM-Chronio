package com.unibo.android.ui.utils

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(tag.name, fontSize = 12.sp) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = color.copy(alpha = 0.2f),
            selectedLabelColor = color
        ),
        modifier = Modifier.border(1.dp, color, RoundedCornerShape(50))
    )
}
