package com.unibo.android.ui.utils

import android.graphics.Color as AndroidColor
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.unibo.android.domain.models.TagModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagFilterRow(
    tags: List<TagModel>,
    activeFilters: Set<Long>,
    onToggle: (Long) -> Unit,
    onEdit: (TagModel) -> Unit = {},
    onDelete: (TagModel) -> Unit = {},
    onCreateNew: () -> Unit = {},
    noTagFilterId: Long = -1L
) {
    var showSheet by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        IconButton(onClick = { showSheet = true }) {
            Icon(
                Icons.Default.Menu,
                contentDescription = "Tag",
                tint = if (activeFilters.isNotEmpty()) MaterialTheme.colorScheme.primary
                       else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            "Tag",
            style = MaterialTheme.typography.labelLarge,
            color = if (activeFilters.isNotEmpty()) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false; query = "" },
            sheetState = sheetState
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Filtra per tag", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    if (activeFilters.isNotEmpty()) {
                        Text(
                            "Rimuovi filtri",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable { activeFilters.forEach { onToggle(it) } }
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Cerca tag...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(Modifier.height(8.dp))
            }

            val filtered = tags.filter { it.name.contains(query, ignoreCase = true) }
            val noTagSelected = noTagFilterId in activeFilters

            LazyColumn(modifier = Modifier.weight(1f, fill = false)) {
                // voce fissa "Senza tag" in cima, visibile solo se la ricerca non esclude
                if ("senza tag".contains(query, ignoreCase = true) || query.isBlank()) {
                    item(key = "no_tag") {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onToggle(noTagFilterId) }
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(Color(AndroidColor.parseColor("#9E9E9E")))
                                    .then(
                                        if (noTagSelected) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                        else Modifier
                                    )
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = "Senza tag",
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (noTagSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (noTagSelected) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.onSurface
                            )
                        }
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    }
                }

                if (filtered.isEmpty() && query.isNotBlank()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Nessun tag trovato", style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                } else {
                    items(filtered, key = { it.id }) { tag ->
                        val selected = tag.id in activeFilters
                        val tagColor = remember(tag.color) {
                            runCatching { Color(AndroidColor.parseColor(tag.color)) }
                                .getOrDefault(Color(0xFF6200EE))
                        }
                        TagRow(
                            tag = tag,
                            tagColor = tagColor,
                            selected = selected,
                            onToggle = { onToggle(tag.id) },
                            onEdit = { showSheet = false; query = ""; onEdit(tag) },
                            onDelete = { onDelete(tag) }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    }
                }
            }

            HorizontalDivider()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showSheet = false; query = ""; onCreateNew() }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                Text("Nuovo tag", style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun TagRow(
    tag: TagModel,
    tagColor: Color,
    selected: Boolean,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(tagColor)
                .then(
                    if (selected) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    else Modifier
                )
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = tag.name,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (selected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface
        )
        IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
            Icon(Icons.Default.Edit, contentDescription = "Modifica",
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
            Icon(Icons.Default.Delete, contentDescription = "Elimina",
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.error)
        }
    }
}
