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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.stormbeats.app.data.model.Song
import com.stormbeats.app.ui.theme.*
import com.stormbeats.app.util.PlayerController
import kotlinx.coroutines.delay

// ── Full-screen player ────────────────────────────────────────────────────────
@Composable
fun PlayerSheet(song: Song, isPlaying: Boolean, onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg),
    ) {
        // Ambient tint from primary colour (no blur needed — API 24+)
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Purple.copy(.12f), Bg, Bg, Bg))))
        // Violet orb top-left
        Box(Modifier.size(240.dp).offset((-60).dp, (-50).dp).clip(CircleShape)
            .background(Brush.radialGradient(listOf(Purple.copy(.2f), Color.Transparent))))
        // Pink orb bottom-right
        Box(Modifier.size(200.dp).align(Alignment.BottomEnd).offset(50.dp, 50.dp).clip(CircleShape)
            .background(Brush.radialGradient(listOf(Pink.copy(.15f), Color.Transparent))))

        PlayerContent(song = song, onDismiss = onDismiss)
    }
}

// ── Player content ────────────────────────────────────────────────────────────
@Composable
fun PlayerContent(song: Song, onDismiss: () -> Unit) {
    val isPlaying  by PlayerController.isPlaying.collectAsState()
    val queue      = remember(song) { PlayerController.getQueue() }

    var posMs       by remember { mutableLongStateOf(0L) }
    var durMs       by remember { mutableLongStateOf(0L) }
    var seeking     by remember { mutableStateOf(false) }
    var shuffle     by remember { mutableStateOf(false) }
    var repeat      by remember { mutableIntStateOf(0) } // 0=off 1=all 2=one
    var liked       by remember { mutableStateOf(false) }
    var showQueue   by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            if (!seeking) { posMs = PlayerController.getCurrentPosition(); durMs = PlayerController.getDuration() }
            delay(300)
        }
    }

    val progress  = if (durMs > 0) (posMs.toFloat() / durMs.toFloat()).coerceIn(0f, 1f) else 0f
    val artScale  by animateFloatAsState(if (isPlaying) 1f else .87f, spring(Spring.DampingRatioMediumBouncy), label = "s")
    val glowAlpha by rememberInfiniteTransition(label = "g").animateFloat(0f, .3f, infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "g")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        // ── Top bar ───────────────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onDismiss) {
                Icon(Icons.Rounded.KeyboardArrowDown, null, tint = OnBg, modifier = Modifier.size(28.dp))
            }
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("NOW PLAYING", style = MaterialTheme.typography.labelSmall, color = PurpleLight, letterSpacing = 1.5.sp, fontSize = 9.sp)
                song.album?.name?.takeIf { it.isNotEmpty() }?.let {
                    Text(it, style = MaterialTheme.typography.labelMedium, color = OnBgSec, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
            IconButton(onClick = {}) {
                Icon(Icons.Rounded.MoreVert, null, tint = OnBgSec, modifier = Modifier.size(20.dp))
            }
        }

        Spacer(Modifier.height(8.dp))

        // ── Album art ─────────────────────────────────────────────────────────
        Box(modifier = Modifier.size((272 * artScale).dp), contentAlignment = Alignment.Center) {
            // Glow ring when playing
            if (isPlaying) {
                Box(
                    modifier = Modifier.size((292 * artScale).dp).clip(RoundedCornerShape(30.dp))
                        .background(Brush.radialGradient(listOf(Purple.copy(glowAlpha), Pink.copy(glowAlpha * .5f), Color.Transparent)))
                )
            }
            // Art
            Box(
                modifier = Modifier
                    .size((272 * artScale).dp)
                    .clip(RoundedCornerShape(22.dp))
                    .border(
                        width = if (isPlaying) 2.dp else 1.dp,
                        brush = Brush.linearGradient(if (isPlaying) listOf(Purple, Pink) else listOf(Surface3, Surface3)),
                        shape = RoundedCornerShape(22.dp),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                val img = song.getImageUrl()
                if (img.isNotEmpty()) {
                    AsyncImage(img, song.name, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                } else {
                    Box(Modifier.fillMaxSize().background(Brush.linearGradient(listOf(Surface1, Surface2))), contentAlignment = Alignment.Center) {
                        Icon(Icons.Rounded.MusicNote, null, modifier = Modifier.size(80.dp), tint = PurpleLight)
                    }
                }
            }
        }

        Spacer(Modifier.height(22.dp))

        // ── Song info ─────────────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 28.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Small artist avatar
            Box(
                modifier = Modifier.size(34.dp).clip(CircleShape).background(Surface2)
                    .border(1.5.dp, Brush.linearGradient(listOf(Purple.copy(.4f), Pink.copy(.3f))), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                val artImg = song.getPrimaryArtistImage()
                if (artImg.isNotEmpty()) AsyncImage(artImg, null, Modifier.fillMaxSize().clip(CircleShape), contentScale = ContentScale.Crop)
                else Icon(Icons.Rounded.Person, null, tint = OnBgTer, modifier = Modifier.size(16.dp))
            }
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(song.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = OnBg, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    if (song.explicitContent) ExplicitBadge()
                    Text(song.getPrimaryArtist(), style = MaterialTheme.typography.bodyMedium, color = OnBgSec, maxLines = 1)
                }
            }
            Spacer(Modifier.width(10.dp))
            // Like button
            LikeButton(liked) { liked = !liked }
        }

        Spacer(Modifier.height(20.dp))

        // ── Seek bar ──────────────────────────────────────────────────────────
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 28.dp)) {
            Slider(
                value = progress,
                onValueChange = { v -> seeking = true; posMs = (v * durMs.coerceAtLeast(1L)).toLong() },
                onValueChangeFinished = { PlayerController.seekTo(posMs); seeking = false },
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor         = Color.White,
                    activeTrackColor   = Purple,
                    inactiveTrackColor = Surface3,
                    activeTickColor    = Color.Transparent,
                    inactiveTickColor  = Color.Transparent,
                ),
            )
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(fmt(posMs), style = MaterialTheme.typography.labelSmall, color = OnBgTer)
                Text(fmt(durMs), style = MaterialTheme.typography.labelSmall, color = OnBgTer)
            }
        }

        Spacer(Modifier.height(14.dp))

        // ── Transport controls ────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Shuffle
            CtrlIcon(
                icon   = Icons.Rounded.Shuffle,
                size   = 44.dp,
                tint   = if (shuffle) Purple else OnBgTer,
                bgBrush = if (shuffle) Brush.linearGradient(listOf(Purple.copy(.15f), Pink.copy(.1f))) else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent)),
                onClick = { shuffle = !shuffle },
            )
            // Prev
            CtrlIcon(Icons.Rounded.SkipPrevious, 52.dp, OnBg, Brush.linearGradient(listOf(Surface1, Surface1))) { PlayerController.playPrevious() }
            // Play / Pause — main button
            Box(
                modifier = Modifier.size(64.dp).clip(CircleShape)
                    .background(Brush.linearGradient(listOf(Purple, Pink)))
                    .clickable { PlayerController.togglePlayPause() },
                contentAlignment = Alignment.Center,
            ) { Icon(if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow, null, tint = Color.White, modifier = Modifier.size(34.dp)) }
            // Next
            CtrlIcon(Icons.Rounded.SkipNext, 52.dp, OnBg, Brush.linearGradient(listOf(Surface1, Surface1))) { PlayerController.playNext() }
            // Repeat
            CtrlIcon(
                icon    = if (repeat == 2) Icons.Rounded.RepeatOne else Icons.Rounded.Repeat,
                size    = 44.dp,
                tint    = if (repeat != 0) Cyan else OnBgTer,
                bgBrush = if (repeat != 0) Brush.linearGradient(listOf(Cyan.copy(.15f), Cyan.copy(.08f))) else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent)),
                onClick = { repeat = (repeat + 1) % 3 },
            )
        }

        Spacer(Modifier.height(22.dp))

        // ── Utility row ───────────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            UtilButton(Icons.Rounded.QueueMusic, "Queue",  PurpleLight) { showQueue = !showQueue }
            UtilButton(Icons.Rounded.Description, "Lyrics", PinkLight)  {}
            UtilButton(Icons.Rounded.DarkMode,   "Sleep",  Color(0xFF60A5FA)) {}
            UtilButton(Icons.Rounded.Equalizer,  "EQ",     Cyan) {}
            UtilButton(Icons.Rounded.Share,       "Share",  OnBgSec) {}
        }

        // ── Up Next queue ─────────────────────────────────────────────────────
        if (showQueue && queue.size > 1) {
            Spacer(Modifier.height(16.dp))
            HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp), thickness = 0.5.dp, color = Surface3)
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("Up Next", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = OnBg, modifier = Modifier.weight(1f))
                TextButton(onClick = { showQueue = false }) { Text("Close", style = MaterialTheme.typography.labelMedium, color = PurpleLight) }
            }
            LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 210.dp)) {
                itemsIndexed(queue.filter { it.id != song.id }.take(5), key = { _, s -> "q${s.id}" }) { _, qs ->
                    QueueRow(qs, onClick = { PlayerController.playSong(qs, queue) })
                }
            }
        }

        Spacer(Modifier.height(10.dp))
    }
}

