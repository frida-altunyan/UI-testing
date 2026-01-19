package com.frida.ui_testing.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.frida.ui_testing.domain.model.Item
import com.frida.ui_testing.ui.state.RepoAction
import com.frida.ui_testing.ui.state.RepoEffect
import com.frida.ui_testing.ui.state.RepoViewModel
import com.frida.ui_testing.ui.theme.UITestingTheme
import com.frida.ui_testing.ui.utils.ObserveAsEvents
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UITestingTheme {
                RepoScreen()
            }
        }
    }
}

@Composable
fun RepoScreen() {
    val viewModel: RepoViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    var query by rememberSaveable { mutableStateOf("") }

    val snackBarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()

    ObserveAsEvents(viewModel.effect) { effect ->
        when (effect) {
            is RepoEffect.ShowError ->
                snackBarHostState.showSnackbar(effect.message)

            RepoEffect.ScrollToTop ->
                listState.animateScrollToItem(0)
        }
    }

    Scaffold(
        modifier = Modifier.testTag("repo_screen_root"),
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState,
                modifier = Modifier.testTag("snackbar")
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag("search_input"),
                placeholder = { Text("Search repositories") },
                singleLine = true
            )

            Button(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .testTag("search_button"),
                onClick = {
                    viewModel.handleAction(
                        RepoAction.Search(query)
                    )
                }
            ) {
                Text("Search")
            }

            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("loading_indicator"),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                state.data?.items?.isNotEmpty() == true -> {
                    state.data?.let {
                        RepoList(
                            items = it.items,
                            listState = listState
                        )
                    }
                }

                else -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("empty_state"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No data")
                    }
                }
            }
        }
    }
}


@Composable
private fun RepoList(
    items: List<Item>,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    onItemClick: (Item) -> Unit = {}
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("repo_list"),
        state = listState,
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(
            items = items,
            key = { it.id }
        ) { item ->
            RepoListItem(
                item = item,
                onClick = { onItemClick(item) }
            )
        }
    }
}


@Composable
private fun RepoListItem(
    item: Item,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
            .testTag("repo_list_item_${item.id}")
    ) {
        Text(
            text = item.fullName,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.testTag("repo_item_title_${item.id}")
        )

        item.owner?.let {
            Text(
                text = it.login,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.testTag("repo_item_owner_${item.id}")
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = item.htmlUrl,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.testTag("repo_item_url_${item.id}")
        )
    }
}


@Preview
@Composable
private fun RepoListItemPreview() {
    UITestingTheme {
        RepoListItem(
            item = Item(
                id = 1L,
                nodeId = "MDQ6VXNlcjE=",
                name = "example-repo",
                fullName = "example-user/example-repo",
                private = false,
                owner = com.frida.ui_testing.domain.model.Owner(
                    login = "example-user",
                    id = 1L,
                    nodeId = "MDQ6VXNlcjE=",
                    avatarUrl = "https://example.com/avatar.png",
                    gravatarId = "",
                    url = "https://api.example.com/users/example-user",
                    htmlUrl = "https://example.com/example-user",
                    followersUrl = "https://api.example.com/users/example-user/followers"
                ),
                htmlUrl = "https://example.com/example-user/example-repo"
            ),
            onClick = {}
        )
    }
}
