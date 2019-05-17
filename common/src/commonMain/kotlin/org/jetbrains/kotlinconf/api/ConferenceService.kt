package org.jetbrains.kotlinconf.api

import kotlinx.serialization.*
import org.jetbrains.kotlinconf.*
import org.jetbrains.kotlinconf.data.*
import org.jetbrains.kotlinconf.model.*
import org.jetbrains.kotlinconf.storage.*

/**
 * [ConferenceService] handles data and builds model.
 */
class ConferenceService(
    private val userId: String,
    endPoint: String,
    storage: ApplicationStorage
) {
    private val api = ApiAdapter(endPoint, userId)
    private var loggedIn: Boolean = false

    private val model: Conference by storage.bind("model", Conference.serializer()) { Conference() }
    internal var privacyPolicyAccepted: Boolean by storage.bind("privacyPolicyAccepted", Boolean.serializer()) { false }

    internal suspend fun reloadModel(): Conference {
        val state = try {
            if (!loggedIn) {
                api.createUser(userId)
                loggedIn = true
            }
            api.getAll(userId)
        } catch (cause: Throwable) {
            throw UpdateProblem(cause)
        }

        model.update(state)
        return model
    }

    internal suspend fun setRating(session: Session, rating: RatingData?) {
        if (rating != null) {
            val vote = VoteData(session.id, rating)
            api.postVote(userId, vote)
        } else {
            api.deleteVote(userId, session.id)
        }

        session.rating = rating
    }

    internal suspend fun setFavorite(session: Session, isFavorite: Boolean) {
        if (isFavorite) {
            api.postFavorite(userId, session.id)
        } else {
            api.deleteFavorite(userId, session.id)
        }

        session.isFavorite = isFavorite
    }
}
