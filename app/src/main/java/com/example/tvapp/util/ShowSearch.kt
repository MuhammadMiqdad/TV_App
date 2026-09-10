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
 * again and does NOT change how shows are fetched.
 */
fun filterShows(shows: List<Show>, query: String): List<Show> {
    val trimmedQuery = query.trim()
    if (trimmedQuery.isEmpty()) return shows

    return shows.filter { show ->
        show.name.contains(trimmedQuery, ignoreCase = true)
    }
}
