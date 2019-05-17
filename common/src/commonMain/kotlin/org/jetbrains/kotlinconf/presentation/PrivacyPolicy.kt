package org.jetbrains.kotlinconf.presentation

import org.jetbrains.kotlinconf.api.*

class PrivacyPolicyPresenter(
    private val repository: ConferenceService
) {
    fun onAcceptPrivacyPolicyClicked() {
        repository.privacyPolicyAccepted = true
    }
}