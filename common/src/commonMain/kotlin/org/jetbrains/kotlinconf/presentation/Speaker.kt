package org.jetbrains.kotlinconf.presentation

import org.jetbrains.kotlinconf.*

interface SpeakerView : BaseView {
}

class SpeakerPresenter(val view: SpeakerView) : BasePresenter(view) {
    fun sessionsForSpeaker(id: String) = ConferenceService.speakerSessions(id)
}