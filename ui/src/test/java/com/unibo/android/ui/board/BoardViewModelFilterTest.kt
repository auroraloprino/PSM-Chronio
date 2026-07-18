package com.unibo.android.ui.board

import com.unibo.android.domain.di.UseCasesProvider
import com.unibo.android.domain.models.BoardTagModel
import com.unibo.android.domain.models.CardModel
import com.unibo.android.domain.models.ColumnModel
import com.unibo.android.domain.usecases.GetBoardTagsUseCase
import com.unibo.android.domain.usecases.GetCardsUseCase
import com.unibo.android.domain.usecases.GetColumnsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BoardViewModelFilterTest {

    private val testDispatcher = StandardTestDispatcher()

    private val urgentTag = BoardTagModel(id = 1, name = "Urgente")
    private val bugTag = BoardTagModel(id = 2, name = "Bug")

    private val columnId = 10L

    private val cardWithUrgent =
        CardModel(id = 100, title = "A", columnId = columnId, position = 0, tags = listOf(urgentTag))
    private val cardWithBug =
        CardModel(id = 101, title = "B", columnId = columnId, position = 1, tags = listOf(bugTag))
    private val cardWithBoth =
        CardModel(id = 102, title = "C", columnId = columnId, position = 2, tags = listOf(urgentTag, bugTag))
    private val cardWithoutTags =
        CardModel(id = 103, title = "D", columnId = columnId, position = 3)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        UseCasesProvider.getColumnsUseCase = FakeGetColumnsUseCase(
            listOf(ColumnModel(id = columnId, title = "Da fare", position = 0, boardId = 1L))
        )
        UseCasesProvider.getCardsUseCase = FakeGetCardsUseCase(
            mapOf(columnId to listOf(cardWithUrgent, cardWithBug, cardWithBoth, cardWithoutTags))
        )
        UseCasesProvider.getBoardTagsUseCase = FakeGetBoardTagsUseCase(listOf(urgentTag, bugTag))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun tagFilter_showsOnlyMatchingCards() = runTest(testDispatcher) {
        val vm = BoardViewModel(boardId = 1L)
        val collectJob = launch { vm.uiState.collect {} }
        advanceUntilIdle()

        assertEquals(4, vm.uiState.value.columns.single().cards.size)
        assertFalse(vm.uiState.value.isFiltering)

        vm.toggleFilter(urgentTag.id)
        advanceUntilIdle()
        assertEquals(
            setOf(cardWithUrgent.id, cardWithBoth.id),
            vm.uiState.value.columns.single().cards.map { it.id }.toSet()
        )

        vm.toggleFilter(bugTag.id)
        advanceUntilIdle()
        assertEquals(
            setOf(cardWithUrgent.id, cardWithBug.id, cardWithBoth.id),
            vm.uiState.value.columns.single().cards.map { it.id }.toSet()
        )

        vm.setFilterMode(FilterMode.AND)
        advanceUntilIdle()
        assertEquals(
            setOf(cardWithBoth.id),
            vm.uiState.value.columns.single().cards.map { it.id }.toSet()
        )

        vm.clearFilters()
        advanceUntilIdle()
        assertEquals(4, vm.uiState.value.columns.single().cards.size)
        assertFalse(vm.uiState.value.isFiltering)

        collectJob.cancel()
    }
}

private class FakeGetColumnsUseCase(private val columns: List<ColumnModel>) : GetColumnsUseCase {
    override fun invoke(boardId: Long): Flow<List<ColumnModel>> = flowOf(columns)
}

private class FakeGetCardsUseCase(
    private val cardsByColumn: Map<Long, List<CardModel>>
) : GetCardsUseCase {
    override fun invoke(columnId: Long): Flow<List<CardModel>> = flowOf(cardsByColumn[columnId] ?: emptyList())
}

private class FakeGetBoardTagsUseCase(private val tags: List<BoardTagModel>) : GetBoardTagsUseCase {
    override fun invoke(): Flow<List<BoardTagModel>> = flowOf(tags)
}
