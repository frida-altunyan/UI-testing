package com.frida.ui_testing.data.repo

import com.frida.ui_testing.data.api.GitHubApi
import com.frida.ui_testing.data.db.RepoDao
import com.frida.ui_testing.data.db.toDomain
import com.frida.ui_testing.data.dto.toDomain
import com.frida.ui_testing.domain.model.Repo
import com.frida.ui_testing.domain.model.toEntity
import com.frida.ui_testing.domain.repo.RepoRepository
import okio.IOException
import javax.inject.Inject

class RepoRepositoryImpl @Inject constructor(
    private val api: GitHubApi,
    private val dao: RepoDao,
) : RepoRepository {
    override suspend fun searchRepositories(query: String): Repo {
        return try {
            val response = api.searchRepositories(query)
            val domain = response.toDomain()

            dao.insertAll(domain.items.map { it.toEntity() })
            domain
        } catch (e: IOException) {
            val cached = dao.getAll()
            Repo(
                totalCount = cached.size.toLong(),
                incompleteResults = false,
                items = cached.map { it.toDomain() }
            )
        }
    }
}