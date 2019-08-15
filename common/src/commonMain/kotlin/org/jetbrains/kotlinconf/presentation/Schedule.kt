package org.jetbrains.kotlinconf.presentation

import org.jetbrains.kotlinconf.*

interface ScheduleView : BaseView

class SchedulePresenter(
    private val view: ScheduleView
) : BasePresenter(view) {
    val schedule: Observable<List<SessionGroup>> = ConferenceService.publicData.onChange {
        ConferenceService.sessionGroups()
    }

    val favorites: Observable<List<SessionGroup>> = ConferenceService.favorites.onChange {
        ConferenceService.favoriteGroups()
    }

    fun vote(sessionId: String, rating: RatingData?) {
        ConferenceService.vote(sessionId, rating)
    }

    fun favorite(sessionId: String) {
        ConferenceService.markFavorite(sessionId)
    }

    fun pullToRefresh() {
        ConferenceService.refresh()
    }
}
