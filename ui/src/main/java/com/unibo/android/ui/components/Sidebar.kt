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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Sidebar(
    visible: Boolean,
    onClose: () -> Unit,
    onToggleTheme: () -> Unit = {},
    isDark: Boolean = false,
    isBoardsSelected: Boolean = false,
    onNavigateToCalendar: () -> Unit = {},
    onNavigateToBoards: () -> Unit = {}
) {
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
                selected = isBoardsSelected,
                onClick = {
                    onClose()
                    onNavigateToBoards()
                },
            )
        }
    }
}
