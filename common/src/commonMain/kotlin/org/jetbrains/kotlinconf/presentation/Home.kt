package org.jetbrains.kotlinconf.presentation

import org.jetbrains.kotlinconf.*

interface HomeView : BaseView {
//    fun onLiveSessions(sessions: List<SessionData>)
//
//    fun onSpeakers(speakers: List<SpeakerData>)
//
//    fun onPartners(partners: List<PartnerData>)
}

class HomePresenter(
    private val view: HomeView,
    private val service: ConferenceService
) : BasePresenter(view) {

    init {
//        service.apply {
//            onLiveSessions {
//                view.onLiveSessions(it)
//            }
//
//            onSpeakers {
//                view.onSpeakers(it)
//            }
//
//            onPartners {
//                view.onPartners(it)
//            }
//
//            service.reloadModel()
//        }
    }
}