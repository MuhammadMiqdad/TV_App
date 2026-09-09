package com.example.tvapp.data.network

import com.example.tvapp.data.model.Show
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TvMazeApiService {

    // GET https://api.tvmaze.com/shows?page=0
    @GET("shows")
    suspend fun getShows(@Query("page") page: Int = 0): List<Show>

    // GET https://api.tvmaze.com/shows/{id}
    @GET("shows/{id}")
    suspend fun getShowDetail(@Path("id") id: Int): Show
}
