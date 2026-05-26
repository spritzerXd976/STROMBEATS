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
        targetValue = if (isPlaying) Color(0x14FF0000) else Color.Transparent,
        animationSpec = tween(300),
        label = "bg",
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 10.dp),
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
                    Modifier.fillMaxSize().background(Color(0xFF1A1A1A)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Rounded.MusicNote, null, tint = Color(0xFF444444), modifier = Modifier.size(24.dp))
                }
            }

            // Playing overlay
            if (isPlaying) {
                val alpha by rememberInfiniteTransition(label = "glow").animateFloat(
                    0.2f, 0.5f,
                    infiniteRepeatable(tween(700), RepeatMode.Reverse),
                    label = "glowA",
                )
                Box(Modifier.fillMaxSize().background(Color(0xFFFF0000).copy(alpha = alpha)))
                Icon(Icons.Rounded.VolumeUp, null, tint = Color.White, modifier = Modifier.size(18.dp))
            }
        }

        Spacer(Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                song.name,
                style      = MaterialTheme.typography.titleSmall,
                fontWeight = if (isPlaying) FontWeight.Bold else FontWeight.Medium,
                color      = if (isPlaying) Color(0xFFFF0000) else Color.White,
                maxLines   = 1,
                overflow   = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(2.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                if (song.explicitContent) {
                    Surface(shape = RoundedCornerShape(3.dp), color = Color(0xFF1E1E1E)) {
                        Text("E", style = MaterialTheme.typography.labelSmall, color = Color(0xFF666666), modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                    }
                }
                Text(
                    song.getPrimaryArtist(),
                    style    = MaterialTheme.typography.bodySmall,
                    color    = Color(0xFF777777),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                song.album?.name?.takeIf { it.isNotEmpty() }?.let { albumName ->
                    Text("·", style = MaterialTheme.typography.bodySmall, color = Color(0xFF3A3A3A))
                    Text(albumName, style = MaterialTheme.typography.bodySmall, color = Color(0xFF555555), maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }

        Spacer(Modifier.width(8.dp))

        song.duration?.let { dur ->
            Text(
                "%d:%02d".format(dur / 60, dur % 60),
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF444444),
            )
            Spacer(Modifier.width(4.dp))
        }

        IconButton(onClick = {}, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Rounded.MoreVert, null, tint = Color(0xFF444444), modifier = Modifier.size(18.dp))
        }
    }
}
