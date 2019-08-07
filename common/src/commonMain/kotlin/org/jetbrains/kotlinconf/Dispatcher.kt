package org.jetbrains.kotlinconf

import kotlinx.coroutines.*

@Suppress("NO_ACTUAL_FOR_EXPECT")
internal expect fun dispatcher(): CoroutineDispatcher

internal expect fun generateUserId(): String
