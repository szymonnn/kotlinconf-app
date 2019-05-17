package org.jetbrains.kotlinconf.presentation

import kotlinx.coroutines.*
import org.jetbrains.kotlinconf.*
import kotlin.coroutines.*

interface BaseView {
    fun showError(error: Throwable)
}

open class BasePresenter(private val baseView: BaseView) : CoroutineScope {
    private val job = Job()

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        baseView.showError(throwable)
    }

    override val coroutineContext: CoroutineContext = dispatcher() + job + exceptionHandler

    open fun onDestroy() {
        job.cancel()
    }
}
