package org.jetbrains.kotlinconf

import kotlinx.coroutines.*
import kotlinx.serialization.*
import kotlinx.serialization.internal.*
import org.jetbrains.kotlinconf.presentation.*
import org.jetbrains.kotlinconf.storage.*
import kotlin.coroutines.*
import kotlin.native.concurrent.*
import kotlin.random.*

/**
 * [ConferenceService] handles data and builds model.
 *
 * @param userId: unique user identifier.
 * @param endPoint: API address
 * @param storage: persistent application storage implementation. TODO: move to common
 */
@ThreadLocal
object ConferenceService : CoroutineScope {
    override val coroutineContext: CoroutineContext = dispatcher() + SupervisorJob()

    private val storage: ApplicationStorage = ApplicationStorage()
    private var userId: String? by storage(NullableSerializer(String.serializer())) { null }

    /**
     * ------------------------------
     * Observables.
     * ------------------------------
     */

    /**
     * Public conference information.
     */
    private var _publicData: SessionizeData by storage(SessionizeData.serializer()) { SessionizeData() }
    val publicData: Observable<SessionizeData> = Observable(_publicData)

    /**
     * Favorites list.
     */
    private var _favorites: Set<String> by storage(String.serializer().set) { emptySet() }
    val favorites: Observable<Set<String>> = Observable(_favorites)

    /**
     * Votes list.
     */
    private var _votes: Map<String, RatingData> by storage((String.serializer() to RatingData.serializer()).map) { emptyMap() }
    val votes = Observable(_votes)

    /**
     * Live sessions.
     */
    private val _liveSessions = Observable<Set<String>>(emptySet())
    private val _upcomingFavorites = Observable<Set<String>>(emptySet())

    val liveSessions: Observable<List<SessionCard>> = _liveSessions.onChange {
        it.toList().map { id -> sessionCard(id) }
    }

    val upcomingFavorites: Observable<List<SessionCard>> = _upcomingFavorites.onChange {
        it.toList().map { id -> sessionCard(id) }
    }

    /**
     * Cached.
     */
    private var cards: MutableMap<String, SessionCard> = mutableMapOf()

    init {
        acceptPrivacyPolicy()

        launch {
            userId?.let { Api.sign(it) }
            if (_publicData.sessions.isEmpty()) {
                refresh()
            }
        }

        launch {
            while (true) {
                updateLive()
                updateUpcoming()
                delay(5 * 1000)
            }
        }

        /**
         * TODO: clear removed votes
         */
        votes.onChange {
            it.entries.forEach { (id, rating) ->
                val card = sessionCard(id)
                card.ratingData.change(rating)
            }
        }
    }

    /**
     * ------------------------------
     * Representation.
     * ------------------------------
     */
    /**
     * Check if session is favorite.
     */
    fun sessionIsFavorite(sessionId: String): Boolean = sessionId in _favorites

    /**
     * Get session rating.
     */
    fun sessionRating(sessionId: String): RatingData? = _votes[sessionId]

    /**
     * Get speakers from session.
     */
    fun sessionSpeakers(sessionId: String): List<SpeakerData> {
        val speakerIds = session(sessionId).speakers
        return speakerIds.map { speaker(it) }
    }

    /**
     * Get sessions for speaker.
     */
    fun speakerSessions(speakerId: String): List<SessionCard> {
        val sessionIds = speaker(speakerId).sessions
        return sessionIds.map { sessionCard(it) }
    }

    /**
     * Get sorted session groups.
     */
    fun sessionGroups(): List<SessionGroup> = _publicData
        .sessions
        .makeGroups()

    /**
     * Get sorted favorite session groups.
     */
    fun favoriteGroups(): List<SessionGroup> = _favorites
        .map { session(it) }
        .makeGroups()

    /**
     * Find speaker by id.
     */
    fun speaker(id: String): SpeakerData =
        _publicData.speakers.find { it.id == id } ?: error("Internal error. Speaker with id $id not found.")

    /**
     * Find session by id.
     */
    fun session(id: String): SessionData =
        _publicData.sessions.find { it.id == id } ?: error("Internal error. Session with id $id not found.")

    /**
     * Find room by id.
     */
    fun room(id: Int): RoomData =
        _publicData.rooms.find { it.id == id } ?: error("Internal error. Room with id $id not found.")

