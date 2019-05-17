package org.jetbrains.kotlinconf.model

import org.jetbrains.kotlinconf.*
import org.jetbrains.kotlinconf.data.*

//@Serializable
class Speaker internal constructor(
    data: SpeakerData,
    sessionIndex: (String) -> Session
) {
    val id: String = data.id
    val firstName: String = data.firstName
    val lastName: String = data.lastName
    val fullName: String = data.fullName
    val bio: String = data.bio

    val profilePicture: String? = data.profilePicture
    val sessions: List<Session> by lazy { data.sessions.map(sessionIndex) }

    val tagLine: String = data.tagLine
    val isTopSpeaker: Boolean = data.isTopSpeaker
    val links: List<LinkData> = data.links
}