package org.jetbrains.kotlinconf

import io.ktor.util.date.*
import kotlinx.serialization.*
import kotlinx.serialization.internal.*

@Serializer(forClass = GMTDate::class)
internal object GMTDateSerializer : KSerializer<GMTDate> {
    override val descriptor: SerialDescriptor =
        StringDescriptor.withName("GMTDateDefaultSerializer")

    override fun serialize(encoder: Encoder, obj: GMTDate) {
        encoder.encodeLong(obj.timestamp)
    }

    override fun deserialize(decoder: Decoder): GMTDate {
        return GMTDate(decoder.decodeLong())
    }
}