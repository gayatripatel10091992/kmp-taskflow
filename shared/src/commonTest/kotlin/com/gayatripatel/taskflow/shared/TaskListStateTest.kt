package com.gayatripatel.taskflow.shared

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Runs on JVM, Android (unit) and iOS simulator — the same assertions on every platform. */
class TaskListStateTest {

    private val today = LocalDate(2026, 11, 23)
    private val tasks = SampleTasks.create(today)

    @Test
    fun stats_countDoneOverdueAndToday() {
        val state = TaskListState.from(tasks, TaskFilter.ALL, "", today)
        assertEquals(8, state.stats.total)
        assertEquals(2, state.stats.done)
        assertEquals(1, state.stats.overdue)
        assertEquals(1, state.stats.dueToday)
        assertEquals(0.25f, state.stats.progress)
    }

    @Test
    fun activeFilter_hidesDoneTasks_andSortsByPriorityThenDate() {
        val state = TaskListState.from(tasks, TaskFilter.ACTIVE, "", today)
        assertTrue(state.visibleTasks.none { it.isDone })
        assertEquals(listOf(1L, 2L, 3L, 4L, 5L, 6L), state.visibleTasks.map { it.id })
    }

    @Test
    fun query_matchesTitleOrTag_caseInsensitive() {
        val state = TaskListState.from(tasks, TaskFilter.ALL, "  kmp ", today)
        assertEquals(listOf(4L), state.visibleTasks.map { it.id })
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun store_addToggleDelete() = runTest(UnconfinedTestDispatcher()) {
        val store = TaskListStore(InMemoryTaskRepository(), { today }, backgroundScope)

        store.dispatch(TaskAction.Add("  Ship release  ", Priority.HIGH, today, tag = " "))
        testScheduler.advanceUntilIdle()
        val added = store.state.value.visibleTasks.single()
        assertEquals("Ship release", added.title)
        assertEquals(null, added.tag)

        store.dispatch(TaskAction.Toggle(added.id))
        testScheduler.advanceUntilIdle()
        assertEquals(1, store.state.value.stats.done)

        store.dispatch(TaskAction.Delete(added.id))
        testScheduler.advanceUntilIdle()
        assertEquals(0, store.state.value.stats.total)
    }

    @Test
    fun blankTitle_isIgnored() = runTest {
        val store = TaskListStore(InMemoryTaskRepository(), { today }, backgroundScope)
        store.dispatch(TaskAction.Add("   ", Priority.LOW, null, null))
        testScheduler.advanceUntilIdle()
        assertEquals(0, store.state.value.stats.total)
    }
}
