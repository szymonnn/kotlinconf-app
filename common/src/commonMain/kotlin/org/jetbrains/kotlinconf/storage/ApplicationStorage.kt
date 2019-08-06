package org.jetbrains.kotlinconf.storage

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import org.jetbrains.kotlinconf.*
import kotlin.properties.*
import kotlin.reflect.*

interface ApplicationStorage {
    fun putBoolean(key: String, value: Boolean)
    fun getBoolean(key: String, defaultValue: Boolean): Boolean
    fun putString(key: String, value: String)
    fun getString(key: String): String?
}

@UseExperimental(UnstableDefault::class)
inline operator fun <reified T> ApplicationStorage.invoke(
    serializer: KSerializer<T>,
    crossinline block: () -> T
): ReadWriteProperty<ConferenceService, T> = object : ReadWriteProperty<ConferenceService, T> {
    private var currentValue: T? = null

    override fun setValue(thisRef: ConferenceService, property: KProperty<*>, value: T) {
        val key = property.name
        currentValue = value
        putString(key, Json.stringify(serializer, value))
    }

    override fun getValue(thisRef: ConferenceService, property: KProperty<*>): T {
        currentValue?.let { return it }

        val key = property.name
        val result = getString(key)?.let { Json.parse(serializer, it) } ?: block()
        currentValue = result

        return result
    }
}
