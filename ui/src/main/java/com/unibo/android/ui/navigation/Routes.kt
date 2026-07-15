package com.unibo.android.ui.navigation

object Routes {
    const val CALENDAR = "calendario"

    const val BOARD = "bacheche/{boardId}"
    fun board(boardId: Long) = "bacheche/$boardId"
}