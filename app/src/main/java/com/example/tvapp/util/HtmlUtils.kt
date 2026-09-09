package com.example.tvapp.util

import kotlin.text.isNullOrBlank
import kotlin.text.replace
import kotlin.text.trim

/**
 * TVMaze's `summary` field contains simple HTML (<p>, <b>, <i>, ...).
 * This strips tags and unescapes the handful of entities that show up
 * in practice, returning plain text suitable for a Compose Text().
 *
 * Deliberately not using android.text.Html/HtmlCompat here: those wrap
 * the Android framework's Html class, which isn't available in plain
 * JVM unit tests (src/test) without Robolectric. A small regex-based
 * stripper keeps this testable with plain JUnit and is more than
 * enough for TVMaze's fairly simple summary markup.
 */
fun stripHtml(html: String?): String {
    if (html.isNullOrBlank()) return ""

    return html
        .replace(Regex("<[^>]+>"), " ")
        .replace("&amp;", "&")
        .replace("&quot;", "\"")
        .replace("&#39;", "'")
        .replace("&apos;", "'")
        .replace("&lt;", "<")
        .replace("&gt;", ">")
        .replace(Regex("\\s+"), " ")
        .trim()
}
