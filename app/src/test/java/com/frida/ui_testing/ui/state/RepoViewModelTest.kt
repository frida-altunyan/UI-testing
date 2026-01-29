@file:OptIn(ExperimentalCoroutinesApi::class)

package com.frida.ui_testing.ui.state

import app.cash.turbine.test
import com.frida.ui_testing.domain.model.Repo
import com.frida.ui_testing.domain.usecase.SearchRepositoryUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RepoViewModelTest {

    private val useCase: SearchRepositoryUseCase = mockk()

    private val dispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun fakeRepo(total: Long = 1L): Repo =
        Repo(totalCount = total, incompleteResults = false, items = emptyList())

    @Test
    fun `init triggers LoadCached and updates state with cached repo`() = runTest(dispatcher) {
        val cached = fakeRepo(total = 3L)
        coEvery { useCase.invoke("") } returns cached

        val vm = RepoViewModel(useCase)

        vm.state.test {
            assertThat(awaitItem()).isEqualTo(RepoState())

            assertThat(awaitItem()).isEqualTo(RepoState(isLoading = true))

            val final = awaitItem()
            assertThat(final.isLoading).isFalse()
            assertThat(final.data).isEqualTo(cached)
            assertThat(final.error).isNull()

            advanceUntilIdle()

            cancelAndIgnoreRemainingEvents()
        }

        coVerify(exactly = 1) { useCase.invoke("") }
        confirmVerified(useCase)
    }

    @Test
    fun `handleAction Search with blank query does nothing`() = runTest(dispatcher) {
        coEvery { useCase.invoke("") } returns fakeRepo(total = 0L)

        val vm = RepoViewModel(useCase)

        vm.handleAction(RepoAction.Search("   "))
        advanceUntilIdle()

        coVerify(exactly = 1) { useCase.invoke("") }
        coVerify(exactly = 0) { useCase.invoke("   ") }
        confirmVerified(useCase)
    }

    @Test
    fun `Search success updates state and emits ScrollToTop effect`() = runTest(dispatcher) {
        coEvery { useCase.invoke("") } returns fakeRepo(total = 0L)

        val resultRepo = fakeRepo(total = 7L)
        coEvery { useCase.invoke("query") } returns resultRepo

        val vm = RepoViewModel(useCase)

        advanceUntilIdle()

        vm.effect.test {
            vm.handleAction(RepoAction.Search("query"))
            advanceUntilIdle()

            assertThat(awaitItem()).isEqualTo(RepoEffect.ScrollToTop)
            cancelAndIgnoreRemainingEvents()
        }

        assertThat(vm.state.value.isLoading).isFalse()
        assertThat(vm.state.value.data).isEqualTo(resultRepo)
        assertThat(vm.state.value.error).isNull()

        coVerify(exactly = 1) { useCase.invoke("") }
        coVerify(exactly = 1) { useCase.invoke("query") }
        confirmVerified(useCase)
    }

    @Test
    fun `Search failure sets loading false and emits ShowError with throwable message`() =
        runTest(dispatcher) {
            coEvery { useCase.invoke("") } returns fakeRepo(total = 0L)

            val error = RuntimeException("error")
            coEvery { useCase.invoke("query") } throws error

            val vm = RepoViewModel(useCase)

            vm.effect.test {
                advanceUntilIdle()
                expectNoEvents()

                vm.handleAction(RepoAction.Search("query"))
                advanceUntilIdle()

                val effect = awaitItem()
                assertThat(effect).isEqualTo(RepoEffect.ShowError("error"))

                cancelAndIgnoreRemainingEvents()
            }

            advanceUntilIdle()
            assertThat(vm.state.value.isLoading).isFalse()

            coVerify(exactly = 1) { useCase.invoke("") }
            coVerify(exactly = 1) { useCase.invoke("query") }
            confirmVerified(useCase)
        }

    @Test
    fun `LoadCached failure (non-cancellation) emits ShowError and stops loading`() =
        runTest(dispatcher) {
            val error = IllegalStateException("cache broken")
            coEvery { useCase.invoke("") } throws error

            val vm = RepoViewModel(useCase)

            vm.effect.test {
                advanceUntilIdle()

                val effect = awaitItem()
                assertThat(effect).isEqualTo(RepoEffect.ShowError("cache broken"))

                cancelAndIgnoreRemainingEvents()
            }

            assertThat(vm.state.value.isLoading).isFalse()

            coVerify(exactly = 1) { useCase.invoke("") }
            confirmVerified(useCase)
        }
}
