package com.example.tvapp.data.repository

import com.example.tvapp.data.model.Show
import com.example.tvapp.data.network.TvMazeApiService

class ShowRepositoryImpl(
    private val api: TvMazeApiService
) : ShowRepository {

    override suspend fun getShows(page: Int): List<Show> = api.getShows(page)

    override suspend fun getShowDetail(id: Int): Show = api.getShowDetail(id)
}
