package org.jetbrains.kotlinconf.presentation

import io.ktor.util.date.*
import org.jetbrains.kotlinconf.*

/**
 * Session displayed in single schedule group.
 */
class SessionGroup(
    val month: String,
    val day: Int,
    val title: String,
    val startsAt: GMTDate,
    val sessions: List<SessionCard>,
    val daySection: Boolean = false,
    val coffeeBreak: Boolean = false
)

/**
 * All data to display session cards.
 */
data class SessionCard(
    val session: SessionData,
    val time: String,
    val location: RoomData,
    val speakers: List<SpeakerData>,
    val isFavorite: Observable<Boolean>,
    val ratingData: Observable<RatingData?>,
    val isLive: Observable<Boolean>
)
