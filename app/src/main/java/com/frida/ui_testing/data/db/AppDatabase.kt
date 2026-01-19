package com.frida.ui_testing.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [RepoEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun repoDao(): RepoDao
}