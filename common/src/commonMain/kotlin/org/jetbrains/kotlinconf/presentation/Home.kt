package org.jetbrains.kotlinconf.presentation

import org.jetbrains.kotlinconf.*

interface HomeView : BaseView {
    fun onLiveSessions(sessions: List<SessionCard>)
    fun onDataReceive(data: SessionizeData)
}

class HomePresenter(
    private val view: HomeView
) : BasePresenter(view) {
}