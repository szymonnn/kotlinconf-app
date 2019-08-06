package org.jetbrains.kotlinconf.presentation

import org.jetbrains.kotlinconf.*

interface SpeakersView : BaseView {
    fun onUpdate(speakers: List<SpeakerData>)
}

class SpeakersPresenter(
    private val view: SpeakersView,
    private val service: ConferenceService
) : BasePresenter(view) {

    fun onCreate() {
//        launch {
//            val model = service.reloadModel()
//            view.onUpdate(model.speakers)
//        }
    }
}
