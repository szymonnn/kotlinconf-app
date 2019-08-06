package org.jetbrains.kotlinconf

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.client.response.*
import io.ktor.http.*
import kotlinx.io.core.*

/**
 * Adapter to handle backend API and manage auth information.
 */
class Api(private val endPoint: String, private val userId: String) {

    private val client = HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer().apply {
                setMapper(ConferenceData::class, ConferenceData.serializer())
                setMapper(VoteData::class, VoteData.serializer())
            }
        }

        HttpResponseValidator {
            validateResponse {
                when (it.status) {
                    COMEBACK_LATER_STATUS -> throw TooEarlyVote()
                    TOO_LATE_STATUS -> throw TooLateVote()
                    HttpStatusCode.Unauthorized -> throw Unauthorized()
                }

                if (!it.status.isSuccess()) {
                    when (it.call.request.url.encodedPath) {
                        "/favorites" -> throw CannotFavorite()
                    }
                }
            }
        }
    }

    /**
     * Create user with id [userId].
     *
     * @return status of request.
     */
    suspend fun createUser(userId: String): Boolean = client.request<HttpResponse> {
        apiUrl("users")
        method = HttpMethod.Post
        body = userId
    }.use {
        println("User created: ${it.status}")
        it.status.isSuccess()
    }

    /**
     * Get [ConferenceData] info.
     * Load favorites and votes info if [userId] provided.
     */
    suspend fun getAll(userId: String?): ConferenceData = client.get {
        apiUrl("all", userId)
    }

    /**
     * Update favorite information.
     */
    suspend fun postFavorite(userId: String, sessionId: String): Unit = client.post {
        apiUrl("favorites", userId)
        body = sessionId
    }

    /**
     * Remove item from favorites list.
     */
    suspend fun deleteFavorite(userId: String, sessionId: String): Unit = client.delete {
        apiUrl("favorites", userId)
        body = sessionId
    }

    suspend fun postVote(userId: String, vote: VoteData): Unit = client.post {
        apiUrl("votes", userId)
        json()
        body = vote
    }

    suspend fun deleteVote(userId: String, sessionId: String): Unit = client.delete {
        apiUrl("votes", userId)
        body = sessionId
    }

    private fun HttpRequestBuilder.json() {
        contentType(ContentType.Application.Json)
    }

    private fun HttpRequestBuilder.apiUrl(path: String, userId: String? = null) {
        if (userId != null) {
            header(HttpHeaders.Authorization, "Bearer $userId")
        }
        header(HttpHeaders.CacheControl, "no-cache")
        url {
            takeFrom(endPoint)
            encodedPath = path
        }
    }
}
