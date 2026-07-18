package com.unibo.android.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.unibo.android.ui.board.BoardScreen
import com.unibo.android.ui.boardlist.BoardListScreen
import com.unibo.android.ui.calendar.CalendarScreen

@Composable
fun AppNavigation(onToggleTheme: () -> Unit = {}, isDark: Boolean = false) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.CALENDAR) {
        composable(Routes.CALENDAR) {
            CalendarScreen(
                onToggleTheme = onToggleTheme,
                isDark = isDark,
                onNavigateToBoards = { navController.navigate(Routes.BOARD_LIST) },
                onNavigateToBoard = { board -> navController.navigate(Routes.board(board.id, board.title)) }
            )
        }

        composable(Routes.BOARD_LIST) {
            BoardListScreen(
                onBoardClick = { board ->
                    navController.navigate(Routes.board(board.id, board.title))
                },
                onNavigateToCalendar = { navController.popBackStack() },
                onToggleTheme = onToggleTheme,
                isDark = isDark
            )
        }

        composable(
            route = Routes.BOARD,
            arguments = listOf(
                navArgument("boardId") { type = NavType.LongType },
                navArgument("boardTitle") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val boardId = backStackEntry.arguments?.getLong("boardId") ?: 0L
            val boardTitle = backStackEntry.arguments?.getString("boardTitle") ?: ""
            BoardScreen(
                boardId = boardId,
                boardTitle = boardTitle,
                onBack = { navController.popBackStack() },
                onToggleTheme = onToggleTheme,
                isDark = isDark
            )
        }
    }
}
