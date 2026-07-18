package com.unibo.android.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.unibo.android.data.local.db.ChronioDatabase
import com.unibo.android.data.local.entity.BoardEntity
import com.unibo.android.data.local.entity.CardEntity
import com.unibo.android.data.local.entity.ColumnEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class PersistenceInstrumentedTest {

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val openedDbNames = mutableListOf<String>()

    private fun openDb(name: String): ChronioDatabase {
        openedDbNames += name
        return Room.databaseBuilder(context, ChronioDatabase::class.java, name).build()
    }

    @After
    fun tearDown() {
        openedDbNames.forEach { context.deleteDatabase(it) }
    }

    @Test
    fun cardMove_persistsInCorrectPositionAcrossRestart() = runBlocking {
        val dbName = "test_card_move.db"
        var db = openDb(dbName)

        val boardId = db.boardDao().insert(BoardEntity(title = "Board"))
        val colAId = db.columnDao().insert(ColumnEntity(title = "A", position = 0, boardId = boardId))
        val colBId = db.columnDao().insert(ColumnEntity(title = "B", position = 1, boardId = boardId))
        val cardId = db.cardDao().insert(CardEntity(title = "Card", columnId = colAId, position = 0))

        db.cardDao().moveToColumn(cardId, colBId, 0)

        db.close()

        db = openDb(dbName)

        val cardsInB = db.cardDao().observeForColumn(colBId).first()
        val cardsInA = db.cardDao().observeForColumn(colAId).first()

        assertEquals(1, cardsInB.size)
        assertEquals(colBId, cardsInB.first().card.columnId)
        assertEquals(0, cardsInB.first().card.position)
        assertTrue(cardsInA.isEmpty())

        db.close()
    }

    @Test
    fun boardsAndColumns_restoreCorrectlyAcrossRestart() = runBlocking {
        val dbName = "test_boards_columns_restart.db"
        var db = openDb(dbName)

        val board1Id = db.boardDao().insert(BoardEntity(title = "Lavoro", description = "Board di lavoro"))
        val board2Id = db.boardDao().insert(BoardEntity(title = "Casa"))

        db.columnDao().insert(ColumnEntity(title = "Da fare", position = 0, boardId = board1Id))
        db.columnDao().insert(ColumnEntity(title = "In corso", position = 1, boardId = board1Id))
        db.columnDao().insert(ColumnEntity(title = "Fatto", position = 2, boardId = board1Id))
        db.columnDao().insert(ColumnEntity(title = "Spesa", position = 0, boardId = board2Id))

        db.close()

        db = openDb(dbName)

        val boards = db.boardDao().observeAll().first()
        assertEquals(2, boards.size)
        assertTrue(boards.any { it.id == board1Id && it.title == "Lavoro" })
        assertTrue(boards.any { it.id == board2Id && it.title == "Casa" })

        val board1Columns = db.columnDao().observeForBoard(board1Id).first()
        assertEquals(listOf("Da fare", "In corso", "Fatto"), board1Columns.map { it.title })

        val board2Columns = db.columnDao().observeForBoard(board2Id).first()
        assertEquals(listOf("Spesa"), board2Columns.map { it.title })

        db.close()
    }

    @Test
    fun deletingColumn_cascadesToRemoveOnlyItsOwnCards() = runBlocking {
        val dbName = "test_delete_column_cascade.db"
        val db = openDb(dbName)

        val boardId = db.boardDao().insert(BoardEntity(title = "Board"))
        val columnId = db.columnDao().insert(ColumnEntity(title = "Colonna", position = 0, boardId = boardId))
        val otherColumnId = db.columnDao().insert(ColumnEntity(title = "Altra colonna", position = 1, boardId = boardId))

        db.cardDao().insert(CardEntity(title = "Card 1", columnId = columnId, position = 0))
        db.cardDao().insert(CardEntity(title = "Card 2", columnId = columnId, position = 1))
        val untouchedCardId = db.cardDao().insert(CardEntity(title = "Card altrove", columnId = otherColumnId, position = 0))

        db.columnDao().delete(ColumnEntity(id = columnId, title = "Colonna", position = 0, boardId = boardId))

        val remainingInDeletedColumn = db.cardDao().observeForColumn(columnId).first()
        assertTrue(remainingInDeletedColumn.isEmpty())

        val remainingInOtherColumn = db.cardDao().observeForColumn(otherColumnId).first()
        assertEquals(1, remainingInOtherColumn.size)
        assertEquals(untouchedCardId, remainingInOtherColumn.first().card.id)

        db.close()
    }
}
