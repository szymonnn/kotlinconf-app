package org.jetbrains.kotlinconf

import io.ktor.util.date.*

/**
 * Conference start time in GMT.
 */
val CONFERENCE_START = GMTDate(
    0, 0, 7, 4, Month.DECEMBER, 2019
)

val TIMEZONE_OFFSET = 1 * 60 * 60 * 1000L
