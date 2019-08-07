package org.jetbrains.kotlinconf

class Observable<T : Any> {
    private val watchers = mutableSetOf<(T) -> Unit>()

    fun onChange(block: (T) -> Unit): Subscription {
        watchers.add(block)

        return object : Subscription {
            override fun cancel() {
                watchers.remove(block)
            }
        }
    }

    fun change(value: T) {
        watchers.forEach {
            it(value)
        }
    }
}

interface Subscription {
    fun cancel()
}