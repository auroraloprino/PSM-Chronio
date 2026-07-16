package com.unibo.android.ui.board.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.unibo.android.domain.models.BoardTagModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagFilterRow(
    tags: List<BoardTagModel>,
    activeFilters: Set<Long>,
    onToggleFilter: (Long) -> Unit,
    onClearFilters: () -> Unit,
    onCreateTag: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(tags, key = { it.id }) { tag ->
            val selected = tag.id in activeFilters
            FilterChip(
                selected = selected,
                onClick = { onToggleFilter(tag.id) },
                label = { Text(tag.name) },
                leadingIcon = {
                    Surface(
                        color = tag.color.toColorOrDefault(),
                        shape = CircleShape,
                        modifier = Modifier.size(12.dp)
                    ) {}
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = tag.color.toColorOrDefault().copy(alpha = 0.25f)
                )
            )
        }

        if (activeFilters.isNotEmpty()) {
            item {
                AssistChip(
                    onClick = onClearFilters,
                    label = { Text("Pulisci") },
                    leadingIcon = { Icon(Icons.Default.Clear, contentDescription = null) }
                )
            }
        }

        item {
            AssistChip(
                onClick = onCreateTag,
                label = { Text("Nuovo tag") },
                leadingIcon = { Icon(Icons.Default.Add, contentDescription = null) }
            )
        }
    }
}