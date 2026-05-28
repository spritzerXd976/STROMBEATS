package com.stormbeats.app.ui.compose

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.stormbeats.app.util.PlayerController

@Composable
fun MiniPlayerBar(
    song: Song,
    isPlaying: Boolean,
    onExpandClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val inf = rememberInfiniteTransition(label = "mp")
    val shimmerX by inf.animateFloat(-600f, 1200f, infiniteRepeatable(tween(2400, easing = LinearEasing)), label = "sx")
    val breathe  by inf.animateFloat(.65f, 1f,  infiniteRepeatable(tween(1100), RepeatMode.Reverse), label = "br")

    var posMs by remember { mutableLongStateOf(0L) }
    var durMs by remember { mutableLongStateOf(1L) }
    LaunchedEffect(song) {
        while (true) {
            posMs = PlayerController.getCurrentPosition()
            durMs = PlayerController.getDuration().coerceAtLeast(1L)
            kotlinx.coroutines.delay(500)
        }
    }
    val prog = (posMs.toFloat() / durMs.toFloat()).coerceIn(0f, 1f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Surface0)
            .border(1.dp, if (isPlaying) Brush.linearGradient(listOf(Purple.copy(.5f), Pink.copy(.4f))) else Brush.linearGradient(listOf(Surface3, Surface3)), RoundedCornerShape(18.dp))
            .clickable(onClick = onExpandClick),
    ) {
        // Progress line at bottom
        Box(Modifier.fillMaxWidth(prog).height(2.dp).align(Alignment.BottomStart).background(Brush.horizontalGradient(listOf(Purple, Pink))))
        // Shimmer at top when playing
        if (isPlaying) {
            Box(
                Modifier.fillMaxWidth().height(1.dp).align(Alignment.TopCenter).background(
                    Brush.horizontalGradient(listOf(Color.Transparent, Purple.copy(.8f), Pink.copy(.6f), Color.Transparent), startX = shimmerX, endX = shimmerX + 600f)
                )
            )
        }
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 9.dp), verticalAlignment = Alignment.CenterVertically) {
            // Art with breathing glow border
            Box(modifier = Modifier.size(50.dp), contentAlignment = Alignment.Center) {
                if (isPlaying) {
                    val sz = (50 * (.93f + breathe * .12f)).dp
                    Box(Modifier.size(sz).clip(RoundedCornerShape(12.dp))
                        .background(Brush.linearGradient(listOf(Purple.copy(breathe * .25f), Pink.copy(breathe * .18f)))))
                }
                Box(
                    Modifier.size(48.dp).clip(RoundedCornerShape(11.dp))
                        .border(1.dp, Brush.linearGradient(if (isPlaying) listOf(Purple.copy(.5f), Pink.copy(.4f)) else listOf(Surface3, Surface3)), RoundedCornerShape(11.dp)),
                ) {
                    val url = song.getImageUrl()
                    if (url.isNotEmpty()) AsyncImage(url, song.name, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    else Box(Modifier.fillMaxSize().background(Surface2), contentAlignment = Alignment.Center) {
                        Icon(Icons.Rounded.MusicNote, null, tint = PurpleLight, modifier = Modifier.size(22.dp))
                    }
                }
            }
            Spacer(Modifier.width(11.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(song.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = OnBg, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(song.getPrimaryArtist(), style = MaterialTheme.typography.bodySmall, color = OnBgSec, maxLines = 1)
            }
            Spacer(Modifier.width(6.dp))
            // Prev
            Box(Modifier.size(32.dp).clip(CircleShape).background(Surface2).clickable { PlayerController.playPrevious() }, contentAlignment = Alignment.Center) {
                Icon(Icons.Rounded.SkipPrevious, null, tint = OnBgSec, modifier = Modifier.size(17.dp))
            }
            Spacer(Modifier.width(7.dp))
            // Play/Pause
            Box(
                modifier = Modifier.size(44.dp).clip(CircleShape).background(Brush.linearGradient(listOf(Purple, Pink))).clickable { PlayerController.togglePlayPause() },
                contentAlignment = Alignment.Center,
            ) { Icon(if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow, null, tint = Color.White, modifier = Modifier.size(22.dp)) }
            Spacer(Modifier.width(7.dp))
            // Next
            Box(Modifier.size(32.dp).clip(CircleShape).background(Surface2).clickable { PlayerController.playNext() }, contentAlignment = Alignment.Center) {
                Icon(Icons.Rounded.SkipNext, null, tint = OnBgSec, modifier = Modifier.size(17.dp))
            }
        }
    }
}
