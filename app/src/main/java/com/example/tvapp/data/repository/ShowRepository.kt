package com.example.tvapp.data.repository

import com.example.tvapp.data.model.Show

interface ShowRepository {
    suspend fun getShows(page: Int = 0): List<Show>
    suspend fun getShowDetail(id: Int): Show
}
