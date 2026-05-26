package com.stormbeats.app.ui.compose

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.stormbeats.app.data.model.Song
import com.stormbeats.app.util.PlayerController

@Composable
fun MiniPlayerBar(
    song: Song,
    isPlaying: Boolean,
    onExpandClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing)),
        label = "shimmer",
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            .clickable(onClick = onExpandClick),
    ) {
        // Shimmer progress indicator at top
        if (isPlaying) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .align(Alignment.TopCenter)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.primary,
                                Color.Transparent,
                            ),
                            startX = shimmerOffset * 900f - 200f,
                            endX   = shimmerOffset * 900f + 200f,
                        )
                    )
            )
        }

        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Album art
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center,
            ) {
                val imageUrl = song.getImageUrl()
                if (imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = song.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Rounded.MusicNote,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    song.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    song.getPrimaryArtist(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Spacer(Modifier.width(4.dp))

            // Play / Pause
            IconButton(
                onClick = { PlayerController.togglePlayPause() },
                modifier = Modifier.size(40.dp),
            ) {
                Icon(
                    if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(26.dp),
                )
            }

            // Skip Next
            IconButton(
                onClick = { PlayerController.playNext() },
                modifier = Modifier.size(40.dp),
            ) {
                Icon(
                    Icons.Rounded.SkipNext,
                    contentDescription = "Next",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp),
                )
            }
        }
    }
}