// ── Sub-composables ───────────────────────────────────────────────────────────
@Composable
private fun CtrlIcon(icon: ImageVector, size: Dp, tint: Color, bgBrush: Brush, onClick: () -> Unit) {
    Box(modifier = Modifier.size(size).clip(CircleShape).background(bgBrush).clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(size * .52f))
    }
}

@Composable
private fun UtilButton(icon: ImageVector, label: String, tint: Color, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier.clickable(onClick = onClick),
    ) {
        Box(
            modifier = Modifier.size(44.dp).clip(RoundedCornerShape(13.dp)).background(Surface1)
                .border(1.dp, Surface3, RoundedCornerShape(13.dp)),
            contentAlignment = Alignment.Center,
        ) { Icon(icon, null, tint = tint, modifier = Modifier.size(18.dp)) }
        Text(label, style = MaterialTheme.typography.labelSmall, color = OnBgTer, fontSize = 10.sp)
    }
}

@Composable
private fun QueueRow(song: Song, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 24.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(modifier = Modifier.size(42.dp).clip(RoundedCornerShape(9.dp)).background(Surface2)) {
            val url = song.getImageUrl()
            if (url.isNotEmpty()) AsyncImage(url, song.name, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            else Icon(Icons.Rounded.MusicNote, null, tint = PurpleLight.copy(.5f), modifier = Modifier.size(18.dp).align(Alignment.Center))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(song.name, style = MaterialTheme.typography.titleSmall, color = OnBg, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(song.getPrimaryArtist(), style = MaterialTheme.typography.bodySmall, color = OnBgSec, maxLines = 1)
        }
        Icon(Icons.Rounded.MoreVert, null, tint = OnBgTer, modifier = Modifier.size(16.dp))
    }
}

@Composable
private fun LikeButton(liked: Boolean, onToggle: () -> Unit) {
    Box(
        modifier = Modifier.size(42.dp).clip(CircleShape)
            .background(if (liked) Brush.linearGradient(listOf(Purple.copy(.2f), Pink.copy(.2f))) else Brush.linearGradient(listOf(Surface1, Surface1)))
            .border(1.dp, if (liked) Brush.linearGradient(listOf(Purple.copy(.6f), Pink.copy(.5f))) else Brush.linearGradient(listOf(Surface3, Surface3)), CircleShape)
            .clickable(onClick = onToggle),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            if (liked) Icons.Rounded.Favorite else Icons.Outlined.FavoriteBorder,
            null,
            tint   = if (liked) Pink else OnBgTer,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun ExplicitBadge() {
    Surface(shape = RoundedCornerShape(3.dp), color = Surface2) {
        Text("E", style = MaterialTheme.typography.labelSmall, color = OnBgTer, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
    }
}

private fun fmt(ms: Long): String { val s = ms / 1000; return "%d:%02d".format(s / 60, s % 60) }
