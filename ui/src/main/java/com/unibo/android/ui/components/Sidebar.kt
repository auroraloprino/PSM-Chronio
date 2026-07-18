package com.unibo.android.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unibo.android.domain.models.BoardModel

@Composable
fun Sidebar(
    visible: Boolean,
    onClose: () -> Unit,
    onToggleTheme: () -> Unit = {},
    isDark: Boolean = false,
    isBoardsSelected: Boolean = false,
    onNavigateToCalendar: () -> Unit = {},
    onNavigateToBoards: () -> Unit = {},
    onNavigateToBoard: (BoardModel) -> Unit = {},
    vm: SidebarViewModel = viewModel()
) {
    val boards by vm.boards.collectAsState()
    var boardsExpanded by remember { mutableStateOf(false) }
    if (visible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.4f))
                .clickable { onClose() }
        )
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally { -it },
        exit = slideOutHorizontally { -it }
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .width(280.dp)
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Text("Chronio", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(16.dp))
            NavigationDrawerItem(
                icon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
                label = { Text("Calendario") },
                selected = !isBoardsSelected,
                onClick = {
                    onClose()
                    onNavigateToCalendar()
                }
            )
            NavigationDrawerItem(
                icon = { Icon(Icons.Default.Dashboard, contentDescription = null) },
                label = { Text("Bacheche") },
                badge = {
                    if (boards.isNotEmpty()) {
                        Icon(
                            imageVector = if (boardsExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (boardsExpanded) "Comprimi" else "Espandi",
                            modifier = Modifier.clickable { boardsExpanded = !boardsExpanded }
                        )
                    }
                },
                selected = isBoardsSelected,
                onClick = {
                    onClose()
                    onNavigateToBoards()
                },
            )

            if (boardsExpanded) {
                LazyColumn {
                    items(boards, key = { it.id }) { board ->
                        NavigationDrawerItem(
                            icon = {},
                            label = {
                                Text(
                                    board.title,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            selected = false,
                            onClick = {
                                onClose()
                                onNavigateToBoard(board)
                            },
                            modifier = Modifier.padding(start = 24.dp)
                        )
                    }
                }
            }
        }
    }
}
