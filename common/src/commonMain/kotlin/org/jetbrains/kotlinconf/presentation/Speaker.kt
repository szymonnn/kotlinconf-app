package org.jetbrains.kotlinconf.presentation

import org.jetbrains.kotlinconf.*

interface SpeakerView : BaseView {
    fun onSpeaker(speaker: SpeakerData, sessions: List<SessionCard>)
}

class SpeakerPresenter(val view: SpeakerView) : BasePresenter(view) {
    fun showSpeaker(speakerId: String) {
        with(ConferenceService) {
            val speaker = speaker(speakerId)
            val cards = speaker.sessions.map { sessionCard(it) }

            view.onSpeaker(speaker, cards)
        }
    }
}