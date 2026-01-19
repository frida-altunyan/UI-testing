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

    @Before
    fun setup() {
        hiltRule.inject()
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
        val repo = EntryPointAccessors.fromApplication(
            composeRule.activity.applicationContext,
            RepoTestEntryPoint::class.java
        ).repoRepository() as FakeRepoRepository

        repo.shouldReturnError = true

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

}
