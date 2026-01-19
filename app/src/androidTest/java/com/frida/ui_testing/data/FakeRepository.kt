package com.frida.ui_testing.data

import com.frida.ui_testing.domain.model.Item
import com.frida.ui_testing.domain.model.Owner
import com.frida.ui_testing.domain.model.Repo
import com.frida.ui_testing.domain.repo.RepoRepository

class FakeRepoRepository : RepoRepository {

    var shouldReturnError = false

    override suspend fun searchRepositories(query: String): Repo {
        if (shouldReturnError) {
            throw RuntimeException("Network error")
        }

        return Repo(
            totalCount = 1,
            incompleteResults = false,
            items = listOf(
                Item(
                    id = 1L,
                    name = "repo",
                    fullName = "user/repo",
                    htmlUrl = "https://github.com/user/repo",
                    owner = Owner(
                        login = "user",
                        id = 1L,
                        avatarUrl = "https://example.com/avatar.png",
                        nodeId = "MDQ6VXNlcjE=",
                        gravatarId = "https://example.com/avatar.png",
                        url = "https://example.com/avatar.png",
                        htmlUrl = "https://example.com/avatar.png",
                        followersUrl = "https://example.com/avatar.png"
                    ),
                    nodeId = "MDQ6VXNlcjE=",
                    private = false

                )
            )
        )
    }
}
