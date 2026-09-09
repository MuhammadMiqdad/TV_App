package com.example.tvapp.ui.detail

import com.example.tvapp.data.model.Show
import com.example.tvapp.data.repository.ShowRepository
import java.io.IOException

/**
 * Only used by Detail screen tests, so getShows() is a minimal stub
 * just to satisfy the ShowRepository contract.
 */
class FakeDetailShowRepository(
    private val showToReturn: Show? = null,
    var shouldThrow: Boolean = false
) : ShowRepository {

    override suspend fun getShows(page: Int): List<Show> {
        throw NotImplementedError("Not used by detail screen tests")
    }

    override suspend fun getShowDetail(id: Int): Show {
        if (shouldThrow) throw IOException("Simulated network failure")
        return showToReturn ?: error("showToReturn was not set for this test")
    }
}
