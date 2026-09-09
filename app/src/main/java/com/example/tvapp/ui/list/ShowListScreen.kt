package com.example.tvapp.ui.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowListScreen(
    onShowClick: (showId: Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ShowListViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text("TV Shows") }) }
    ) { innerPadding ->
        when (val state = uiState) {
            is ShowListUiState.Loading -> {
                // Minimal placeholder for now — a dedicated loading UI
                // (with retry-friendly error state alongside it) lands
                // in the next commit.
                CircularProgressIndicator(
                    modifier = Modifier.padding(innerPadding)
                )
            }

            is ShowListUiState.Success -> {
                LazyColumn(
                    modifier = Modifier.padding(innerPadding),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(state.shows, key = { it.id }) { show ->
                        ShowListItem(
                            show = show,
                            onClick = { onShowClick(show.id) }
                        )
                    }
                }
            }

            is ShowListUiState.Error -> {
                // Also a placeholder — retry button comes in the next commit.
                Text(
                    text = state.message,
                    modifier = Modifier.padding(innerPadding),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
