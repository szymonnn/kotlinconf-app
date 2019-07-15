package org.jetbrains.kotlinconf.presentation

import kotlinx.coroutines.*
import org.jetbrains.kotlinconf.*
import org.jetbrains.kotlinconf.api.*
import org.jetbrains.kotlinconf.model.*

interface ScheduleView : BaseView {
    fun onUpdate(sessions: List<List<Session>>, favorites: List<Session>)
}

class SchedulePresenter(
    private val view: ScheduleView,
    private val service: ConferenceService
) : BasePresenter(view) {

    fun onCreate() {
        updateData()
    }

    fun onPullRefresh() {
        updateData()
    }

    private fun updateData() {
        launch {
            val model = service.reloadModel()
            val sessions = model.sessions
                .groupBy { it.startsAt }
                .entries
                .sortedBy { it.key }
                .map { it.value }

            view.onUpdate(sessions, model.favorites)

            model.updateWatcher.onSessionUpdate { view.onUpdate(sessions, model.favorites) }
        }
    }
}