package com.frida.ui_testing.di

import com.frida.ui_testing.domain.repo.RepoRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface RepoTestEntryPoint {
    fun repoRepository(): RepoRepository
}