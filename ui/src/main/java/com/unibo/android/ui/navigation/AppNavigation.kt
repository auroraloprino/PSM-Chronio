package com.unibo.android.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.unibo.android.ui.board.BoardScreen
//import com.unibo.android.ui.calendar.CalendarScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.CALENDAR) {
        /*composable(Routes.CALENDAR) {
            CalendarScreen()
        }*/


        composable(
            route = Routes.BOARD,
            arguments = listOf(navArgument("boardId") { type = NavType.LongType })
        ) { backStackEntry ->
            val boardId = backStackEntry.arguments?.getLong("boardId") ?: 0L
            BoardScreen(boardId = boardId)
        }
    }
}
