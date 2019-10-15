package org.jetbrains.kotlinconf

import io.ktor.util.date.*

/**
 * Conference start time in GMT.
 */
val CONFERENCE_START = GMTDate(
    0, 0, 7, 4, Month.DECEMBER, 2019
)

/**
 * Conference end time in GMT.
 */
val CONFERENCE_END = GMTDate(
    1, 0, 0, 7, Month.DECEMBER, 2019
)

/**
 * Conference location timezone offset in millis.
 */
val TIMEZONE_OFFSET = 1 * 60 * 60 * 1000L

class Timestamp(val days: Int, val hours: Int, val minutes: Int, val seconds: Int)