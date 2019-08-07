package org.jetbrains.kotlinconf

import kotlinx.coroutines.*

internal actual fun dispatcher(): CoroutineDispatcher = Dispatchers.Main

internal actual fun generateUserId(): String {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}