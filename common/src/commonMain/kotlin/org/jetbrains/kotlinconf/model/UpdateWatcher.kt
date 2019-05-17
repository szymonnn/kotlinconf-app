package org.jetbrains.kotlinconf.model

import org.jetbrains.kotlinconf.*

class UpdateWatcher {
    private val sessionHandlers = mutableListOf<(Session) -> Unit>()

    fun onSessionUpdate(block: (session: Session) -> Unit) {
        sessionHandlers.add(block)
    }

    fun updateSession(session: Session) {
        sessionHandlers.forEach { it(session) }
    }
}