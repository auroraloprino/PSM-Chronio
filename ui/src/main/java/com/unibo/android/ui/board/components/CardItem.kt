package com.unibo.android.ui.board.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.unibo.android.domain.models.CardModel

@Composable
fun CardItem(
    card: CardModel,
    onToggleDone: () -> Unit = {},
    modifier: Modifier = Modifier,
    elevation: Dp = 1.dp
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .alpha(if (card.isDone) 0.55f else 1f),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 4.dp),
            verticalAlignment = Alignment.Top
        ) {
            Checkbox(
                checked = card.isDone,
                onCheckedChange = { onToggleDone() }
            )
            Column(modifier = Modifier.padding(top = 12.dp, end = 8.dp)) {
                Text(
                    text = card.title,
                    style = MaterialTheme.typography.bodyLarge,
                    textDecoration = if (card.isDone) TextDecoration.LineThrough else null,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (card.description.isNotBlank()) {
                    Text(
                        text = card.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textDecoration = if (card.isDone) TextDecoration.LineThrough else null,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                if (card.tags.isNotEmpty()) {
                    Row(
                        modifier = Modifier.padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        card.tags.take(5).forEach { tag ->
                            Surface(
                                color = tag.color.toColorOrDefault(),
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier.size(width = 20.dp, height = 6.dp)
                            ) {}
                        }
                        if (card.tags.size > 5) {
                            Text(
                                text = "+${card.tags.size - 5}",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
        }
    }
}
