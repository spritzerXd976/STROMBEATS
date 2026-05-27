package com.stormbeats.app.ui.compose

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.stormbeats.app.data.model.Song
import com.stormbeats.app.ui.theme.*
import com.stormbeats.app.util.PlayerController
import kotlinx.coroutines.delay

@Composable
fun PlayerSheet(
    song: Song,
    isPlaying: Boolean,
    queue: List<Song>,
    onDismiss: () -> Unit,
) {
    var posMs         by remember { mutableLongStateOf(0L) }
    var durMs         by remember { mutableLongStateOf(0L) }
    var isSeeking     by remember { mutableStateOf(false) }
    var isShuffleOn   by remember { mutableStateOf(false) }
    var repeatMode    by remember { mutableIntStateOf(0) }
    var isLiked       by remember { mutableStateOf(false) }
    val isPlayingLive by PlayerController.isPlaying.collectAsState()

    LaunchedEffect(Unit) {
        while (true) {
            if (!isSeeking) {
                posMs = PlayerController.getCurrentPosition()
                durMs = PlayerController.getDuration()
            }
            delay(300)
        }
    }

    // Art scale: big when playing, small when paused
    val artScale by animateFloatAsState(
        if (isPlayingLive) 1f else 0.80f,
        spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
        label = "artScale",
    )

    // White background full screen — exactly like reference image
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        // Subtle top glow behind album art
        Box(
            Modifier
                .fillMaxWidth()
                .height(380.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Purple500.copy(alpha = 0.10f),
                            Color.Transparent,
                        )
                    )
                )
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 32.dp),
        ) {
            // ── Header ───────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 4.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Rounded.ArrowBack, "Close", tint = MaterialTheme.colorScheme.onBackground, modifier = Modifier.size(24.dp))
                    }
                    Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "NOW PLAYING",
                            style = MaterialTheme.typography.labelSmall,
                            color = Purple500,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp,
                            fontSize = 9.sp,
                        )
                        Text(
                            song.name,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Rounded.MoreVert, null, tint = MaterialTheme.colorScheme.onBackground, modifier = Modifier.size(22.dp))
                    }
                }
            }

            // ── Album Art ────────────────────────────────────────
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 28.dp)
                        .aspectRatio(1f)
                        .graphicsLayer(scaleX = artScale, scaleY = artScale)
                        .shadow(16.dp, RoundedCornerShape(24.dp))
                        .clip(RoundedCornerShape(24.dp)),
                ) {
                    val img = song.getImageUrl()
                    if (img.isNotEmpty()) {
                        AsyncImage(img, song.name, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    } else {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(Brush.linearGradient(listOf(Purple100, MaterialTheme.colorScheme.primaryContainer))),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Rounded.MusicNote, null, Modifier.size(80.dp), tint = Purple500)
                        }
                    }
                }
                Spacer(Modifier.height(28.dp))
            }

            // ── Song Info + Like ─────────────────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 28.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            song.name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            song.getPrimaryArtist(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                        )
                    }
                    Box(
                        Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (isLiked) Purple100 else MaterialTheme.colorScheme.surfaceContainerHigh)
                            .clickable { isLiked = !isLiked },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            if (isLiked) Icons.Rounded.Favorite else Icons.Outlined.FavoriteBorder,
                            null,
                            tint = if (isLiked) PinkAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(22.dp),
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
            }

            // ── Waveform-style progress bar ───────────────────────
            item {
                val progress = if (durMs > 0) posMs.toFloat() / durMs.toFloat() else 0f
                Column(modifier = Modifier.padding(horizontal = 28.dp)) {
                    // Waveform visual (decorative bars)
                    WaveformBar(progress = progress)
                    Spacer(Modifier.height(4.dp))
                    // Actual slider
                    Slider(
                        value = progress,
                        onValueChange = { v ->
                            isSeeking = true
                            posMs = (v * durMs).toLong()
                        },
                        onValueChangeFinished = {
                            PlayerController.seekTo(posMs)
                            isSeeking = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = SliderDefaults.colors(
                            thumbColor         = Purple500,
                            activeTrackColor   = Purple500,
                            inactiveTrackColor = MaterialTheme.colorScheme.outline,
                        ),
                    )
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(fmtMs(posMs), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(fmtMs(durMs), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Spacer(Modifier.height(20.dp))
            }

            // ── Controls ─────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = { isShuffleOn = !isShuffleOn }, Modifier.size(52.dp)) {
                        Icon(Icons.Rounded.Shuffle, null, tint = if (isShuffleOn) Purple500 else MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(24.dp))
                    }
                    IconButton(onClick = { PlayerController.playPrevious() }, Modifier.size(58.dp)) {
                        Icon(Icons.Rounded.SkipPrevious, null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.onBackground)
                    }
                    // Large purple circle play/pause — matches reference exactly
                    Box(
                        Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(Purple500)
                            .clickable { PlayerController.togglePlayPause() },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            if (isPlayingLive) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                            null, tint = Color.White, modifier = Modifier.size(40.dp),
                        )
                    }
                    IconButton(onClick = { PlayerController.playNext() }, Modifier.size(58.dp)) {
                        Icon(Icons.Rounded.SkipNext, null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.onBackground)
                    }
                    IconButton(onClick = { repeatMode = (repeatMode + 1) % 3 }, Modifier.size(52.dp)) {
                        Icon(
                            when (repeatMode) { 2 -> Icons.Rounded.RepeatOne; else -> Icons.Rounded.Repeat },
                            null,
                            tint = if (repeatMode == 0) MaterialTheme.colorScheme.onSurfaceVariant else Purple500,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
            }

            // ── Bottom utility row ────────────────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    listOf(
                        Icons.Rounded.QueueMusic  to "Queue",
                        Icons.Rounded.TextSnippet to "Lyrics",
                        Icons.Rounded.DarkMode    to "Sleep Timer",
                        Icons.Rounded.Equalizer   to "Equalizer",
                        Icons.Rounded.Share       to "Share",
                    ).forEach { (icon, label) ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.clickable {},
                        ) {
                            Box(
                                Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(icon, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                            }
                            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, fontSize = 9.sp)
                        }
                    }
                }
                Spacer(Modifier.height(28.dp))
            }

            // ── Up Next ───────────────────────────────────────────
            if (queue.isNotEmpty()) {
                item {
                    Text(
                        "Up Next",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(horizontal = 20.dp),
                    )
                    Spacer(Modifier.height(8.dp))
                }
                itemsIndexed(queue.take(5)) { _, qSong ->
                    UpNextItem(song = qSong, isCurrent = qSong.id == song.id)
                }
            }
        }
    }
}

