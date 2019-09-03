package org.jetbrains.kotlinconf

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.*
import kotlinx.serialization.internal.*
import org.jetbrains.kotlinconf.presentation.*
import org.jetbrains.kotlinconf.storage.*
import kotlin.coroutines.*
import kotlin.native.concurrent.*
import kotlin.random.*

/**
 * [ConferenceService] handles data and builds model.
 */
@ThreadLocal
@UseExperimental(ExperimentalCoroutinesApi::class)
object ConferenceService : CoroutineScope {
    private val exceptionHandler = object : CoroutineExceptionHandler {
        override val key: CoroutineContext.Key<*> = CoroutineExceptionHandler

        override fun handleException(context: CoroutineContext, exception: Throwable) {
            _errors.offer(exception)
        }
    }

    private val _errors = ConflatedBroadcastChannel<Throwable>()
    val errors = _errors.asFlow().wrap()

    override val coroutineContext: CoroutineContext = dispatcher() + SupervisorJob() + exceptionHandler

    private val storage: ApplicationStorage = ApplicationStorage()
    private var userId: String? by storage(NullableSerializer(String.serializer())) { null }

    /**
     * Cached.
     */
    private var cards: MutableMap<String, SessionCard> = mutableMapOf()

    /**
     * ------------------------------
     * Observables.
     * ------------------------------
     */

    /**
     * Public conference information.
     */
    private val _publicData by storage.live { SessionizeData() }
    val publicData = _publicData.asFlow().wrap()

    /**
     * Favorites list.
     */
    private val _favorites by storage.live { mutableSetOf<String>() }
    val favorites = _favorites.asFlow().wrap()

    /**
     * Votes list.
     */
    private val _votes by storage.live { mutableMapOf<String, RatingData>() }
    val votes = _votes.asFlow().wrap()

    /**
     * Live sessions.
     */
    private val _liveSessions = ConflatedBroadcastChannel<Set<String>>(mutableSetOf())
    private val _upcomingFavorites = ConflatedBroadcastChannel<Set<String>>(mutableSetOf())

    val liveSessions = _liveSessions.asFlow().map {
        it.toList().map { id -> sessionCard(id) }
    }.wrap()

    val upcomingFavorites = _upcomingFavorites.asFlow().map {
        it.toList().map { id -> sessionCard(id) }
    }.wrap()

    val schedule = publicData.map {
        it.sessions.groupByDay()
            .addDayStart()
            .addLunches()
    }.wrap()

    val favoriteSchedule = favorites.map {
        it.map { id -> session(id) }
            .groupByDay()
            .addDayStart()
    }.wrap()

    val speakers = publicData.map { it.speakers }.wrap()

    val sessions = publicData.map {
        it.sessions.sortedBy { it.title }.map { sessionCard(it.id) }
    }.wrap()

