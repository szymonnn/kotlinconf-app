package org.jetbrains.kotlinconf

import kotlinx.coroutines.*
import platform.Foundation.*
import platform.UIKit.*
import platform.darwin.*
import kotlin.coroutines.*

internal actual fun dispatcher(): CoroutineDispatcher = UI

private object UI : CoroutineDispatcher() {
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        val queue = dispatch_get_main_queue()
        dispatch_async(queue) {
            block.run()
        }
    }
}

internal actual fun generateUserId(): String {
    return (UIDevice.currentDevice.identifierForVendor ?: NSUUID.UUID()).UUIDString
}
