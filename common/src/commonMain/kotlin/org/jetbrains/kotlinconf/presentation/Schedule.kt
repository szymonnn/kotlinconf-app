package org.jetbrains.kotlinconf.presentation

import org.jetbrains.kotlinconf.*

interface ScheduleView : BaseView {
    fun onSessions(session: List<SessionData>)
    fun onFavorites(session: List<SessionData>)
}

class SchedulePresenter(
    private val view: ScheduleView,
    private val service: ConferenceService
) : BasePresenter(view) {
    init {
//        service.apply {
//            onSessions {
//                view.onSessions(it)
//            }
//            onFavorites {
//                view.onFavorites(it)
//            }
//        }
    }

    fun onPullRefresh() {
//        service.reloadModel()
    }
}