@Composable
private fun WaveformBar(progress: Float) {
    val heights = remember { listOf(6, 10, 15, 8, 12, 18, 10, 14, 9, 16, 11, 7, 13, 18, 12, 9, 15, 7, 11, 16, 8, 14, 10, 17, 9, 13, 6, 12, 18, 8) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(28.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        heights.forEachIndexed { i, h ->
            val isActive = i.toFloat() / heights.size < progress
            Box(
                Modifier
                    .width(3.dp)
                    .height(h.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (isActive) Purple500 else MaterialTheme.colorScheme.outline)
            )
        }
    }
}

@Composable
private fun UpNextItem(song: Song, isCurrent: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { PlayerController.playSong(song) }
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(48.dp).clip(RoundedCornerShape(10.dp))) {
            val img = song.getImageUrl()
            if (img.isNotEmpty()) {
                AsyncImage(img, song.name, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            } else {
                Box(Modifier.fillMaxSize().background(Purple100), contentAlignment = Alignment.Center) {
                    Icon(Icons.Rounded.MusicNote, null, tint = Purple500, modifier = Modifier.size(22.dp))
                }
            }
            if (isCurrent) {
                Box(Modifier.fillMaxSize().background(Color(0x55000000)))
                Icon(Icons.Rounded.VolumeUp, null, tint = Color.White, modifier = Modifier.size(18.dp).align(Alignment.Center))
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(song.name, style = MaterialTheme.typography.titleSmall, fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium, color = if (isCurrent) Purple500 else MaterialTheme.colorScheme.onBackground, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(song.getPrimaryArtist(), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
        }
        IconButton(onClick = {}, Modifier.size(32.dp)) {
            Icon(Icons.Rounded.MoreVert, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
        }
    }
}

private fun fmtMs(ms: Long): String {
    val s = ms / 1000
    return "%d:%02d".format(s / 60, s % 60)
}
