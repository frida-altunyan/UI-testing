package com.frida.ui_testing.domain.repo

import com.frida.ui_testing.domain.model.Repo

interface RepoRepository {
    suspend fun searchRepositories(query: String): Repo
}