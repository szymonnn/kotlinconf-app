package org.jetbrains.kotlinconf.presentation

import kotlinx.coroutines.*
import org.jetbrains.kotlinconf.api.*
import org.jetbrains.kotlinconf.model.*

interface HomeView : BaseView {
    fun onLiveSessions(sessions: List<Session>)

    fun onSpeakers(speakers: List<Speaker>)

    fun onPartners(partners: List<Partner>)
}

class HomePresenter(
    private val view: HomeView,
    private val service: ConferenceService
) : BasePresenter(view) {

    fun onCreate() {
        launch {
            val model = service.reloadModel()
            val sessions = model.sessions
                .groupBy { it.startsAt }
                .entries
                .sortedBy { it.key }
                .map { it.value }

            view.onLiveSessions(model.sessions.take(3))
            view.onSpeakers(model.speakers)
            view.onPartners(model.partners)
        }
    }
}