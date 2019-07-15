package org.jetbrains.kotlinconf.presentation

import kotlinx.coroutines.*
import org.jetbrains.kotlinconf.api.*
import org.jetbrains.kotlinconf.model.*

interface SpeakersView : BaseView {
    fun onUpdate(speakers: List<Speaker>)
}

class SpeakersPresenter(
    private val view: SpeakersView,
    private val service: ConferenceService
) : BasePresenter(view) {

    fun onCreate() {
        launch {
            val model = service.reloadModel()
            view.onUpdate(model.speakers)
        }
    }
}
