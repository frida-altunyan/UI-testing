package com.frida.ui_testing.domain.model

import com.frida.ui_testing.data.db.RepoEntity

data class Item(
    val id: Long,
    val nodeId: String,
    val name: String,
    val fullName: String,
    val private: Boolean,
    val owner: Owner?,
    val htmlUrl: String,
)

fun Item.toEntity() = RepoEntity(
    id = id,
    name = name,
    fullName = fullName,
    isPrivate = private,
    htmlUrl = htmlUrl,
    ownerLogin = owner?.login.orEmpty(),
    ownerAvatarUrl = owner?.avatarUrl.orEmpty(),
)
