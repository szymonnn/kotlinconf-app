package org.jetbrains.kotlinconf.presentation

import kotlinx.coroutines.*
import org.jetbrains.kotlinconf.*
import kotlin.coroutines.*

interface BaseView {
    fun showError(error: Throwable)
}

open class BasePresenter(private val baseView: BaseView)
