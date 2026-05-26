package com.stormbeats.app.ui.compose

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
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
    onDismiss: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceDark)
    ) {
        // Blurred full-screen album art bg
        val imageUrl = song.getImageUrl()
        if (imageUrl.isNotEmpty()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(100.dp),
                contentScale = ContentScale.Crop,
            )
        }
        // Heavy dark scrim with violet tint
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xF00D0D14),
                            Color(0xCC0D0D14),
                            Color(0xF50D0D14),
                        )
                    )
                )
        )
        // Ambient violet orb top-left
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-80).dp, y = (-80).dp)
                .blur(120.dp)
                .background(VioletPrimary.copy(alpha = 0.3f), CircleShape)
        )
        // Pink orb bottom-right
        Box(
            modifier = Modifier
                .size(250.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 60.dp, y = 60.dp)
                .blur(100.dp)
                .background(PinkAccent.copy(alpha = 0.25f), CircleShape)
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
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(SurfaceElevated.copy(alpha = 0.6f))
                    .clickable(onClick = onDismiss),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Rounded.KeyboardArrowDown, null, tint = Color.White, modifier = Modifier.size(24.dp))
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    "NOW PLAYING",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF6666AA),
                    letterSpacing = 2.sp,
                    fontSize = 9.sp,
                )
                song.album?.name?.takeIf { it.isNotEmpty() }?.let {
                    Text(
                        it,
                        style     = MaterialTheme.typography.labelMedium,
                        color     = Color(0xFFCCCCDD),
                        maxLines  = 1,
                        overflow  = TextOverflow.Ellipsis,
                    )
                }
            }
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(SurfaceElevated.copy(alpha = 0.6f))
                    .clickable {},
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Rounded.MoreVert, null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }

        Spacer(Modifier.height(20.dp))

        // ── Album Art ───────────────────────────────────────────────
        val artScale by animateFloatAsState(
            targetValue = if (isPlayingState) 1f else 0.88f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
            label = "artScale",
        )

        val infiniteTransition = rememberInfiniteTransition(label = "glow")
        val glowAlpha by infiniteTransition.animateFloat(
            0f, 0.4f,
            infiniteRepeatable(tween(1800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
            label = "glowA",
        )

        Box(
            modifier = Modifier
                .size((290 * artScale).dp),
            contentAlignment = Alignment.Center,
        ) {
            // Glow ring behind art
            if (isPlayingState) {
                Box(
                    modifier = Modifier
                        .size((300 * artScale).dp)
                        .blur(30.dp)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    VioletPrimary.copy(alpha = glowAlpha),
                                    PinkAccent.copy(alpha = glowAlpha * 0.6f),
                                    Color.Transparent,
                                )
                            ),
                            CircleShape,
                        )
                )
            }
            Box(
                modifier = Modifier
                    .size((290 * artScale).dp)
                    .clip(RoundedCornerShape(28.dp))
                    .border(
                        width = if (isPlayingState) 2.dp else 1.dp,
                        brush = Brush.linearGradient(
                            if (isPlayingState)
                                listOf(VioletPrimary, PinkAccent)
                            else
                                listOf(Color(0xFF2E2E4A), Color(0xFF1E1E32))
                        ),
                        shape = RoundedCornerShape(28.dp),
                    ),
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
                                Brush.linearGradient(listOf(Color(0xFF1A0A3A), Color(0xFF2D1566)))
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Rounded.MusicNote, null, modifier = Modifier.size(80.dp), tint = VioletSoft)
                    }
                }
            }
        }

        Spacer(Modifier.height(32.dp))

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
                            color = SurfaceElevated,
                        ) {
                            Text("E", style = MaterialTheme.typography.labelSmall, color = Color(0xFF7777AA), modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp))
                        }
                    }
                    Text(
                        song.getPrimaryArtist(),
                        style    = MaterialTheme.typography.bodyMedium,
                        color    = Color(0xFF8888BB),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            // Like button
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(
                        if (isLiked)
                            Brush.linearGradient(listOf(VioletPrimary.copy(0.2f), PinkAccent.copy(0.2f)))
                        else
                            Brush.linearGradient(listOf(SurfaceElevated, SurfaceElevated))
                    )
                    .border(
                        1.dp,
                        if (isLiked)
                            Brush.linearGradient(listOf(VioletPrimary.copy(0.5f), PinkAccent.copy(0.5f)))
                        else
                            Brush.linearGradient(listOf(Color(0xFF2E2E4A), Color(0xFF2E2E4A))),
                        CircleShape,
                    )
                    .clickable { isLiked = !isLiked },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    if (isLiked) Icons.Rounded.Favorite else Icons.Outlined.FavoriteBorder,
                    null,
                    tint = if (isLiked) PinkAccent else Color(0xFF6666AA),
                    modifier = Modifier.size(22.dp),
                )
            }
        }

        Spacer(Modifier.height(28.dp))

        // ── Seekbar ─────────────────────────────────────────────────
        val progress = if (durationMs > 0) positionMs.toFloat() / durationMs.toFloat() else 0f
        Column(modifier = Modifier.padding(horizontal = 28.dp)) {
            // Custom styled slider track
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
                    activeTrackColor   = VioletPrimary,
                    inactiveTrackColor = Color(0xFF2A2A3E),
                    activeTickColor    = Color.Transparent,
                    inactiveTickColor  = Color.Transparent,
                ),
            )
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(formatTime(positionMs), style = MaterialTheme.typography.labelSmall, color = Color(0xFF6666AA))
                Text(formatTime(durationMs), style = MaterialTheme.typography.labelSmall, color = Color(0xFF6666AA))
            }
        }

        Spacer(Modifier.height(24.dp))

        // ── Playback Controls ───────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Shuffle
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        if (isShuffleOn)
                            Brush.linearGradient(listOf(VioletPrimary.copy(0.2f), PinkAccent.copy(0.15f)))
                        else
                            Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
                    )
                    .clickable { isShuffleOn = !isShuffleOn },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Rounded.Shuffle, null,
                    tint = if (isShuffleOn) VioletSoft else Color(0xFF4A4A6A),
                    modifier = Modifier.size(22.dp),
                )
            }
            // Previous
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(SurfaceElevated.copy(0.5f))
                    .clickable { PlayerController.playPrevious() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Rounded.SkipPrevious, null, modifier = Modifier.size(32.dp), tint = Color.White)
            }
            // Play/Pause — big gradient pill
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(VioletPrimary, PinkAccent)))
                    .clickable { PlayerController.togglePlayPause() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    if (isPlayingState) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    null, tint = Color.White, modifier = Modifier.size(36.dp),
                )
            }
            // Next
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(SurfaceElevated.copy(0.5f))
                    .clickable { PlayerController.playNext() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Rounded.SkipNext, null, modifier = Modifier.size(32.dp), tint = Color.White)
            }
            // Repeat
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        if (repeatMode != 0)
                            Brush.linearGradient(listOf(CyanAccent.copy(0.2f), CyanAccent.copy(0.1f)))
                        else
                            Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
                    )
                    .clickable { repeatMode = (repeatMode + 1) % 3 },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    when (repeatMode) { 2 -> Icons.Rounded.RepeatOne; else -> Icons.Rounded.Repeat },
                    null,
                    tint = if (repeatMode == 0) Color(0xFF4A4A6A) else CyanAccent,
                    modifier = Modifier.size(22.dp),
                )
            }
        }

        Spacer(Modifier.height(28.dp))

        // ── Bottom Utility Row ──────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            listOf(
                Triple(Icons.Rounded.QueueMusic,  "Queue",  VioletSoft),
                Triple(Icons.Rounded.DarkMode,    "Sleep",  Color(0xFF60A5FA)),
                Triple(Icons.Rounded.TextSnippet, "Lyrics", PinkSoft),
                Triple(Icons.Rounded.Equalizer,   "EQ",     CyanAccent),
                Triple(Icons.Outlined.IosShare,   "Share",  Color(0xFF9999BB)),
            ).forEach { (icon, label, tintColor) ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    modifier = Modifier.clickable {},
                ) {
                    Box(
                        Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(SurfaceCard)
                            .border(
                                1.dp,
                                Brush.linearGradient(listOf(Color(0xFF2E2E4A), Color(0xFF1E1E32))),
                                RoundedCornerShape(14.dp),
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(icon, null, tint = tintColor, modifier = Modifier.size(20.dp))
                    }
                    Text(label, style = MaterialTheme.typography.labelSmall, color = Color(0xFF5555AA), fontSize = 10.sp)
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
