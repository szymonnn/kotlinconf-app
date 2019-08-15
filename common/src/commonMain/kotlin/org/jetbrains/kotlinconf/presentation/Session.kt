package org.jetbrains.kotlinconf.presentation

import org.jetbrains.kotlinconf.*


interface SessionView : BaseView {
}

/**
 * Single session presenter.
 * Keep separate local values for favorite and rating.
 */
class SessionPresenter(
    private val view: SessionView
) : BasePresenter(view) {
    /**
     * Handle rating button event.
     *
     * Change offline and revert if submit attempt failed.
     */
    fun voteTouch(sessionId: String, rating: RatingData) {
        ConferenceService.vote(sessionId, rating)
    }

    /**
     * Handle favorite button event.
     *
     * Change offline and revert if submit attempt failed.
     */
    fun favoriteTouch(sessionId: String) {
        ConferenceService.markFavorite(sessionId)
    }
}
