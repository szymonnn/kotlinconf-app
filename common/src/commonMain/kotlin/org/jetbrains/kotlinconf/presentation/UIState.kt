package org.jetbrains.kotlinconf.presentation

import org.jetbrains.kotlinconf.*

/**
 * Session displayed in single schedule group.
 */
class SessionGroup(
    val groupName: String,
    val sessions: List<SessionCard>
)

/**
 * All data to display session cards.
 */
data class SessionCard(
    val session: SessionData,
    val speakers: List<SpeakerData>,
    val isFavorite: Boolean,
    val ratingData: RatingData?,
    val isLive: Boolean
)
