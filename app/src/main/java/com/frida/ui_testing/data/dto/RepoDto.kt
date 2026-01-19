package com.frida.ui_testing.data.dto

import com.frida.ui_testing.domain.model.Repo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RepoDto(
    @SerialName("total_count")
    val totalCount: Long? = null,
    @SerialName("incomplete_results")
    val incompleteResults: Boolean? = null,
    val items: List<ItemDto>? = null,
)

fun RepoDto.toDomain() = Repo(
    totalCount = totalCount?: 0L,
    incompleteResults = incompleteResults ?: false,
    items = items?.map { it.toDomain() }.orEmpty(),
)