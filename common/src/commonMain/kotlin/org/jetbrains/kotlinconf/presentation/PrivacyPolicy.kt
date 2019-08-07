package org.jetbrains.kotlinconf.presentation

import org.jetbrains.kotlinconf.*

class PrivacyPolicyPresenter {
    fun acceptPrivacyPolicy() {
        ConferenceService.acceptPrivacyPolicy()
    }
}