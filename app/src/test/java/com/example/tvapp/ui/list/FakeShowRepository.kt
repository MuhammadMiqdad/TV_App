package com.example.tvapp.ui.list

import com.example.tvapp.data.model.Show
import com.example.tvapp.data.repository.ShowRepository
import java.io.IOException

class FakeShowRepository(
    private val showsToReturn: List<Show> = emptyList(),
    var shouldThrow: Boolean = false
) : ShowRepository {

    override suspend fun getShows(page: Int): List<Show> {
        if (shouldThrow) throw IOException("Simulated network failure")
        return showsToReturn
    }

    override suspend fun getShowDetail(id: Int): Show {
        throw NotImplementedError("Not used by list screen tests")
    }
}
