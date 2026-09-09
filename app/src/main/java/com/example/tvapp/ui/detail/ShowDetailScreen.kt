package com.example.tvapp.ui.detail

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.tvapp.ui.common.ErrorState
import com.example.tvapp.ui.common.LoadingState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowDetailScreen(
    showId: Int,
    modifier: Modifier = Modifier,
    viewModel: ShowDetailViewModel = viewModel(
        factory = viewModelFactory {
            initializer { ShowDetailViewModel(showId = showId) }
        }
    )
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text("Detail") }) }
    ) { innerPadding ->
        when (val state = uiState) {
            is ShowDetailUiState.Loading -> {
                LoadingState(modifier = Modifier.padding(innerPadding))
            }

            is ShowDetailUiState.Success -> {
                Text(
                    text = state.show.name,
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(16.dp),
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            is ShowDetailUiState.Error -> {
                ErrorState(
                    message = state.message,
                    onRetry = { viewModel.loadShowDetail() },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}