    init {
        acceptPrivacyPolicy()

        launch {
            userId?.let { Api.sign(it) }
            if (_publicData.value.sessions.isEmpty()) {
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
    }

    /**
     * ------------------------------
     * Representation.
     * ------------------------------
     */
    /**
     * Check if session is favorite.
     */
    fun sessionIsFavorite(sessionId: String): Boolean = sessionId in _favorites.value

    /**
     * Get session rating.
     */
    fun sessionRating(sessionId: String): RatingData? = _votes.value[sessionId]

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
     * Find speaker by id.
     */
    fun speaker(id: String): SpeakerData =
        _publicData.value.speakers.find { it.id == id } ?: error("Internal error. Speaker with id $id not found.")

    /**
     * Find session by id.
     */
    fun session(id: String): SessionData =
        _publicData.value.sessions.find { it.id == id } ?: error("Internal error. Session with id $id not found.")

    /**
     * Find room by id.
     */
    fun room(id: Int): RoomData =
        _publicData.value.rooms.find { it.id == id } ?: error("Internal error. Room with id $id not found.")

    /**
     * Get session card.
     */
    fun sessionCard(id: String): SessionCard {
        cards[id]?.let { return it }

        val session = session(id)
        val roomId = session.roomId ?: error("No room id in session: ${session.id}")

        val location = room(roomId)
        val speakers = sessionSpeakers(id)
        val isFavorite = favorites.map { id in it }.wrap()
        val ratingData = votes.map { it[id] }.wrap()
        val isLive = _liveSessions.asFlow().map { id in it }.wrap()

        val result = SessionCard(
            session,
            session.startsAt.dayAndMonth(),
            "${session.startsAt.time()}-${session.endsAt.time()}",
            location,
            speakers,
            isFavorite,
            ratingData,
            isLive
        )

        cards[id] = result
        return result
    }

    fun findPicture(url: String, block: (ByteArray) -> Unit) {
        launch {
            val result = Api.loadPicture(url)
            block(result)
        }
    }

    /**
     * ------------------------------
     * User actions.
     * ------------------------------
     */

    /**
     * Vote for session.
     */
    fun vote(sessionId: String, rating: RatingData) {
        launch {
            val userId = userId ?: error("Privacy policy isn't accepted.")

            val votes = _votes.value
            val oldRating = votes[sessionId]
            val newRating = if (rating == oldRating) null else rating

            try {
                updateVote(sessionId, newRating)

                if (newRating != null) {
                    val vote = VoteData(sessionId, rating)
                    Api.postVote(userId, vote)
                } else {
                    Api.deleteVote(userId, sessionId)
                }
            } catch (cause: Throwable) {
                updateVote(sessionId, oldRating)
                throw cause
            }

        }
    }

    /**
     * Mark session as favorite.
     */
    fun markFavorite(sessionId: String) {
        launch {
            val userId = userId ?: error("Privacy policy isn't accepted.")

            val isFavorite = sessionId in _favorites.value

            try {
                updateFavorite(sessionId, !isFavorite)
                if (isFavorite) {
                    Api.postFavorite(userId, sessionId)
                } else {
                    Api.deleteFavorite(userId, sessionId)
                }
            } catch (cause: Throwable) {
                updateFavorite(sessionId, isFavorite)
            }
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
                _publicData.offer(allData)
                _favorites.offer(favorites.toMutableSet())
                val votesMap = mutableMapOf<String, RatingData>().apply {
                    putAll(votes.mapNotNull { vote -> vote.rating?.let { vote.sessionId to it } })
                }
                _votes.offer(votesMap)
            }
        }
    }

    /**
     * TODO: mock for now
     */
    private fun updateLive() {
        val sessions = _publicData.value.sessions
        if (sessions.isEmpty()) {
            _liveSessions.offer(emptySet())
            return
        }

        val result = mutableSetOf<String>()
        repeat(5) {
            val index = Random.nextInt(sessions.size)
            result += sessions[index].id
            return
        }

        _liveSessions.offer(result)
    }

    /**
     * TODO: mock for now
     */
    private fun updateUpcoming() {
        val favorites = _favorites.value.toList()
        if (favorites.isEmpty()) {
            _upcomingFavorites.offer(emptySet())
            return
        }

        val result = mutableSetOf<String>()
        repeat(5) {
            val index = Random.nextInt(favorites.size)
            result += favorites[index]
        }

        _upcomingFavorites.offer(result)
    }

    private fun updateVote(sessionId: String, rating: RatingData?) {
        val votes = _votes.value

        if (rating == null) {
            votes.remove(sessionId)
        } else {
            votes[sessionId] = rating
        }

        _votes.offer(votes)
    }

    private fun updateFavorite(sessionId: String, isFavorite: Boolean) {
        val favorites = _favorites.value
        if (isFavorite) check(sessionId !in favorites)
        if (!isFavorite) check(sessionId in favorites)

        if (!isFavorite) {
            favorites.remove(sessionId)
        } else {
            favorites.add(sessionId)
        }

        _favorites.offer(favorites)
    }
}
