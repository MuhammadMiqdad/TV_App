package com.example.tvapp.ui.detail

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import coil.compose.AsyncImage
import com.example.tvapp.data.model.Show
import com.example.tvapp.ui.common.ErrorState
import com.example.tvapp.ui.common.LoadingState
import com.example.tvapp.util.buildShareText
import com.example.tvapp.util.seasonEpisodeSummary
import com.example.tvapp.util.stripHtml

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
    val context = LocalContext.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Detail") },
                actions = {
                    val successState = uiState as? ShowDetailUiState.Success
                    if (successState != null) {
                        TextButton(onClick = { shareShow(context, successState.show) }) {
                            Text("Share")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        when (val state = uiState) {
            is ShowDetailUiState.Loading -> {
                LoadingState(modifier = Modifier.padding(innerPadding))
            }

            is ShowDetailUiState.Success -> {
                ShowDetailContent(
                    show = state.show,
                    modifier = Modifier.padding(innerPadding)
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

/**
 * The actual Intent/Context plumbing — deliberately thin. The text it
 * shares comes from [buildShareText], which is the part that's covered
 * by unit tests (this function itself needs a real Context, so it's
 * exercised manually / via the walkthrough video instead).
 */
private fun shareShow(context: Context, show: Show) {
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, show.name)
        putExtra(Intent.EXTRA_TEXT, buildShareText(show))
    }
    context.startActivity(Intent.createChooser(sendIntent, "Share ${show.name}"))
}

@Composable
private fun ShowDetailContent(
    show: Show,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        AsyncImage(
            model = show.image?.original,
            contentDescription = show.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f / 3f)
        )

        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = show.name,
                style = MaterialTheme.typography.headlineSmall
            )

            if (!show.premiered.isNullOrBlank()) {
                Text(
                    text = "Premiered: ${show.premiered}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            val seasonEpisodeText = seasonEpisodeSummary(show.embedded?.episodes)
            if (seasonEpisodeText != null) {
                Text(
                    text = seasonEpisodeText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            val summary = stripHtml(show.summary)
            if (summary.isNotBlank()) {
                Text(
                    text = summary,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            val cast = show.embedded?.cast
            if (!cast.isNullOrEmpty()) {
                Text(
                    text = "Cast",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 20.dp, bottom = 8.dp)
                )
                CastRow(cast = cast)
            }
        }
    }
}
