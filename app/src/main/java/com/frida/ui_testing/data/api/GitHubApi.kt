package com.frida.ui_testing.data.api

import com.frida.ui_testing.data.dto.RepoDto
import retrofit2.http.GET
import retrofit2.http.Query

interface GitHubApi {
    @GET("search/repositories")
    suspend fun searchRepositories(
        @Query("q") query: String
    ): RepoDto
}