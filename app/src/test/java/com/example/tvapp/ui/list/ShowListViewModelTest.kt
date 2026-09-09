package com.example.tvapp.ui.list

import com.example.tvapp.MainDispatcherRule
import com.example.tvapp.data.model.Show
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ShowListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val dummyShows = listOf(
        Show(id = 1, url = null, name = "Under the Dome", premiered = "2013-06-24", rating = null, image = null, summary = null),
        Show(id = 2, url = null, name = "Person of Interest", premiered = "2011-09-22", rating = null, image = null, summary = null)
    )

    @Test
    fun `loadShows success updates uiState to Success with the fetched shows`() = runTest {
        // Arrange
        val viewModel = ShowListViewModel(repository = FakeShowRepository(showsToReturn = dummyShows))

        // Act: init{} already triggers loadShows(); runTest drains the coroutine.

        // Assert
        val state = viewModel.uiState.value
        assertTrue(state is ShowListUiState.Success)
        assertEquals(dummyShows, (state as ShowListUiState.Success).shows)
    }

    @Test
    fun `loadShows failure updates uiState to Error`() = runTest {
        // Arrange
        val viewModel = ShowListViewModel(repository = FakeShowRepository(shouldThrow = true))

        // Assert
        val state = viewModel.uiState.value
        assertTrue(state is ShowListUiState.Error)
    }

    @Test
    fun `retry after a failed load can recover to Success`() = runTest {
        // Arrange: repository starts failing (simulates the initial load erroring out)
        val fakeRepository = FakeShowRepository(showsToReturn = dummyShows, shouldThrow = true)
        val viewModel = ShowListViewModel(repository = fakeRepository)
        assertTrue(viewModel.uiState.value is ShowListUiState.Error)

        // Act: user taps Retry, and this time the "network" succeeds
        fakeRepository.shouldThrow = false
        viewModel.loadShows()

        // Assert
        val state = viewModel.uiState.value
        assertTrue(state is ShowListUiState.Success)
        assertEquals(dummyShows, (state as ShowListUiState.Success).shows)
    }
}
