package com.frida.ui_testing.di

import android.content.Context
import androidx.room.Room
import com.frida.ui_testing.data.db.AppDatabase
import com.frida.ui_testing.data.db.RepoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DbModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "github_db"
        ).build()

    @Provides
    fun provideRepoDao(db: AppDatabase): RepoDao =
        db.repoDao()
}