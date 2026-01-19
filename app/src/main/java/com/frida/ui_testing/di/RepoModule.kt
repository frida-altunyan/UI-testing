package com.frida.ui_testing.di

import com.frida.ui_testing.data.repo.RepoRepositoryImpl
import com.frida.ui_testing.domain.repo.RepoRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepoModule {

    @Binds
    @Singleton
    abstract fun bindRepoRepository(
        impl: RepoRepositoryImpl
    ): RepoRepository
}