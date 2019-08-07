package org.jetbrains.kotlinconf

import kotlinx.coroutines.*
import kotlinx.serialization.*
import kotlinx.serialization.internal.*
import org.jetbrains.kotlinconf.presentation.*
import org.jetbrains.kotlinconf.storage.*
import kotlin.coroutines.*
import kotlin.native.concurrent.ThreadLocal

/**
 * [ConferenceService] handles data and builds model.
 *
 * @param userId: unique user identifier.
 * @param endPoint: API address
 * @param storage: persistent application storage implementation. TODO: move to common
 */
@ThreadLocal
internal object ConferenceService : CoroutineScope {
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
    val publicData = Observable<SessionizeData>()
    private var _publicData: SessionizeData by storage(SessionizeData.serializer()) { SessionizeData() }

    /**
     * Favorites list.
     */
    val favorites = Observable<Set<String>>()
    private var _favorites: Set<String> by storage(String.serializer().set) { emptySet() }

    /**
     * Votes list.
     */
    val votes = Observable<Map<String, RatingData>>()
    private var _votes: Map<String, RatingData> by storage((String.serializer() to RatingData.serializer()).map) { emptyMap() }

    /**
     * Live sessions.
     */
    val liveSessions = Observable<List<SessionCard>>()
    private var _live: Set<String> = emptySet()

    init {
        launch {
            userId?.let { Api.sign(it) }
            if (_publicData.sessions.isEmpty()) {
                refresh()
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
    fun speakerSessions(speakerId: String): List<SessionData> {
        val sessionIds = speaker(speakerId).sessions
        return sessionIds.map { session(it) }
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
     * Get session card.
     */
    fun sessionCard(id: String): SessionCard = SessionCard(
            session(id),
            sessionSpeakers(id),
            id in _favorites,
            _votes[id],
            id in _live
    )

    /**
     * Group sessions by title.
     */
    private fun List<SessionData>.makeGroups(): List<SessionGroup> = groupBy { it.startsAt + " " + it.endsAt }
            .toList()
            .map { (title, sessions) ->
                val cards = sessions.map { sessionCard(it.id) }
                SessionGroup(title, cards)
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
        val userId = userId ?: error("Privacy policy isn't accepted.")

        launch {
            if (rating != null) {
                val vote = VoteData(sessionId, rating)
                Api.postVote(userId, vote)
            } else {
                Api.deleteVote(userId, sessionId)
            }

            updateVote(sessionId, rating)
        }.invokeOnCompletion {
            votes.change(_votes)
        }
    }

    /**
     * Mark session as favorite.
     */
    fun markFavorite(sessionId: String, isFavorite: Boolean) {
        val userId = userId ?: error("Privacy policy isn't accepted.")

        launch {
            if (isFavorite) {
                Api.postFavorite(userId, sessionId)
            } else {
                Api.deleteFavorite(userId, sessionId)
            }
            updateFavorite(sessionId, isFavorite)
        }.invokeOnCompletion {
            favorites.change(_favorites)
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
        }
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
        val result = mutableSetOf<String>()
        result.addAll(_favorites)

        if (isFavorite) {
            result.remove(sessionId)
        } else {
            result.add(sessionId)
        }

        _favorites = result
    }
}
