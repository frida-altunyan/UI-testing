package com.frida.ui_testing.domain.model

data class Repo(
    val totalCount: Long,
    val incompleteResults: Boolean,
    val items: List<Item>,
)