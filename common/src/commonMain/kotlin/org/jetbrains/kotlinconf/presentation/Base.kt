package org.jetbrains.kotlinconf.presentation

interface BaseView {
    fun showError(error: Throwable)
}

open class BasePresenter(private val baseView: BaseView)

