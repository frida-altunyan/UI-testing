package com.frida.ui_testing.data.repo

import com.frida.ui_testing.data.api.GitHubApi
import com.frida.ui_testing.data.db.RepoDao
import com.frida.ui_testing.data.db.RepoEntity
import com.frida.ui_testing.data.db.toDomain
import com.frida.ui_testing.data.dto.ItemDto
import com.frida.ui_testing.data.dto.RepoDto
import com.frida.ui_testing.data.dto.toDomain
import com.frida.ui_testing.domain.model.Repo
import com.frida.ui_testing.domain.model.toEntity
import com.frida.ui_testing.domain.repo.RepoRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.IOException

class RepoRepositoryImplTest {

    private val api: GitHubApi = mockk()
    private val dao: RepoDao = mockk()

    private lateinit var repository: RepoRepository

    @BeforeEach
    fun setUp() {
        repository = RepoRepositoryImpl(api = api, dao = dao)
    }

    @Test
    fun `searchRepositories when api succeeds returns domain and caches entities`() = runTest {
        val query = "kotlin"

        val apiResponse: RepoDto = fakeRepoResponseDto(
            totalCount = 2,
            items = listOf(
                fakeRepoItemDto(id = 1, name = "Repo1"),
                fakeRepoItemDto(id = 2, name = "Repo2"),
            )
        )

        val expectedDomain: Repo = apiResponse.toDomain()

        coEvery { api.searchRepositories(query) } returns apiResponse
        coEvery { dao.insertAll(any()) } returns Unit

        val result = repository.searchRepositories(query)

        assertThat(result).isEqualTo(expectedDomain)

        val expectedEntities = expectedDomain.items.map { it.toEntity() }
        coVerify(exactly = 1) { dao.insertAll(expectedEntities) }
        coVerify(exactly = 1) { api.searchRepositories(query) }
        coVerify(exactly = 0) { dao.getAll() }

        confirmVerified(api, dao)
    }

    @Test
    fun `searchRepositories when api throws IOException returns cached data`() = runTest {
        val query = "android"

        val cachedEntities: List<RepoEntity> = listOf(
            fakeRepoEntity(id = 10, name = "Cached1"),
            fakeRepoEntity(id = 11, name = "Cached2"),
            fakeRepoEntity(id = 12, name = "Cached3"),
        )

        coEvery { api.searchRepositories(query) } throws IOException("network down")
        coEvery { dao.getAll() } returns cachedEntities

        val result = repository.searchRepositories(query)

        assertThat(result.totalCount).isEqualTo(cachedEntities.size.toLong())
        assertThat(result.incompleteResults).isFalse()
        assertThat(result.items).isEqualTo(cachedEntities.map { it.toDomain() })

        coVerify(exactly = 1) { api.searchRepositories(query) }
        coVerify(exactly = 1) { dao.getAll() }
        coVerify(exactly = 0) { dao.insertAll(any()) }

        confirmVerified(api, dao)
    }

    @Test
    fun `searchRepositories when api throws IOException and cache empty returns empty Repo`() =
        runTest {
            val query = "compose"

            coEvery { api.searchRepositories(query) } throws IOException("no internet")
            coEvery { dao.getAll() } returns emptyList()

            val result = repository.searchRepositories(query)

            assertThat(result.totalCount).isEqualTo(0L)
            assertThat(result.incompleteResults).isFalse()
            assertThat(result.items).isEmpty()

            coVerify(exactly = 1) { api.searchRepositories(query) }
            coVerify(exactly = 1) { dao.getAll() }
            coVerify(exactly = 0) { dao.insertAll(any()) }

            confirmVerified(api, dao)
        }


    private fun fakeRepoResponseDto(
        totalCount: Long,
        items: List<ItemDto>
    ): RepoDto = RepoDto(
        totalCount = totalCount,
        incompleteResults = false,
        items = items
    )

    private fun fakeRepoItemDto(id: Long, name: String): ItemDto =
        ItemDto(id = id, name = name)

    private fun fakeRepoEntity(id: Long, name: String): RepoEntity =
        RepoEntity(
            id = id,
            name = name,
            fullName = "",
            isPrivate = false,
            htmlUrl = "",
            ownerLogin = "",
            ownerAvatarUrl = ""
        )
}