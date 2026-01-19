package com.frida.ui_testing.domain.usecase

import com.frida.ui_testing.domain.repo.RepoRepository
import javax.inject.Inject

class SearchRepositoryUseCase @Inject constructor(
    private val repository: RepoRepository
) {
    suspend operator fun invoke(query: String) =
        repository.searchRepositories(query)
}