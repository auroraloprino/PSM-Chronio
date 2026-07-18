package com.unibo.android.domain

import com.unibo.android.domain.models.EventModel
import com.unibo.android.domain.repositories.EventRepository
import com.unibo.android.domain.usecases.DeleteEventUseCaseImpl
import com.unibo.android.domain.usecases.GetEventsUseCaseImpl
import com.unibo.android.domain.usecases.SaveEventUseCaseImpl
import com.unibo.android.domain.usecases.UpdateEventUseCaseImpl
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

private val EVENT_A = EventModel(id = 1, title = "Evento A", startTime = 1000L, endTime = 2000L)
private val EVENT_B = EventModel(id = 2, title = "Evento B", startTime = 3000L, endTime = 4000L, allDay = true)

class FakeEventRepository : EventRepository {
    private val events = mutableListOf<EventModel>()
    private val tags = mutableMapOf<Long, List<Long>>()
    private var nextId = 1L

    override suspend fun getAll() = events.toList()
    override suspend fun getInRange(startMs: Long, endMs: Long) =
        events.filter { it.startTime >= startMs && it.startTime <= endMs }
    override suspend fun getById(id: Long) = events.find { it.id == id }
    override suspend fun save(event: EventModel): Long {
        val id = nextId++
        events.add(event.copy(id = id))
        return id
    }
    override suspend fun update(event: EventModel) {
        val index = events.indexOfFirst { it.id == event.id }
        if (index >= 0) events[index] = event
    }
    override suspend fun delete(event: EventModel) {
        events.removeAll { it.id == event.id }
        tags.remove(event.id)
    }
    override suspend fun setTags(eventId: Long, tagIds: List<Long>) {
        tags[eventId] = tagIds
    }
    fun tagsFor(eventId: Long) = tags[eventId] ?: emptyList()
}

class EventUseCaseTest {

    private lateinit var repo: FakeEventRepository
    private lateinit var save: SaveEventUseCaseImpl
    private lateinit var getAll: GetEventsUseCaseImpl
    private lateinit var update: UpdateEventUseCaseImpl
    private lateinit var delete: DeleteEventUseCaseImpl

    @Before
    fun setup() {
        repo = FakeEventRepository()
        save = SaveEventUseCaseImpl(repo)
        getAll = GetEventsUseCaseImpl(repo)
        update = UpdateEventUseCaseImpl(repo)
        delete = DeleteEventUseCaseImpl(repo)
    }

    @Test
    fun `salva evento e lo recupera`() = runTest {
        save(EVENT_A, emptyList())
        val all = getAll()
        assertEquals(1, all.size)
        assertEquals("Evento A", all.first().title)
    }

    @Test
    fun `salva evento con tag`() = runTest {
        val id = save(EVENT_A, listOf(10L, 20L))
        assertEquals(listOf(10L, 20L), repo.tagsFor(id))
    }

    @Test
    fun `aggiorna titolo evento`() = runTest {
        val id = save(EVENT_A, emptyList())
        val updated = EVENT_A.copy(id = id, title = "Titolo aggiornato")
        update(updated, emptyList())
        assertEquals("Titolo aggiornato", getAll().first().title)
    }

    @Test
    fun `aggiorna evento con titolo vuoto fallisce`() = runTest {
        val id = save(EVENT_A, emptyList())
        val result = update(EVENT_A.copy(id = id, title = ""), emptyList())
        assertTrue(result.isFailure)
    }

    @Test
    fun `elimina evento`() = runTest {
        val id = save(EVENT_A, emptyList())
        delete(EVENT_A.copy(id = id))
        assertTrue(getAll().isEmpty())
    }

    @Test
    fun `salva piu eventi e li recupera tutti`() = runTest {
        save(EVENT_A, emptyList())
        save(EVENT_B, emptyList())
        assertEquals(2, getAll().size)
    }

    @Test
    fun `evento allDay salvato correttamente`() = runTest {
        save(EVENT_B, emptyList())
        val saved = getAll().first()
        assertTrue(saved.allDay)
    }
}
