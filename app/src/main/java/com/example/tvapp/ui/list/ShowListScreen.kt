package com.example.tvapp.ui.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tvapp.ui.common.ErrorState
import com.example.tvapp.ui.common.LoadingState

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
                LoadingState(modifier = Modifier.padding(innerPadding))
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
                ErrorState(
                    message = state.message,
                    onRetry = { viewModel.loadShows() },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}