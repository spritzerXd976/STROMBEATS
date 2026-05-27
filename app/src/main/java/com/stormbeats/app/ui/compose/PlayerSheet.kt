package com.stormbeats.app.ui.compose

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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

// ─────────────────────────────────────────────────────────────────────────────
// Container
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun PlayerSheet(song: Song, isPlaying: Boolean, onDismiss: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().background(SurfaceDark),
    ) {
        // Top gradient tint from album art (static, API 24 safe)
        Box(
            modifier = Modifier.fillMaxSize()
                .background(Brush.verticalGradient(listOf(VioletPrimary.copy(0.15f), SurfaceDark, SurfaceDark)))
        )
        // Ambient orbs
        Box(Modifier.size(260.dp).offset((-50).dp, (-40).dp).clip(CircleShape)
            .background(Brush.radialGradient(listOf(VioletPrimary.copy(0.18f), Color.Transparent))))
        Box(Modifier.size(200.dp).align(Alignment.BottomEnd).offset(40.dp, 40.dp).clip(CircleShape)
            .background(Brush.radialGradient(listOf(PinkAccent.copy(0.14f), Color.Transparent))))

        PlayerContent(song = song, onDismiss = onDismiss)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Content
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun PlayerContent(song: Song, onDismiss: () -> Unit) {
    val isPlaying  by PlayerController.isPlaying.collectAsState()
    val queue      = remember { PlayerController.getQueue() }

    var positionMs    by remember { mutableLongStateOf(0L) }
    var durationMs    by remember { mutableLongStateOf(0L) }
    var isUserSeeking by remember { mutableStateOf(false) }
    var isShuffleOn   by remember { mutableStateOf(false) }
    var repeatMode    by remember { mutableIntStateOf(0) }
    var isLiked       by remember { mutableStateOf(false) }
    var showQueue     by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            if (!isUserSeeking) {
                positionMs = PlayerController.getCurrentPosition()
                durationMs = PlayerController.getDuration()
            }
            delay(300)
        }
    }

    val progress = if (durationMs > 0) positionMs.toFloat() / durationMs.toFloat() else 0f

    // Scale art when paused
    val artScale by animateFloatAsState(
        targetValue = if (isPlaying) 1f else 0.88f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale",
    )
    val glowAlpha by rememberInfiniteTransition(label = "glow").animateFloat(
        0f, 0.35f, infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "gA",
    )

    Column(
        modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        // ── Top bar ──────────────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onDismiss) {
                Icon(Icons.Rounded.KeyboardArrowDown, null, tint = Color.White, modifier = Modifier.size(28.dp))
            }
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("NOW PLAYING", style = MaterialTheme.typography.labelSmall, color = VioletSoft, letterSpacing = 1.5.sp, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                song.album?.name?.takeIf { it.isNotEmpty() }?.let {
                    Text(it, style = MaterialTheme.typography.labelMedium, color = Color(0xFFCCCCDD), maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
            IconButton(onClick = {}) {
                Icon(Icons.Rounded.MoreVert, null, tint = Color(0xFF8888AA), modifier = Modifier.size(20.dp))
            }
        }

        // ── Album art ────────────────────────────────────────────────────────
        Spacer(Modifier.height(12.dp))

        Box(modifier = Modifier.size((280 * artScale).dp), contentAlignment = Alignment.Center) {
            // Glow (API-24-safe, no blur)
            if (isPlaying) {
                Box(
                    modifier = Modifier.size((300 * artScale).dp).clip(RoundedCornerShape(30.dp))
                        .background(Brush.radialGradient(listOf(VioletPrimary.copy(glowAlpha), PinkAccent.copy(glowAlpha * 0.5f), Color.Transparent)))
                )
            }
            Box(
                modifier = Modifier.size((280 * artScale).dp).clip(RoundedCornerShape(24.dp))
                    .border(
                        if (isPlaying) 2.dp else 1.dp,
                        Brush.linearGradient(if (isPlaying) listOf(VioletPrimary, PinkAccent) else listOf(Color(0xFF2A2A40), Color(0xFF2A2A40))),
                        RoundedCornerShape(24.dp),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                val img = song.getImageUrl()
                if (img.isNotEmpty()) {
                    AsyncImage(img, song.name, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                } else {
                    Box(Modifier.fillMaxSize().background(Brush.linearGradient(listOf(Color(0xFF1A0A3A), Color(0xFF2D1566)))), contentAlignment = Alignment.Center) {
                        Icon(Icons.Rounded.MusicNote, null, modifier = Modifier.size(80.dp), tint = VioletSoft)
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // ── Song info + like ─────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 28.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Artist image (small circle)
            Box(
                modifier = Modifier.size(36.dp).clip(CircleShape).background(SurfaceElevated)
                    .border(1.dp, Brush.linearGradient(listOf(VioletPrimary.copy(0.4f), PinkAccent.copy(0.3f))), CircleShape),
            ) {
                val artistImg = song.getPrimaryArtistImage()
                if (artistImg.isNotEmpty()) {
                    AsyncImage(artistImg, null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                } else {
                    Icon(Icons.Rounded.Person, null, tint = Color(0xFF5A5A7A), modifier = Modifier.size(18.dp).align(Alignment.Center))
                }
            }
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(song.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    if (song.explicitContent) {
                        Surface(shape = RoundedCornerShape(3.dp), color = SurfaceElevated) {
                            Text("E", style = MaterialTheme.typography.labelSmall, color = Color(0xFF7777AA), modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                        }
                    }
                    Text(song.getPrimaryArtist(), style = MaterialTheme.typography.bodyMedium, color = Color(0xFF8888BB), maxLines = 1)
                }
            }
            Spacer(Modifier.width(12.dp))
            Box(
                modifier = Modifier.size(44.dp).clip(CircleShape)
                    .background(if (isLiked) Brush.linearGradient(listOf(VioletPrimary.copy(0.2f), PinkAccent.copy(0.2f))) else Brush.linearGradient(listOf(SurfaceElevated, SurfaceElevated)))
                    .border(1.dp, if (isLiked) Brush.linearGradient(listOf(VioletPrimary.copy(0.6f), PinkAccent.copy(0.5f))) else Brush.linearGradient(listOf(Color(0xFF2A2A40), Color(0xFF2A2A40))), CircleShape)
                    .clickable { isLiked = !isLiked },
                contentAlignment = Alignment.Center,
            ) {
                Icon(if (isLiked) Icons.Rounded.Favorite else Icons.Outlined.FavoriteBorder, null, tint = if (isLiked) PinkAccent else Color(0xFF6666AA), modifier = Modifier.size(20.dp))
            }
        }

        Spacer(Modifier.height(20.dp))

        // ── Seekbar ──────────────────────────────────────────────────────────
        Column(modifier = Modifier.padding(horizontal = 28.dp)) {
            Slider(
                value = progress.coerceIn(0f, 1f),
                onValueChange = { v -> isUserSeeking = true; positionMs = (v * durationMs.coerceAtLeast(1L)).toLong() },
                onValueChangeFinished = { PlayerController.seekTo(positionMs); isUserSeeking = false },
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = VioletPrimary,
                    inactiveTrackColor = Color(0xFF252540),
                    activeTickColor = Color.Transparent,
                    inactiveTickColor = Color.Transparent,
                ),
            )
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(fmtMs(positionMs), style = MaterialTheme.typography.labelSmall, color = Color(0xFF7777AA))
                Text(fmtMs(durationMs), style = MaterialTheme.typography.labelSmall, color = Color(0xFF7777AA))
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Transport controls ───────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Shuffle
            CtrlBtn(
                icon = Icons.Rounded.Shuffle, size = 44,
                bg = if (isShuffleOn) Brush.linearGradient(listOf(VioletPrimary.copy(0.18f), PinkAccent.copy(0.12f))) else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent)),
                tint = if (isShuffleOn) VioletSoft else Color(0xFF4A4A6A), iconSz = 20,
                onClick = { isShuffleOn = !isShuffleOn },
            )
            // Prev
            CtrlBtn(Icons.Rounded.SkipPrevious, 54, Brush.linearGradient(listOf(SurfaceElevated, SurfaceElevated)), Color.White, 28) { PlayerController.playPrevious() }
            // Play / Pause — large
            Box(
                modifier = Modifier.size(66.dp).clip(CircleShape)
                    .background(Brush.linearGradient(listOf(VioletPrimary, PinkAccent)))
                    .clickable { PlayerController.togglePlayPause() },
                contentAlignment = Alignment.Center,
            ) { Icon(if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow, null, tint = Color.White, modifier = Modifier.size(36.dp)) }
            // Next
            CtrlBtn(Icons.Rounded.SkipNext, 54, Brush.linearGradient(listOf(SurfaceElevated, SurfaceElevated)), Color.White, 28) { PlayerController.playNext() }
            // Repeat
            CtrlBtn(
                icon = if (repeatMode == 2) Icons.Rounded.RepeatOne else Icons.Rounded.Repeat, size = 44,
                bg = if (repeatMode != 0) Brush.linearGradient(listOf(CyanAccent.copy(0.18f), CyanAccent.copy(0.1f))) else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent)),
                tint = if (repeatMode == 0) Color(0xFF4A4A6A) else CyanAccent, iconSz = 20,
                onClick = { repeatMode = (repeatMode + 1) % 3 },
            )
        }

        Spacer(Modifier.height(22.dp))

        // ── Utility row ──────────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            UtilBtn(Icons.Rounded.QueueMusic, "Queue",  VioletSoft)   { showQueue = !showQueue }
            UtilBtn(Icons.Rounded.DarkMode,   "Sleep",  Color(0xFF60A5FA)) {}
            UtilBtn(Icons.Rounded.Description,"Lyrics", PinkSoft)     {}
            UtilBtn(Icons.Rounded.Equalizer,  "EQ",     CyanAccent)   {}
            UtilBtn(Icons.Rounded.Share,       "Share",  Color(0xFF9999BB)) {}
        }

        // ── Up Next queue ─────────────────────────────────────────────────────
        if (showQueue && queue.size > 1) {
            Spacer(Modifier.height(16.dp))
            HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp), thickness = 0.5.dp, color = Color(0xFF1E1E32))
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("Up Next", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.weight(1f))
                TextButton(onClick = { showQueue = false }) { Text("Close", style = MaterialTheme.typography.labelMedium, color = VioletSoft) }
            }
            val currentId = song.id
            LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 200.dp)) {
                itemsIndexed(queue.filter { it.id != currentId }.take(5), key = { _, s -> "q${s.id}" }) { _, qs ->
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { PlayerController.playSong(qs, queue) }.padding(horizontal = 20.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Box(modifier = Modifier.size(44.dp).clip(RoundedCornerShape(10.dp)).background(SurfaceElevated)) {
                            val qUrl = qs.getImageUrl()
                            if (qUrl.isNotEmpty()) AsyncImage(qUrl, qs.name, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                            else Icon(Icons.Rounded.MusicNote, null, tint = VioletSoft.copy(0.5f), modifier = Modifier.size(20.dp).align(Alignment.Center))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(qs.name, style = MaterialTheme.typography.titleSmall, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(qs.getPrimaryArtist(), style = MaterialTheme.typography.bodySmall, color = Color(0xFF7777AA), maxLines = 1)
                        }
                        Icon(Icons.Rounded.MoreVert, null, tint = Color(0xFF44445A), modifier = Modifier.size(17.dp))
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Sub-composables
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun CtrlBtn(icon: ImageVector, size: Int, bg: Brush, tint: Color, iconSz: Int, onClick: () -> Unit) {
    Box(modifier = Modifier.size(size.dp).clip(CircleShape).background(bg).clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(iconSz.dp))
    }
}

@Composable
private fun UtilBtn(icon: ImageVector, label: String, tint: Color, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.clickable(onClick = onClick),
    ) {
        Box(
            modifier = Modifier.size(46.dp).clip(RoundedCornerShape(13.dp)).background(SurfaceCard)
                .border(1.dp, Brush.linearGradient(listOf(Color(0xFF2A2A40), Color(0xFF1E1E32))), RoundedCornerShape(13.dp)),
            contentAlignment = Alignment.Center,
        ) { Icon(icon, null, tint = tint, modifier = Modifier.size(19.dp)) }
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color(0xFF5555AA), fontSize = 10.sp)
    }
}

private fun fmtMs(ms: Long): String {
    val s = ms / 1000
    return "%d:%02d".format(s / 60, s % 60)
}
