package com.example.tvapp.ui.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.tvapp.data.model.Rating
import com.example.tvapp.data.model.Show
import com.example.tvapp.data.model.ShowImage
import com.example.tvapp.ui.theme.TVAppTheme

private val PosterCornerRadius = 14.dp
private val CardCornerRadius = 18.dp

@Composable
fun ShowListItem(
    show: Show,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CardCornerRadius),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PosterWithRatingBadge(show = show)

            Column(
                modifier = Modifier
                    .padding(start = 14.dp)
                    .weight(1f)
            ) {
                Text(
                    text = show.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (!show.premiered.isNullOrBlank()) {
                    Text(
                        text = show.premiered.take(4), // just the year — a quiet detail, not a duplicate of Detail's full date
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PosterWithRatingBadge(show: Show) {
    Box {
        AsyncImage(
            model = show.image?.medium,
            contentDescription = show.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(width = 84.dp, height = 116.dp)
                .clip(RoundedCornerShape(PosterCornerRadius))
        )

        val average = show.rating?.average
        if (average != null) {
            Text(
                text = "\u2605 $average",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(6.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(horizontal = 6.dp, vertical = 3.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ShowListItemPreview() {
    TVAppTheme {
        ShowListItem(
            show = Show(
                id = 1,
                url = null,
                name = "Under the Dome",
                premiered = "2013-06-24",
                rating = Rating(average = 6.6),
                image = ShowImage(medium = null, original = null),
                summary = null
            ),
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ShowListItemNoRatingPreview() {
    TVAppTheme {
        ShowListItem(
            show = Show(
                id = 2,
                url = null,
                name = "A Brand New Show With No Ratings Yet",
                premiered = null,
                rating = null,
                image = ShowImage(medium = null, original = null),
                summary = null
            ),
            onClick = {}
        )
    }
}
