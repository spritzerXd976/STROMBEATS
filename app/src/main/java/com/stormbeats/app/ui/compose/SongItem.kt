package com.stormbeats.app.ui.compose

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
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
    val bg by animateColorAsState(if (isPlaying) Purple.copy(.07f) else Color.Transparent, tween(300), label = "bg")

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Album art
        Box(modifier = Modifier.size(52.dp).clip(RoundedCornerShape(10.dp)).background(Surface1), contentAlignment = Alignment.Center) {
            val url = song.getImageUrl()
            if (url.isNotEmpty()) AsyncImage(url, song.name, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            else Box(Modifier.fillMaxSize().background(Surface2), contentAlignment = Alignment.Center) {
                Icon(Icons.Rounded.MusicNote, null, tint = OnBgTer, modifier = Modifier.size(22.dp))
            }
            if (isPlaying) {
                val alpha by rememberInfiniteTransition(label = "pa").animateFloat(.2f, .5f, infiniteRepeatable(tween(700), RepeatMode.Reverse), label = "a")
                Box(Modifier.fillMaxSize().background(Purple.copy(alpha)))
                Icon(Icons.Rounded.VolumeUp, null, tint = Color.White, modifier = Modifier.size(16.dp))
            }
        }
        Spacer(Modifier.width(13.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                song.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = if (isPlaying) FontWeight.Bold else FontWeight.Medium,
                color = if (isPlaying) PurpleLight else OnBg,
                maxLines = 1, overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                if (song.explicitContent) {
                    Surface(shape = RoundedCornerShape(3.dp), color = Surface2) {
                        Text("E", style = MaterialTheme.typography.labelSmall, color = OnBgTer, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                    }
                }
                Text(song.getPrimaryArtist(), style = MaterialTheme.typography.bodySmall, color = OnBgSec, maxLines = 1, overflow = TextOverflow.Ellipsis)
                song.album?.name?.takeIf { it.isNotEmpty() }?.let { n ->
                    Text("·", style = MaterialTheme.typography.bodySmall, color = OnBgTer)
                    Text(n, style = MaterialTheme.typography.bodySmall, color = OnBgTer, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }
        Spacer(Modifier.width(8.dp))
        song.duration?.let { d ->
            Text("%d:%02d".format(d / 60, d % 60), style = MaterialTheme.typography.labelSmall, color = OnBgTer)
            Spacer(Modifier.width(4.dp))
        }
        IconButton(onClick = {}, modifier = Modifier.size(30.dp)) {
            Icon(Icons.Rounded.MoreVert, null, tint = OnBgTer, modifier = Modifier.size(16.dp))
        }
    }
}
