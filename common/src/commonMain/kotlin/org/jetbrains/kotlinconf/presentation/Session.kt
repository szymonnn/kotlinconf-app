package org.jetbrains.kotlinconf.presentation

import org.jetbrains.kotlinconf.*


interface SessionView : BaseView {
    fun onSession(session: SessionData, speakers: List<SpeakerData>, isFavorite: Boolean, rating: RatingData?)
    fun onVoteChange(rating: RatingData?)
    fun onUpdateFavorite(isFavorite: Boolean)
}

/**
 * Single session presenter.
 * Keep separate local values for favorite and rating.
 */
class SessionPresenter(
    private val view: SessionView
) : BasePresenter(view) {
    private var currentSession: String? = null

    init {
        with(ConferenceService) {
            favorites.onChange {
                view.onUpdateFavorite(currentSession in it)
            }

            votes.onChange {
                view.onVoteChange(it[currentSession!!])
            }
        }
    }

    /**
     * Display session info.
     */
    fun showSession(sessionId: String) {
        with(ConferenceService) {
            val session = session(sessionId)
            val speakers = sessionSpeakers(sessionId)
            val isFavorite = sessionIsFavorite(sessionId)
            val rating = sessionRating(sessionId)

            currentSession = sessionId
            view.onSession(session, speakers, isFavorite, rating)
        }
    }

    /**
     * Handle rating button event.
     *
     * Change offline and revert if submit attempt failed.
     */
    fun voteTouch(rating: RatingData) {
        ConferenceService.vote(currentSession!!, rating)
    }

    /**
     * Handle favorite button event.
     *
     * Change offline and revert if submit attempt failed.
     */
    fun favoriteTouch() {
        with(ConferenceService) {
            val id = currentSession!!
            markFavorite(id, !sessionIsFavorite(id))
        }
    }
}
