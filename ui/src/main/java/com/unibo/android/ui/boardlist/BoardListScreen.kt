package com.unibo.android.ui.boardlist

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.unibo.android.domain.models.BoardModel
import com.unibo.android.ui.board.dialogs.BoardDialog
import com.unibo.android.ui.components.Sidebar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoardListScreen(
    onBoardClick: (BoardModel) -> Unit,
    onNavigateToCalendar: () -> Unit = {},
    onToggleTheme: () -> Unit = {},
    isDark: Boolean = false,
    vm: BoardListViewModel = viewModel()
) {
    val boards by vm.boards.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var editingBoard by remember { mutableStateOf<BoardModel?>(null) }
    var showDrawer by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Bacheche") },
                    navigationIcon = {
                        IconButton(onClick = { showDrawer = true }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        IconButton(onClick = onToggleTheme) {
                            Icon(
                                imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = if (isDark) "Tema chiaro" else "Tema scuro"
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { showDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Nuova bacheca")
                }
            }
        ) { padding ->
            if (boards.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Nessuna bacheca. Creane una con il +",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(boards, key = { it.id }) { board ->
                        BoardCard(
                            board = board,
                            onClick = { onBoardClick(board) },
                            onLongClick = { editingBoard = board }
                        )
                    }
                }
            }
        }

        if (showDialog) {
            BoardDialog(
                onConfirm = { title, desc, coverUrl ->
                    vm.createBoard(title, desc, coverUrl)
                    showDialog = false
                },
                onDismiss = { showDialog = false }
            )
        }

        editingBoard?.let { board ->
            BoardDialog(
                initialTitle = board.title,
                initialDescription = board.description,
                initialCoverImageUrl = board.coverImageUrl,
                isEditing = true,
                onConfirm = { title, desc, coverUrl ->
                    vm.updateBoard(board, title, desc, coverUrl)
                    editingBoard = null
                },
                onDismiss = { editingBoard = null },
                onDelete = {
                    vm.deleteBoard(board)
                    editingBoard = null
                }
            )
        }

        Sidebar(
            visible = showDrawer,
            onClose = { showDrawer = false },
            onToggleTheme = onToggleTheme,
            isDark = isDark,
            isBoardsSelected = true,
            onNavigateToCalendar = onNavigateToCalendar
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BoardCard(board: BoardModel, onClick: () -> Unit, onLongClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
    ) {
        Column {
            if (board.coverImageUrl != null) {
                AsyncImage(
                    model = board.coverImageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Text(board.title, style = MaterialTheme.typography.titleMedium)
                if (board.description.isNotBlank()) {
                    Text(
                        board.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}