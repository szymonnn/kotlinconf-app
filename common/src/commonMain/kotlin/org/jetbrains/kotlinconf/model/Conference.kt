package org.jetbrains.kotlinconf.model

import kotlinx.serialization.*
import org.jetbrains.kotlinconf.data.*

@Serializable
class Conference {
    @Transient
    val updateWatcher = UpdateWatcher()

    @Transient
    private val _sessions = mutableListOf<Session>()

    @Transient
    private val _speakers = mutableListOf<Speaker>()

    @Transient
    private val _partners = mutableListOf<Partner>()

    val sessions: List<Session> get() = _sessions

    val speakers: List<Speaker> get() = _speakers

    val favorites: List<Session> get() = _sessions.filter { it.isFavorite }

    val partners: List<Partner> get() = _partners

    fun update(data: ConferenceData) {
        _sessions.clear()
        _speakers.clear()
        _partners.clear()

        val favorites: Set<String> = data.favorites.toSet()
        val votesIndex: Map<String, RatingData?> = data.votes.map { it.sessionId to it.rating }.toMap()
        val roomsIndex: Map<Int, Room> = data.rooms.map { it.id to Room(it.id, it.name, it.sort) }.toMap()
        val tagsIndex = data.categories.flatMap { it.items }.map { it.id to it.name }.toMap()
        val speakerById: (String) -> Speaker = { id: String -> speakers.find { it.id == id }!! }
        val sessionById: (String) -> Session = { id: String -> sessions.find { it.id == id }!! }

        for (sessionData in data.sessions) {
            val sessionId = sessionData.id
            val isFavorite = sessionId in favorites
            val room = roomsIndex[sessionData.roomId]!!
            val tags = sessionData.categoryItems.map { tagsIndex[it]!! }

            val session = Session(
                sessionData, isFavorite, votesIndex[sessionId], room, tags, speakerById, updateWatcher
            )
            _sessions.add(session)
        }

        for (speakerData in data.speakers) {
            _speakers.add(Speaker(speakerData, sessionById))
        }

        _sessions.sortBy { it.startsAt }
        _speakers.sortBy { it.fullName }

        _partners.addAll(data.partners.map { Partner(it.name, it.logo, it.description) })
    }
}

@Serializable
class Partner(
    val name: String,
    val logo: String,
    val description: String
)

@Serializable
class Room(val id: Int, val name: String, val sort: Int)
