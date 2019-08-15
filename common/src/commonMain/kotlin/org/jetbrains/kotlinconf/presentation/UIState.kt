package org.jetbrains.kotlinconf.presentation

import org.jetbrains.kotlinconf.*

/**
 * Session displayed in single schedule group.
 */
class SessionGroup(
    val month: String,
    val day: Int,
    val time: String,
    val sessions: List<SessionCard>
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
