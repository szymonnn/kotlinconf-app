package org.jetbrains.kotlinconf.presentation

import kotlinx.coroutines.*
import org.jetbrains.kotlinconf.*
import org.jetbrains.kotlinconf.api.*
import org.jetbrains.kotlinconf.data.*

interface SessionDetailsView : BaseView {
    fun updateRating(rating: RatingData?)
    fun setRatingClickable(clickable: Boolean)
    fun updateFavorite(isFavorite: Boolean)
}

/**
 * Single session presenter.
 * Keep separate local values for favorite and rating.
 */
class SessionDetailsPresenter(
    private val view: SessionDetailsView,
    private val session: Session,
    private val service: ConferenceService
) : BasePresenter(view) {

    /**
     * Handle rating button event.
     *
     * Change offline and revert if submit attempt failed.
     */
    fun onRatingButtonClicked(clicked: RatingData) {
        launch {
            view.setRatingClickable(false)
            view.updateRating(clicked)
            service.setRating(session, clicked)
        }.invokeOnCompletion {
            if (it != null) {
                view.showError(it)
                view.updateRating(session.rating)
            }

            view.setRatingClickable(true)
        }
    }

    /**
     * Handle favorite button event.
     *
     * Change offline and revert if submit attempt failed.
     */
    fun onFavoriteButtonClicked() {
        launch {
            val newRating = !session.isFavorite
            view.updateFavorite(newRating)
            service.setFavorite(session, newRating)
        }.invokeOnCompletion {
            if (it != null) {
                view.showError(it)
                view.updateFavorite(session.isFavorite)
            }
        }
    }
}
