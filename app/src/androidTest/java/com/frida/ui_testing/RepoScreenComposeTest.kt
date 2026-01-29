package com.frida.ui_testing

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.frida.ui_testing.data.FakeRepoRepository
import com.frida.ui_testing.di.RepoModule
import com.frida.ui_testing.di.RepoTestEntryPoint
import com.frida.ui_testing.ui.MainActivity
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
@UninstallModules(RepoModule::class)
class RepoScreenComposeTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private lateinit var repoRepository: FakeRepoRepository

    @Before
    fun setup() {
        hiltRule.inject()
        repoRepository = EntryPointAccessors.fromApplication(
            composeRule.activity.applicationContext,
            RepoTestEntryPoint::class.java
        ).repoRepository() as FakeRepoRepository
    }

    @Test
    fun search_displaysRepoList() {
        composeRule
            .onNodeWithTag("search_input")
            .performTextInput("repo")

        composeRule
            .onNodeWithTag("search_button")
            .performClick()

        composeRule
            .onNodeWithTag("repo_list")
            .assertIsDisplayed()

        composeRule
            .onNodeWithText("user/repo")
            .assertIsDisplayed()
    }


    @Test
    fun error_showsSnackbar() {
        repoRepository.shouldReturnError = true

        composeRule
            .onNodeWithTag("search_input")
            .performTextInput("repo")

        composeRule
            .onNodeWithTag("search_button")
            .performClick()

        composeRule
            .onNodeWithText("Network error")
            .assertIsDisplayed()
    }

    @Test
    fun search_emptyResults_showsEmptyState() {
        repoRepository.items = emptyList()

        composeRule
            .onNodeWithTag("search_input")
            .performTextInput("nonexistent")

        composeRule
            .onNodeWithTag("search_button")
            .performClick()

        composeRule
            .onNodeWithTag("empty_state")
            .assertIsDisplayed()

        composeRule
            .onNodeWithText("No data")
            .assertIsDisplayed()
    }

    @Test
    fun search_emptyQuery_doesNotTriggerSearch() {
        val initialSearchCount = repoRepository.searchCount

        composeRule
            .onNodeWithTag("search_button")
            .performClick()

        assertEquals(initialSearchCount, repoRepository.searchCount)
    }

    @Test
    fun repoListItem_displaysDetails() {
        composeRule
            .onNodeWithTag("search_input")
            .performTextInput("repo")

        composeRule
            .onNodeWithTag("search_button")
            .performClick()

        composeRule
            .onNodeWithText("user")
            .assertIsDisplayed()

        composeRule
            .onNodeWithText("https://github.com/user/repo")
            .assertIsDisplayed()
    }
}
