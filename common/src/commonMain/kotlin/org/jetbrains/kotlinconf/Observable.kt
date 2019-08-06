package org.jetbrains.kotlinconf

class Observable<T : Any> {

    fun onEach(block: (T) -> Unit) {
    }

    fun emit(value: T) {
    }
}