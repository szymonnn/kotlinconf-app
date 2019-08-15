package org.jetbrains.kotlinconf.presentation

import org.jetbrains.kotlinconf.*

interface SpeakersView : BaseView

class SpeakersPresenter(
    private val view: SpeakersView
) : BasePresenter(view) {
    val speakers: Observable<List<SpeakerData>> = ConferenceService.publicData.onChange {
        it.speakers
    }
}
