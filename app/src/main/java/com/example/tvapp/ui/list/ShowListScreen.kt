package com.example.tvapp.ui.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tvapp.ui.common.ErrorState
import com.example.tvapp.ui.common.LoadingState
import com.example.tvapp.util.filterShows

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowListScreen(
    onShowClick: (showId: Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ShowListViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("TV Shows", style = MaterialTheme.typography.titleLarge) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground
                    )
                )

                // Search only filters the page of shows already loaded below,
                // it never triggers another network call.
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = viewModel::onSearchQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Search shows\u2026") },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        }
    ) { innerPadding ->
        when (val state = uiState) {
            is ShowListUiState.Loading -> {
                LoadingState(modifier = Modifier.padding(innerPadding))
            }

            is ShowListUiState.Success -> {
                val filteredShows = remember(state.shows, searchQuery) {
                    filterShows(state.shows, searchQuery)
                }

                if (filteredShows.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No shows found for \"$searchQuery\"",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.padding(innerPadding),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredShows, key = { it.id }) { show ->
                            ShowListItem(
                                show = show,
                                onClick = { onShowClick(show.id) }
                            )
                        }
                    }
                }
            }

            is ShowListUiState.Error -> {
                ErrorState(
                    message = state.message,
                    onRetry = { viewModel.loadShows() },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}
