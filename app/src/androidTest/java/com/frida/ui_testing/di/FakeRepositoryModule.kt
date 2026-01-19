package com.frida.ui_testing.di

import com.frida.ui_testing.data.FakeRepoRepository
import com.frida.ui_testing.domain.repo.RepoRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import jakarta.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RepoModule::class]
)
object FakeRepositoryModule {

    @Provides
    @Singleton
    fun provideFakeRepoRepository(): RepoRepository =
        FakeRepoRepository()
}