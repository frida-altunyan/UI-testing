package com.frida.ui_testing.domain.uscease

import com.frida.ui_testing.domain.model.Repo
import com.frida.ui_testing.domain.repo.RepoRepository
import com.frida.ui_testing.domain.usecase.SearchRepositoryUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SearchRepositoryUseCaseTest {
    private val repository: RepoRepository = mockk()

    private lateinit var useCase: SearchRepositoryUseCase

    @BeforeEach
    fun setUp() {
        useCase = SearchRepositoryUseCase(repository)
    }

    @Test
    fun `invoke calls repository and returns result`() = runTest {
        val query = "query"

        val expectedRepo = Repo(
            totalCount = 1,
            incompleteResults = false,
            items = emptyList()
        )

        coEvery { repository.searchRepositories(query) } returns expectedRepo

        val result = useCase(query)

        assertThat(result).isEqualTo(expectedRepo)

        coVerify(exactly = 1) { repository.searchRepositories(query) }
        confirmVerified(repository)
    }
}