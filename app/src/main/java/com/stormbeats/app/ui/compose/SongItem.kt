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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.stormbeats.app.data.model.Song

@Composable
fun SongItem(
    song: Song,
    isPlaying: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bgColor by animateColorAsState(
        targetValue = if (isPlaying)
            MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
        else
            Color.Transparent,
        animationSpec = tween(300),
        label = "bgColor",
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Album art
        Box(
            modifier = Modifier
                .size(54.dp)
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
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Rounded.MusicNote,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp),
                    )
                }
            }

            // Playing overlay
            if (isPlaying) {
                val infiniteTransition = rememberInfiniteTransition(label = "glow")
                val alpha by infiniteTransition.animateFloat(
                    initialValue = 0.2f, targetValue = 0.5f,
                    animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse),
                    label = "glow",
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = alpha))
                )
                Icon(
                    Icons.Rounded.VolumeUp,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp),
                )
            }
        }

        Spacer(Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                song.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = if (isPlaying) FontWeight.Bold else FontWeight.Medium,
                color = if (isPlaying) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(2.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                if (song.explicitContent) {
                    Surface(
                        shape = RoundedCornerShape(3.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    ) {
                        Text(
                            "E",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                        )
                    }
                }
                Text(
                    song.getPrimaryArtist(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                song.album?.name?.takeIf { it.isNotEmpty() }?.let { albumName ->
                    Text("·", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                    Text(
                        albumName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }

        Spacer(Modifier.width(8.dp))

        song.duration?.let { dur ->
            Text(
                "%d:%02d".format(dur / 60, dur % 60),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
            )
            Spacer(Modifier.width(6.dp))
        }

        IconButton(onClick = {}, modifier = Modifier.size(32.dp)) {
            Icon(
                Icons.Rounded.MoreVert,
                contentDescription = "More",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(18.dp),
            )
        }
    }
}
