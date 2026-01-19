package com.frida.ui_testing.data.dto

import com.frida.ui_testing.domain.model.Item
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ItemDto(
    val id: Long? = null,
    @SerialName("node_id")
    val nodeId: String? = null,
    val name: String? = null,
    @SerialName("full_name")
    val fullName: String? = null,
    val private: Boolean? = null,
    val owner: OwnerDto? = null,
    @SerialName("html_url")
    val htmlUrl: String? = null,
)

fun ItemDto.toDomain() = Item(
    id = id ?: 0L,
    nodeId = nodeId.orEmpty(),
    name = name.orEmpty(),
    fullName = fullName.orEmpty(),
    private = private ?: false,
    owner = owner?.toDomain(),
    htmlUrl = htmlUrl.orEmpty(),
)