package com.unibo.android.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.unibo.android.ui.board.BoardScreen
import com.unibo.android.ui.calendar.CalendarScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.CALENDAR) {
        composable(Routes.CALENDAR) {
            CalendarScreen()
        }
    }
}
