package org.jetbrains.kotlinconf.presentation

import org.jetbrains.kotlinconf.api.*

interface MainView : BaseView

class MainPresenter(
    view: MainView,
    private val service: ConferenceService
) : BasePresenter(view) {

    fun onCreate() {
        if (!service.privacyPolicyAccepted) {
//            service.navigation.showPrivacyPolicyDialog()
        }
    }
}