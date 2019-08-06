package org.jetbrains.kotlinconf

import kotlinx.coroutines.*
import kotlinx.serialization.*
import org.jetbrains.kotlinconf.api.*
import org.jetbrains.kotlinconf.storage.*
import kotlin.coroutines.*

/**
 * [ConferenceService] handles data and builds model.
 *
 * @param userId: unique user identifier.
 * @param endPoint: API address
 * @param storage: persistent application storage implementation. TODO: move to common
 */
class ConferenceService(
    val userId: String,
    endPoint: String,
    storage: ApplicationStorage
) : CoroutineScope {
    override val coroutineContext: CoroutineContext = dispatcher() + SupervisorJob()
    private val api = Api(endPoint, userId)

    private var _loggedIn: Boolean = false
    private var _sessions: SessionizeData by storage(SessionizeData.serializer()) { SessionizeData() }
    private var _favorites: Set<String> by storage(String.serializer().set) { emptySet() }
    private var _votes: List<String> by storage(String.serializer().list) { emptyList() }
    private var _privacyPolicyAccepted: Boolean by storage(Boolean.serializer()) { false }

    /**
     * ------------------------------
     * Observables.
     * ------------------------------
     */

    /**
     * Favorites list.
     */
    val favorites = Observable<Set<String>>()

    /**
     * Votes list.
     */
    val votes = Observable<List<VoteData>>()

    /**
     * Live sessions.
     */
    val liveSessions = Observable<List<SessionData>>()

    init {
        launch {
            api.createUser(userId)
            _loggedIn = true
        }
    }

    /**
     * ------------------------------
     * Representation.
     * ------------------------------
     */

    /**
     * Get session rating.
     */
    fun sessionRating(sessionId: String): RatingData? = TODO()

    /**
     * Get speakers from session.
     */
    fun sessionSpeakers(sessionId: String): List<SpeakerData> = TODO()

    /**
     * Get sessions for speaker.
     */
    fun speakerSessions(speakerId: String): List<SessionData> = TODO()

    /**
     * Get sorted session groups.
     */
    fun sessionGroups(): List<SessionGroup> = TODO()

    /**
     * ------------------------------
     * User actions.
     * ------------------------------
     */

    /**
     * Vote for session.
     *
     * @param rating to set, null to remove.
     */
    fun vote(sessionId: String, rating: RatingData?) {
    }

    /**
     * Mark session as favorite.
     */
    fun markFavorite(sessionId: String, isFavorite: Boolean) {
    }

    /**
     * Update sessions.
     */
    fun pullToRefresh() {
    }

    /**
     * Accept privacy policy clicked.
     */
    fun acceptPrivacyPolicy() {
    }

//    internal fun reloadModel() {
//        launch {
//            api.getAll(user.id)
//        }
//    }
//
//    internal fun setRating(session: String, rating: RatingData?) {
//        launch {
//            if (rating != null) {
//                val vote = VoteData(session, rating)
//                api.postVote(user.id, vote)
//            } else {
//                api.deleteVote(user.id, session)
//            }
//        }
//    }
//
//    internal suspend fun setFavorite(session: String, isFavorite: Boolean) {
//        if (isFavorite) {
//            api.postFavorite(user.id, session)
//        } else {
//            api.deleteFavorite(user.id, session)
//        }
//    }
}