    /**
     * Get session card.
     */
    fun sessionCard(id: String): SessionCard {
        cards[id]?.let { return it }

        val session = session(id)
        val roomId = session.roomId ?: error("No room id in session: ${session.id}")

        val result = SessionCard(
            session,
            "${session.startsAt.time()}-${session.endsAt.time()}",
            room(roomId),
            sessionSpeakers(id),
            favorites.onChange { id in it },
            votes.onChange { it[id] },
            _liveSessions.onChange { id in it }
        )

        cards[id] = result
        return result
    }


    /**
     * ------------------------------
     * User actions.
     * ------------------------------
     */

    /**
     * Vote for session.
     */
    fun vote(sessionId: String, rating: RatingData?) {
        launch {
            val userId = userId ?: error("Privacy policy isn't accepted.")
            val ratingData = sessionCard(sessionId).ratingData

            ratingData.tryUpdate(rating) {
                if (rating != null) {
                    val vote = VoteData(sessionId, rating)
                    Api.postVote(userId, vote)
                } else {
                    Api.deleteVote(userId, sessionId)
                }
            }

            updateVote(sessionId, rating)
        }
    }

    /**
     * Mark session as favorite.
     */
    fun markFavorite(sessionId: String) {
        launch {
            val userId = userId ?: error("Privacy policy isn't accepted.")

            val favorite = sessionCard(sessionId).isFavorite
            val isFavorite = !favorite.current

            favorite.tryUpdate(isFavorite) {
                if (isFavorite) {
                    Api.postFavorite(userId, sessionId)
                } else {
                    Api.deleteFavorite(userId, sessionId)
                }
            }

            updateFavorite(sessionId, isFavorite)
        }
    }

    /**
     * Accept privacy policy clicked.
     */
    fun acceptPrivacyPolicy() {
        if (userId != null) return

        val id = generateUserId().also {
            userId = it
        }

        launch {
            Api.sign(id)
        }
    }

    /**
     * Reload data model from server.
     */
    fun refresh() {
        launch {
            Api.getAll(userId).apply {
                _publicData = allData
                _favorites = favorites.toSet()
                _votes = votes.mapNotNull { vote -> vote.rating?.let { vote.sessionId to it } }.toMap()
            }

            publicData.change(_publicData)
            favorites.change(_favorites)
        }
    }

    /**
     * TODO: mock for now
     */
    private fun updateLive() {
        val sessions = publicData.current.sessions
        if (sessions.isEmpty()) {
            return
        }

        val result = mutableSetOf<String>()
        repeat(5) {
            val index = Random.nextInt(sessions.size)
            result += sessions[index].id
        }

        _liveSessions.change(result)
    }

    /**
     * TODO: mock for now
     */
    private fun updateUpcoming() {
        val favorites = favorites.current.toList()
        if (favorites.isEmpty()) {
            return
        }

        val result = mutableSetOf<String>()
        repeat(5) {
            val index = Random.nextInt(favorites.size)
            result += favorites[index]
        }

        _upcomingFavorites.change(result)
    }

    private fun updateVote(sessionId: String, rating: RatingData?) {
        val result = mutableMapOf<String, RatingData>()
        result.putAll(_votes)
        if (rating == null) {
            result.remove(sessionId)
        } else {
            result[sessionId] = rating
        }

        _votes = result
    }

    private fun updateFavorite(sessionId: String, isFavorite: Boolean) {
        if (isFavorite) check(sessionId !in _favorites)
        if (!isFavorite) check(sessionId in _favorites)

        val result = mutableSetOf<String>()
        result.addAll(_favorites)

        if (!isFavorite) {
            result.remove(sessionId)
        } else {
            result.add(sessionId)
        }

        _favorites = result
        favorites.change(_favorites)
    }
}

/**
 * 1. user click vote button
 * 2. update observable
 * 3. send request
 * 4. onFail -> show error, revert observable
 * 5. onSuccess -> do nothing
 **/
/**
 * Group sessions by title.
 */
private fun List<SessionData>.makeGroups(): List<SessionGroup> = groupBy { it.startsAt }
    .map { (startsAt, sessions) ->
        val monthName = startsAt.month.value
        val day = startsAt.dayOfMonth
        val endsAt = sessions.first().endsAt
        val time = "${startsAt.time()}-${endsAt.time()}"

        val cards = sessions.map { ConferenceService.sessionCard(it.id) }

        SessionGroup(monthName, day, time, cards)
    }

