package com.example.tvapp.util

import com.example.tvapp.data.model.Show
import kotlin.collections.filter
import kotlin.text.contains
import kotlin.text.isEmpty
import kotlin.text.trim

/**
 * Filters an already-fetched list of shows by title, case-insensitively.
 *
 * This is intentionally a pure, local (client-side) filter over the list the
 * app already loaded from GET /shows?page=0 — it does NOT call the network
 * again and does NOT change how shows are fetched. TVMaze's public API has
 * no "search within a single page" endpoint that fits this app's scope, so
 * filtering the already-fetched page locally is the correct, low-risk choice
 * here (its dedicated /search/shows endpoint searches the whole catalog, not
 * this page, and would require separate wiring outside this task's scope).
 *
 * Kept outside the ViewModel as a plain function so it can be unit tested
 * without touching coroutines, StateFlow, or the repository at all.
 */
fun filterShows(shows: List<Show>, query: String): List<Show> {
    val trimmedQuery = query.trim()
    if (trimmedQuery.isEmpty()) return shows

    return shows.filter { show ->
        show.name.contains(trimmedQuery, ignoreCase = true)
    }
}
