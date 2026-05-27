package com.stormbeats.app.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.stormbeats.app.data.model.Song
import com.stormbeats.app.ui.theme.Purple100
import com.stormbeats.app.ui.theme.Purple500
import com.stormbeats.app.util.PlayerController
import kotlinx.coroutines.delay

@Composable
fun MiniPlayerBar(
    song: Song,
    isPlaying: Boolean,
    onExpandClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var posMs by remember { mutableLongStateOf(0L) }
    var durMs by remember { mutableLongStateOf(1L) }
    LaunchedEffect(song) {
        while (true) {
            posMs = PlayerController.getCurrentPosition()
            durMs = PlayerController.getDuration().coerceAtLeast(1L)
            delay(500)
        }
    }
    val progress = (posMs.toFloat() / durMs.toFloat()).coerceIn(0f, 1f)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .shadow(8.dp, RoundedCornerShape(20.dp))
            .clickable(onClick = onExpandClick),
        shape  = RoundedCornerShape(20.dp),
        color  = MaterialTheme.colorScheme.surface,
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Album art
                Box(Modifier.size(46.dp).clip(RoundedCornerShape(12.dp))) {
                    val img = song.getImageUrl()
                    if (img.isNotEmpty()) {
                        AsyncImage(img, song.name, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    } else {
                        Box(Modifier.fillMaxSize().background(Purple100), contentAlignment = Alignment.Center) {
                            Icon(Icons.Rounded.MusicNote, null, tint = Purple500)
                        }
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(song.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onBackground, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(song.getPrimaryArtist(), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                IconButton(onClick = { PlayerController.playPrevious() }, Modifier.size(36.dp)) {
                    Icon(Icons.Rounded.SkipPrevious, null, tint = MaterialTheme.colorScheme.onBackground, modifier = Modifier.size(22.dp))
                }
                Box(
                    Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Purple500)
                        .clickable { PlayerController.togglePlayPause() },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        null, tint = Color.White, modifier = Modifier.size(22.dp),
                    )
                }
                IconButton(onClick = { PlayerController.playNext() }, Modifier.size(36.dp)) {
                    Icon(Icons.Rounded.SkipNext, null, tint = MaterialTheme.colorScheme.onBackground, modifier = Modifier.size(22.dp))
                }
            }
            // Progress at bottom
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(2.dp),
                color      = Purple500,
                trackColor = MaterialTheme.colorScheme.outline,
            )
        }
    }
}
