package org.jetbrains.kotlinconf.data

import kotlinx.serialization.*

class SessionizeData(val allData: ConferenceData, val etag: String = allData.hashCode().toString())

@Serializable
data class ConferenceData(
    val sessions: List<SessionData> = emptyList(),
    val rooms: List<RoomData> = emptyList(),
    val speakers: List<SpeakerData> = emptyList(),
    val questions: List<QuestionData> = emptyList(),
    val categories: List<CategoryData> = emptyList(),
    val favorites: List<String> = emptyList(),
    val votes: List<VoteData> = emptyList()
)

@Serializable
data class SessionData(
    val id: String,
    val isServiceSession: Boolean,
    val isPlenumSession: Boolean,
    val speakers: List<String>,
    @SerialName("description")
    val descriptionText: String,
    val startsAt: String,
    val title: String,
    val endsAt: String,
    val roomId: Int?,
    val questionAnswers: List<QuestionAnswerData> = emptyList(),
    val categoryItems: List<Int> = emptyList()
)

@Serializable
data class RoomData(
    val name: String,
    val id: Int,
    val sort: Int
)

@Serializable
data class SpeakerData(
    val id: String,
    val firstName: String,
    val lastName: String,
    val profilePicture: String?,
    val sessions: List<String>,
    val tagLine: String,
    val isTopSpeaker: Boolean,
    val bio: String,
    val fullName: String,
    val links: List<LinkData> = emptyList(),
    val categoryItems: List<Int> = emptyList(),
    val questionAnswers: List<QuestionAnswerData> = emptyList()
)

@Serializable
data class QuestionData(
    val question: String,
    val id: Int,
    val sort: Int,
    val questionType: String
)

@Serializable
data class CategoryData(
    val id: Int,
    val sort: Int,
    val title: String,
    val items: List<CategoryItemData> = emptyList()
)

@Serializable
class VoteData(
    val sessionId: String,
    val rating: RatingData? = null
)

@Serializable
data class QuestionAnswerData(
    val questionId: Int,
    val answerValue: String
)

@Serializable
data class LinkData(
    val linkType: String,
    val title: String,
    val url: String
)

@Serializable
data class CategoryItemData(
    val name: String,
    val id: Int,
    val sort: Int
)

enum class RatingData(val value: Int) {
    BAD(-1),
    OK(0),
    GOOD(1);

    companion object {
        fun valueOf(value: Int): RatingData = values().find { it.value == value }!!
    }
}