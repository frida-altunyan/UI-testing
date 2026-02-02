package com.frida.ui_testing.data.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class RepoDaoTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()


    private lateinit var database: AppDatabase
    private lateinit var dao: RepoDao


    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.repoDao()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun insertAll_then_getAll_returns_inserted_repos() = runTest {
        val repos = listOf(
            repoEntity(id = 1, name = "Repo1"),
            repoEntity(id = 2, name = "Repo2"),
        )

        dao.insertAll(repos)
        val result = dao.getAll()

        assertThat(result.sortedBy { it.id })
            .isEqualTo(repos.sortedBy { it.id })
    }

    @Test
    fun insertAll_replaces_on_conflict_by_primary_key() = runTest {
        val original = repoEntity(id = 10, name = "OldName", fullName = "old/full")
        val updated = repoEntity(id = 10, name = "NewName", fullName = "new/full")

        dao.insertAll(listOf(original))
        dao.insertAll(listOf(updated))

        val result = dao.getAll()

        assertThat(result).hasSize(1)
        assertThat(result.first().id).isEqualTo(10L)
        assertThat(result.first().name).isEqualTo("NewName")
        assertThat(result.first().fullName).isEqualTo("new/full")
    }

    @Test
    fun getAll_returns_empty_list_when_database_is_empty() = runTest {
        val result = dao.getAll()
        assertThat(result).isEmpty()
    }

    private fun repoEntity(
        id: Long,
        name: String,
        fullName: String = "owner/$name",
        isPrivate: Boolean = false,
        htmlUrl: String = "https://github.com/owner/$name",
        ownerLogin: String = "owner",
        ownerAvatarUrl: String = "https://avatars.githubusercontent.com/u/1?v=4",
    ) = RepoEntity(
        id = id,
        name = name,
        fullName = fullName,
        isPrivate = isPrivate,
        htmlUrl = htmlUrl,
        ownerLogin = ownerLogin,
        ownerAvatarUrl = ownerAvatarUrl
    )
}