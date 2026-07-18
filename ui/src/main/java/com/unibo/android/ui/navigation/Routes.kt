package com.unibo.android.ui.navigation

object Routes {
    const val CALENDAR = "calendario"

    const val BOARD_LIST = "bacheche"

    const val BOARD = "bacheche/{boardId}/{boardTitle}"
    fun board(boardId: Long, boardTitle: String) = "bacheche/$boardId/$boardTitle"
}
