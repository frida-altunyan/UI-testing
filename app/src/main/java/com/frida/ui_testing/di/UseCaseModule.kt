package com.frida.ui_testing.di

import com.frida.ui_testing.domain.repo.RepoRepository
import com.frida.ui_testing.domain.usecase.SearchRepositoryUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

    @Provides
    fun provideSearchRepositoriesUseCase(
        repository: RepoRepository
    ): SearchRepositoryUseCase =
        SearchRepositoryUseCase(repository)
}
