package com.stormbeats.app.ui.compose

import androidx.compose.animation.animateColorAsState
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
import com.stormbeats.app.ui.theme.*

@Composable
fun SongItem(
    song: Song,
    isPlaying: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bgColor by animateColorAsState(
        targetValue = if (isPlaying) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else Color.Transparent,
        animationSpec = tween(300),
        label = "songBg",
    )

    ListItem(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = ListItemDefaults.colors(
            containerColor = bgColor,
        ),
        headlineContent = {
            Text(
                text = song.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = if (isPlaying) FontWeight.Bold else FontWeight.Medium,
                color = if (isPlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        supportingContent = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                if (song.explicitContent) {
                    Surface(shape = RoundedCornerShape(3.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                        Text("E", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                    }
                }
                Text(song.getPrimaryArtist(), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                song.album?.name?.takeIf { it.isNotEmpty() }?.let { albumName ->
                    Text("·", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                    Text(albumName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        },
        leadingContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isPlaying) {
                    val infiniteTransition = rememberInfiniteTransition(label = "activeBar")
                    val barAlpha by infiniteTransition.animateFloat(
                        0.5f, 1f,
                        infiniteRepeatable(tween(800), RepeatMode.Reverse),
                        label = "barA",
                    )
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .height(44.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = barAlpha))
                    )
                    Spacer(Modifier.width(10.dp))
                }

                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center,
                ) {
                    val imageUrl = song.getImageUrl()
                    if (imageUrl.isNotEmpty()) {
                        AsyncImage(model = imageUrl, contentDescription = song.name, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    } else {
                        Icon(Icons.Rounded.MusicNote, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                    }

                    // Playing overlay with equalizer
                    if (isPlaying) {
                        val infiniteTransition = rememberInfiniteTransition(label = "eq")
                        val b1 by infiniteTransition.animateFloat(0.3f, 1f, infiniteRepeatable(tween(380), RepeatMode.Reverse), label = "sb1")
                        val b2 by infiniteTransition.animateFloat(0.5f, 0.8f, infiniteRepeatable(tween(500), RepeatMode.Reverse), label = "sb2")
                        val b3 by infiniteTransition.animateFloat(0.2f, 0.9f, infiniteRepeatable(tween(420), RepeatMode.Reverse), label = "sb3")
                        Box(
                            Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface.copy(alpha = 0.55f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                                verticalAlignment = Alignment.Bottom,
                                modifier = Modifier.height(16.dp),
                            ) {
                                listOf(b1, b2, b3).forEach { h ->
                                    Box(
                                        modifier = Modifier
                                            .width(3.dp)
                                            .fillMaxHeight(h)
                                            .clip(RoundedCornerShape(1.dp))
                                            .background(Brush.verticalGradient(listOf(MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.primary)))
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        trailingContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                song.duration?.let { dur ->
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        Icon(Icons.Rounded.MusicNote, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(10.dp))
                        Text("%d:%02d".format(dur / 60, dur % 60), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(Modifier.width(8.dp))
                }
                IconButton(onClick = {}) {
                    Icon(Icons.Rounded.MoreVert, contentDescription = "More", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    )
}
