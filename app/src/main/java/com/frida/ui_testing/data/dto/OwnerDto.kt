package com.frida.ui_testing.data.dto

import com.frida.ui_testing.domain.model.Owner
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OwnerDto(
    val login: String? = null,
    val id: Long? = null,
    @SerialName("node_id")
    val nodeId: String? = null,
    @SerialName("avatar_url")
    val avatarUrl: String? = null,
    @SerialName("gravatar_id")
    val gravatarId: String? = null,
    val url: String? = null,
    @SerialName("html_url")
    val htmlUrl: String? = null,
    @SerialName("followers_url")
    val followersUrl: String? = null,
)

fun OwnerDto.toDomain() = Owner(
    login = login.orEmpty(),
    id = id ?: 0L,
    nodeId = nodeId.orEmpty(),
    avatarUrl = avatarUrl.orEmpty(),
    gravatarId = gravatarId.orEmpty(),
    url = url.orEmpty(),
    htmlUrl = htmlUrl.orEmpty(),
    followersUrl = followersUrl.orEmpty(),
)