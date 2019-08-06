package org.jetbrains.kotlinconf.presentation

import org.jetbrains.kotlinconf.*

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
    private val session: SessionData,
    private val service: ConferenceService
) : BasePresenter(view) {

    init {
//        service.apply {
//            onFavorites {
//            }
//
//            onRating {
//            }
//        }
    }

    /**
     * Handle rating button event.
     *
     * Change offline and revert if submit attempt failed.
     */
    fun onRatingButtonClicked(clicked: RatingData) {
        view.setRatingClickable(false)
        view.updateRating(clicked)
//        service.setRating(session.id, clicked)

        view.setRatingClickable(true)
    }

    /**
     * Handle favorite button event.
     *
     * Change offline and revert if submit attempt failed.
     */
    fun onFavoriteButtonClicked() {
//        launch {
//            val newRating = !session.isFavorite
//            view.updateFavorite(newRating)
//            service.setFavorite(session, newRating)
//        }.invokeOnCompletion {
//            if (it != null) {
//                view.showError(it)
//                view.updateFavorite(session.isFavorite)
//            }
//        }
    }
}
