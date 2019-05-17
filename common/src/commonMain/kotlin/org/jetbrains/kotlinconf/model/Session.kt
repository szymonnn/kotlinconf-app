package org.jetbrains.kotlinconf

import io.ktor.util.date.*
import kotlinx.serialization.*
import org.jetbrains.kotlinconf.data.*
import org.jetbrains.kotlinconf.model.*

//@Serializable
class Session internal constructor(
    data: SessionData,
    isFavorite: Boolean,
    rating: RatingData?,
    val room: Room,
    val tags: List<String>,
    speakersIndex: (String) -> Speaker,
    private val observer: UpdateWatcher
) {
    var isFavorite: Boolean = isFavorite
        set(value) {
            field = value
            observer.updateSession(this)
        }

    var rating: RatingData? = rating
        set(value) {
            field = value
            observer.updateSession(this)
        }

    val id: String = data.id
    val title: String = data.title
    val descriptionText: String = data.descriptionText

    @Transient
    val speakers: List<Speaker> by lazy { data.speakers.map(speakersIndex) }

    @Transient
    val startsAt: GMTDate = data.startsAt.parseDate()

    @Transient
    val endsAt: GMTDate = data.endsAt.parseDate()

    fun isActive(): Boolean = GMTDate() in startsAt..endsAt

    override fun hashCode(): Int = id.hashCode()
}

@Serializable
class Room(val id: Int, val name: String, val sort: Int)
