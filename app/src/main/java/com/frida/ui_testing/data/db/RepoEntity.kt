package com.frida.ui_testing.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.frida.ui_testing.domain.model.Item
import com.frida.ui_testing.domain.model.Owner
import com.frida.ui_testing.domain.model.Repo

@Entity(tableName = "repos")
data class RepoEntity(
    @PrimaryKey
    val id: Long,
    val name: String,
    val fullName: String,
    val isPrivate: Boolean,
    val htmlUrl: String,
    val ownerLogin: String,
    val ownerAvatarUrl: String,
)

fun List<RepoEntity>.toRepoDomain(): Repo =
    Repo(
        totalCount = size.toLong(),
        incompleteResults = false,
        items = map { it.toDomain() }
    )

fun RepoEntity.toDomain() = Item(
    id = id,
    nodeId = "",
    name = name,
    fullName = fullName,
    private = isPrivate,
    htmlUrl = htmlUrl,
    owner = Owner(
        login = ownerLogin,
        id = 0L,
        nodeId = "",
        avatarUrl = ownerAvatarUrl,
        gravatarId = "",
        url = "",
        htmlUrl = "",
        followersUrl = ""
    )
)
