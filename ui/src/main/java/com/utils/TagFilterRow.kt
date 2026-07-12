package com.unibo.android.ui.utils

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.unibo.android.domain.models.TagModel

@Composable
fun TagFilterRow(
    tags: List<TagModel>,
    activeFilters: Set<Long>,
    onToggle: (Long) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tags.forEach { tag ->
            TagChip(
                tag = tag,
                selected = tag.id in activeFilters,
                onClick = { onToggle(tag.id) }
            )
        }
    }
}
