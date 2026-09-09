package com.example.tvapp

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Uses UnconfinedTestDispatcher (not StandardTestDispatcher) on purpose:
 * ShowListViewModel kicks off loadShows() from its init{} block, so the
 * coroutine needs to run eagerly for the ViewModel to already be in its
 * final state by the time a test reads `uiState.value`, without every
 * test having to manually call `advanceUntilIdle()`.
 */
@ExperimentalCoroutinesApi
class MainDispatcherRule(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {

    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}
