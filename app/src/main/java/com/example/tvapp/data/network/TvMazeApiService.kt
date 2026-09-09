package com.example.tvapp.data.network

import com.example.tvapp.data.model.Show
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TvMazeApiService {

    // GET https://api.tvmaze.com/shows?page=0
    @GET("shows")
    suspend fun getShows(@Query("page") page: Int = 0): List<Show>

    // GET https://api.tvmaze.com/shows/{id}?embed[]=cast&embed[]=episodes
    // Embedding cast + episodes in the same request (rather than 2 extra calls) is well within the
    // ~20 calls/10s rate limit and keeps the
    @GET("shows/{id}")
    suspend fun getShowDetail(
        @Path("id") id: Int,
        @Query("embed[]") embed: List<String> = listOf("cast", "episodes")
    ): Show
}
