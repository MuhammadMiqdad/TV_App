package com.example.tvapp.ui.detail

import com.example.tvapp.MainDispatcherRule
import com.example.tvapp.data.model.Show
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ShowDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val dummyShow = Show(
        id = 1,
        url = null,
        name = "Under the Dome",
        premiered = "2013-06-24",
        rating = null,
        image = null,
        summary = null
    )

    @Test
    fun `loadShowDetail success updates uiState to Success with the fetched show`() = runTest {
        // Arrange
        val viewModel = ShowDetailViewModel(
            showId = 1,
            repository = FakeDetailShowRepository(showToReturn = dummyShow)
        )

        // Assert (init{} already triggered the load)
        val state = viewModel.uiState.value
        assertTrue(state is ShowDetailUiState.Success)
        assertEquals(dummyShow, (state as ShowDetailUiState.Success).show)
    }

    @Test
    fun `loadShowDetail failure updates uiState to Error`() = runTest {
        // Arrange
        val viewModel = ShowDetailViewModel(
            showId = 1,
            repository = FakeDetailShowRepository(shouldThrow = true)
        )

        // Assert
        val state = viewModel.uiState.value
        assertTrue(state is ShowDetailUiState.Error)
    }
}
