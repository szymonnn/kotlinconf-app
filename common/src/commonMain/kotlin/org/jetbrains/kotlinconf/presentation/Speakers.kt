package org.jetbrains.kotlinconf.presentation

import org.jetbrains.kotlinconf.*

interface SpeakersView : BaseView {
    fun onSpeakers(speakers: List<SpeakerData>)
}

class SpeakersPresenter(
    private val view: SpeakersView
) : BasePresenter(view) {
    init {
        ConferenceService.publicData.onChange {
            view.onSpeakers(it.speakers)
        }
    }
}
