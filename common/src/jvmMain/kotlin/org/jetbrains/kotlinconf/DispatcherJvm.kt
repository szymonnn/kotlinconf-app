package org.jetbrains.kotlinconf

import kotlinx.coroutines.*

internal actual fun dispatcher(): CoroutineDispatcher = Dispatchers.Main
