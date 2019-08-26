package org.jetbrains.kotlinconf

class Observable<T>(var current: T) {
    private val subscriptions = mutableSetOf<(T) -> Unit>()
    private var onClose: () -> Unit = {}

    fun <O> onChange(block: (T) -> O): Observable<O> {
        val value = block(current)
        val result = Observable(value)

        val element: (T) -> Unit = { value ->
            result.change(block(value))
        }

        subscriptions.add(element)
        result.onClose = {
            subscriptions.remove(element)
        }

        return result
    }

    fun change(value: T) {
        current = value

        subscriptions.forEach {
            it(value)
        }
    }

    inline fun tryUpdate(newValue: T, block: () -> Unit) {
        val old = current
        try {
            change(newValue)
            block()
        } catch (cause: Throwable) {
            change(old)
            throw cause
        }
    }

    fun close() {
        onClose()
    }
}
