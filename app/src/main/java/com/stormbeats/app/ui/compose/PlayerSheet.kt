package com.stormbeats.app.ui.compose

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.IosShare
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.stormbeats.app.data.model.Song
import com.stormbeats.app.util.PlayerController
import kotlinx.coroutines.delay

@Composable
fun PlayerSheet(
    song: Song,
    isPlaying: Boolean,
    onDismiss: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A))
    ) {
        // Blurred full-screen album art bg
        val imageUrl = song.getImageUrl()
        if (imageUrl.isNotEmpty()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(80.dp),
                contentScale = ContentScale.Crop,
            )
        }
        // Heavy dark scrim
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xE6000000),
                            Color(0xCC000000),
                            Color(0xF2000000),
                        )
                    )
                )
        )

        PlayerContent(
            song = song,
            isPlaying = isPlaying,
            onDismiss = onDismiss,
        )
    }
}

@Composable
fun PlayerContent(song: Song, isPlaying: Boolean, onDismiss: () -> Unit) {
    var positionMs    by remember { mutableLongStateOf(0L) }
    var durationMs    by remember { mutableLongStateOf(0L) }
    var isUserSeeking by remember { mutableStateOf(false) }
    var isShuffleOn   by remember { mutableStateOf(false) }
    var repeatMode    by remember { mutableIntStateOf(0) }
    var isLiked       by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            if (!isUserSeeking) {
                positionMs = PlayerController.getCurrentPosition()
                durationMs = PlayerController.getDuration()
            }
            delay(500)
        }
    }

    val isPlayingState by PlayerController.isPlaying.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        // ── Header ──────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onDismiss) {
                Icon(Icons.Rounded.KeyboardArrowDown, null, tint = Color.White, modifier = Modifier.size(32.dp))
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    "NOW PLAYING",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF888888),
                    letterSpacing = 2.sp,
                    fontSize = 9.sp,
                )
                song.album?.name?.takeIf { it.isNotEmpty() }?.let {
                    Text(
                        it,
                        style     = MaterialTheme.typography.labelMedium,
                        color     = Color(0xFFCCCCCC),
                        maxLines  = 1,
                        overflow  = TextOverflow.Ellipsis,
                    )
                }
            }
            IconButton(onClick = {}) {
                Icon(Icons.Rounded.MoreVert, null, tint = Color.White, modifier = Modifier.size(24.dp))
            }
        }

        Spacer(Modifier.height(12.dp))

        // ── Album Art ───────────────────────────────────────────────
        val artScale by animateFloatAsState(
            targetValue = if (isPlayingState) 1f else 0.88f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
            label = "artScale",
        )
        Box(
            modifier = Modifier
                .size((300 * artScale).dp)
                .clip(RoundedCornerShape(20.dp)),
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
                        .background(
                            Brush.linearGradient(listOf(Color(0xFF1A0000), Color(0xFF3A0000)))
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Rounded.MusicNote, null, modifier = Modifier.size(80.dp), tint = Color(0xFFFF0000))
                }
            }
            // Red glow overlay when playing
            if (isPlayingState) {
                val glowAlpha by rememberInfiniteTransition(label = "glow").animateFloat(
                    0f, 0.15f,
                    infiniteRepeatable(tween(1500), RepeatMode.Reverse),
                    label = "glowA",
                )
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color(0xFFFF0000).copy(alpha = glowAlpha))
                )
            }
        }

        Spacer(Modifier.height(28.dp))

        // ── Song Info + Actions ─────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    song.name,
                    style      = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color      = Color.White,
                    maxLines   = 1,
                    overflow   = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    if (song.explicitContent) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFF2A2A2A),
                        ) {
                            Text("E", style = MaterialTheme.typography.labelSmall, color = Color(0xFF888888), modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp))
                        }
                    }
                    Text(
                        song.getPrimaryArtist(),
                        style    = MaterialTheme.typography.bodyMedium,
                        color    = Color(0xFFAAAAAA),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            // Like button
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (isLiked) Color(0x33FF0000) else Color(0xFF1A1A1A))
                    .clickable { isLiked = !isLiked },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    if (isLiked) Icons.Rounded.Favorite else Icons.Outlined.FavoriteBorder,
                    null,
                    tint = if (isLiked) Color(0xFFFF0000) else Color(0xFF888888),
                    modifier = Modifier.size(22.dp),
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // ── Seekbar ─────────────────────────────────────────────────
        val progress = if (durationMs > 0) positionMs.toFloat() / durationMs.toFloat() else 0f
        Column(modifier = Modifier.padding(horizontal = 28.dp)) {
            Slider(
                value = progress,
                onValueChange = { v ->
                    isUserSeeking = true
                    positionMs = (v * durationMs).toLong()
                },
                onValueChangeFinished = {
                    PlayerController.seekTo(positionMs)
                    isUserSeeking = false
                },
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor         = Color.White,
                    activeTrackColor   = Color.White,
                    inactiveTrackColor = Color(0xFF333333),
                ),
            )
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(formatTime(positionMs), style = MaterialTheme.typography.labelSmall, color = Color(0xFF888888))
                Text(formatTime(durationMs), style = MaterialTheme.typography.labelSmall, color = Color(0xFF888888))
            }
        }

        Spacer(Modifier.height(20.dp))

        // ── Playback Controls ───────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Shuffle
            IconButton(onClick = { isShuffleOn = !isShuffleOn }, modifier = Modifier.size(52.dp)) {
                Icon(
                    Icons.Rounded.Shuffle, null,
                    tint = if (isShuffleOn) Color(0xFFFF0000) else Color(0xFF555555),
                    modifier = Modifier.size(24.dp),
                )
            }
            // Previous
            IconButton(onClick = { PlayerController.playPrevious() }, modifier = Modifier.size(60.dp)) {
                Icon(Icons.Rounded.SkipPrevious, null, modifier = Modifier.size(40.dp), tint = Color.White)
            }
            // Play/Pause pill
            Box(
                modifier = Modifier
                    .size(width = 100.dp, height = 60.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .background(Color.White)
                    .clickable { PlayerController.togglePlayPause() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    if (isPlayingState) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    null, tint = Color.Black, modifier = Modifier.size(40.dp),
                )
            }
            // Next
            IconButton(onClick = { PlayerController.playNext() }, modifier = Modifier.size(60.dp)) {
                Icon(Icons.Rounded.SkipNext, null, modifier = Modifier.size(40.dp), tint = Color.White)
            }
            // Repeat
            IconButton(onClick = { repeatMode = (repeatMode + 1) % 3 }, modifier = Modifier.size(52.dp)) {
                Icon(
                    when (repeatMode) { 2 -> Icons.Rounded.RepeatOne; else -> Icons.Rounded.Repeat },
                    null,
                    tint = if (repeatMode == 0) Color(0xFF555555) else Color(0xFFFF0000),
                    modifier = Modifier.size(24.dp),
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // ── Bottom Utility Row ──────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            listOf(
                Icons.Rounded.QueueMusic  to "Queue",
                Icons.Rounded.DarkMode    to "Sleep",
                Icons.Rounded.TextSnippet to "Lyrics",
                Icons.Rounded.Equalizer   to "EQ",
                Icons.Outlined.IosShare   to "Share",
            ).forEach { (icon, label) ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.clickable {},
                ) {
                    Box(
                        Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1A1A1A)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(icon, null, tint = Color(0xFF888888), modifier = Modifier.size(20.dp))
                    }
                    Text(label, style = MaterialTheme.typography.labelSmall, color = Color(0xFF555555), fontSize = 9.sp)
                }
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

private fun formatTime(ms: Long): String {
    val secs = ms / 1000
    return "%d:%02d".format(secs / 60, secs % 60)
}
