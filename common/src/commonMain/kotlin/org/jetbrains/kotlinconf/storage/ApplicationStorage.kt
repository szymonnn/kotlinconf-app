package org.jetbrains.kotlinconf.storage

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import org.jetbrains.kotlinconf.api.*
import kotlin.properties.*
import kotlin.reflect.*

interface ApplicationStorage {
    fun putBoolean(key: String, value: Boolean)
    fun getBoolean(key: String, defaultValue: Boolean): Boolean
    fun putString(key: String, value: String)
    fun getString(key: String): String?
}

@UnstableDefault
inline fun <reified T> ApplicationStorage.bind(
    key: String,
    serializer: KSerializer<T>,
    block: () -> T
): ReadWriteProperty<ConferenceService, T> {
    val value = getString(key)?.let { Json.parse(serializer, it) } ?: block()

    return object : ReadWriteProperty<ConferenceService, T> {
        private var currentValue: T = value

        override fun setValue(thisRef: ConferenceService, property: KProperty<*>, value: T) {
            currentValue = value
            putString(key, Json.stringify(serializer, value))
        }

        override fun getValue(thisRef: ConferenceService, property: KProperty<*>): T = currentValue
    }
}
/*
    /*
     * Local storage
     */
    private inline fun <reified T : Any> read(key: String, elementSerializer: KSerializer<T>) = storage
        .getString(key, "")
        .takeUnless { it.isBlank() }
        ?.let {
            try {
                Json.parse(elementSerializer, it)
            } catch (_: Throwable) {
                null
            }
        }

    private inline fun <reified T : Any> write(key: String, obj: T?, elementSerializer: KSerializer<T>) {
    }

    private inline fun <reified T : Any> preferences(): ReadWriteProperty<Any?, T?> {
        val key = T::class.simpleName!!
        val serializer = T::serializer

        observable(read(key, elementSerializer)) { _, _, new ->
            write(key, new, elementSerializer)
        }
    }
 */
