package com.example.tvapp.util

import com.example.tvapp.data.model.Episode
import kotlin.collections.count
import kotlin.collections.distinct
import kotlin.collections.isNullOrEmpty
import kotlin.collections.mapNotNull

fun seasonEpisodeSummary(episodes: List<Episode>?): String? {
    if (episodes.isNullOrEmpty()) return null

    val seasonCount = episodes.mapNotNull { it.season }.distinct().count()
    val episodeCount = episodes.size

    if (seasonCount == 0 && episodeCount == 0) return null

    val seasonLabel = if (seasonCount == 1) "Season" else "Seasons"
    val episodeLabel = if (episodeCount == 1) "Episode" else "Episodes"

    return "$seasonCount $seasonLabel • $episodeCount $episodeLabel"
}
