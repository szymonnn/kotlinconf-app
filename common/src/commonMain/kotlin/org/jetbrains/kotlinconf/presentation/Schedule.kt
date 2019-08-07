package org.jetbrains.kotlinconf.presentation

import org.jetbrains.kotlinconf.*

interface ScheduleView : BaseView {
    fun onSessions(session: List<SessionGroup>)
    fun onFavorites(session: List<SessionGroup>)
    fun onVotes(session: Map<String, RatingData>)
}

class SchedulePresenter(
    private val view: ScheduleView
) : BasePresenter(view) {
    init {
        println("Create conference: $view")
        print(ConferenceService)
        with(ConferenceService) {
            publicData.onChange {
                view.onSessions(sessionGroups())
            }
            favorites.onChange {
                view.onFavorites(favoriteGroups())
            }

            votes.onChange(view::onVotes)

            view.onSessions(sessionGroups())
            view.onFavorites(favoriteGroups())
        }
    }

    fun pullToRefresh() {
        ConferenceService.refresh()
    }
}
