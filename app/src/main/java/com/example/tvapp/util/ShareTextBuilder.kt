package com.example.tvapp.util

import com.example.tvapp.data.model.Show
import kotlin.text.isNotBlank
import kotlin.text.isNullOrBlank

/**
 * Builds the plain-text body used for the Share action on the Detail
 * screen: title, then the HTML-stripped summary, then the show's URL
 * (each part on its own paragraph, and only included if present).
 */
fun buildShareText(show: Show): String {
    return buildString {
        append(show.name)

        val summary = stripHtml(show.summary)
        if (summary.isNotBlank()) {
            append("\n\n")
            append(summary)
        }

        if (!show.url.isNullOrBlank()) {
            append("\n\n")
            append(show.url)
        }
    }
}
