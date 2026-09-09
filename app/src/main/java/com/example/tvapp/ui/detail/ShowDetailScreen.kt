package com.example.tvapp.ui.detail

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import coil.compose.AsyncImage
import com.example.tvapp.data.model.Show
import com.example.tvapp.ui.common.ErrorState
import com.example.tvapp.ui.common.InfoChip
import com.example.tvapp.ui.common.LoadingState
import com.example.tvapp.util.buildShareText
import com.example.tvapp.util.seasonEpisodeSummary
import com.example.tvapp.util.stripHtml


private val HeroHeight = 380.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowDetailScreen(
    showId: Int,
    onBack: () -> Unit = {},
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
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text(
                            text = "\u2190 Back",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                actions = {
                    val successState = uiState as? ShowDetailUiState.Success
                    if (successState != null) {
                        TextButton(
                            onClick = { shareShow(context, successState.show) }
                        ) {
                            Text(
                                text = "⤄ Share",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
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
        HeroPoster(show = show)

        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!show.premiered.isNullOrBlank()) {
                    InfoChip(text = "Premiered ${show.premiered}")
                }
                seasonEpisodeSummary(show.embedded?.episodes)?.let { summaryText ->
                    InfoChip(text = summaryText)
                }
            }

            val summary = stripHtml(show.summary)
            if (summary.isNotBlank()) {
                Text(
                    text = summary,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 18.dp)
                )
            }

            val cast = show.embedded?.cast
            if (!cast.isNullOrEmpty()) {
                HorizontalDivider(
                    modifier = Modifier.padding(top = 24.dp, bottom = 20.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                )
                Text(
                    text = "Cast",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                CastRow(cast = cast)
            }
        }
    }
}

@Composable
private fun HeroPoster(show: Show) {
    val backgroundColor = MaterialTheme.colorScheme.background

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(HeroHeight)
    ) {
        AsyncImage(
            model = show.image?.original,
            contentDescription = show.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            backgroundColor.copy(alpha = 0f),
                            backgroundColor.copy(alpha = 0.55f),
                            backgroundColor
                        ),
                        startY = 0f
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            Text(
                text = show.name,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )

            val average = show.rating?.average
            if (average != null) {
                Text(
                    text = "\u2605 $average",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }
    }
}

private fun shareShow(context: Context, show: Show) {
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, show.name)
        putExtra(Intent.EXTRA_TEXT, buildShareText(show))
    }
    context.startActivity(Intent.createChooser(sendIntent, "Share ${show.name}"))
}
