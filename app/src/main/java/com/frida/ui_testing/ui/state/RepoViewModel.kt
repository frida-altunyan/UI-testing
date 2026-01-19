package com.frida.ui_testing.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.frida.ui_testing.domain.model.Repo
import com.frida.ui_testing.domain.usecase.SearchRepositoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.annotation.concurrent.Immutable
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException


sealed interface RepoAction {
    data class Search(val query: String) : RepoAction
    object LoadCached : RepoAction
}

sealed interface RepoEffect {
    data class ShowError(val message: String) : RepoEffect
    object ScrollToTop : RepoEffect
}

@Immutable
data class RepoState(
    val isLoading: Boolean = false,
    val data: Repo? = null,
    val error: String? = null
)

@HiltViewModel
class RepoViewModel @Inject constructor(
    private val searchRepositoryUseCase: SearchRepositoryUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(RepoState())
    val state: StateFlow<RepoState> = _state.asStateFlow()

    private val _effect = Channel<RepoEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        handleAction(RepoAction.LoadCached)
    }

    fun handleAction(action: RepoAction) {
        when (action) {
            is RepoAction.Search -> search(action.query)
            RepoAction.LoadCached -> loadCached()
        }
    }

    private fun search(query: String) {
        if (query.isBlank()) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            runCatching {
                searchRepositoryUseCase(query)
            }.onSuccess { repo ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        data = repo
                    )
                }

                _effect.send(RepoEffect.ScrollToTop)

            }.onFailure { throwable ->
                _state.update { it.copy(isLoading = false) }
                _effect.send(
                    RepoEffect.ShowError(
                        throwable.message ?: "Something went wrong"
                    )
                )
            }
        }
    }

    private fun loadCached() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            runCatching {
                searchRepositoryUseCase("")
            }.onSuccess { repo ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        data = repo
                    )
                }
            }.onFailure { throwable ->
                if (throwable is CancellationException) throw throwable

                _state.update { it.copy(isLoading = false) }
                _effect.send(
                    RepoEffect.ShowError(
                        throwable.message ?: "Failed to load cache"
                    )
                )
            }
        }
    }
}