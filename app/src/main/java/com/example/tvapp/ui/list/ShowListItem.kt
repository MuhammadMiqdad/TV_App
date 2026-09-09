package com.example.tvapp.ui.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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

@Composable
fun ShowListItem(
    show: Show,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = show.image?.medium,
                contentDescription = show.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(width = 80.dp, height = 110.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Column(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .weight(1f)
            ) {
                Text(
                    text = show.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    modifier = Modifier.padding(top = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RatingLabel(rating = show.rating)
                }
            }
        }
    }
}

@Composable
private fun RowScope.RatingLabel(rating: Rating?) {
    val average = rating?.average
    Text(
        text = if (average != null) "\u2605 $average" else "No rating",
        style = MaterialTheme.typography.bodyMedium,
        color = if (average != null) {
            MaterialTheme.colorScheme.onSurface
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        }
    )
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
