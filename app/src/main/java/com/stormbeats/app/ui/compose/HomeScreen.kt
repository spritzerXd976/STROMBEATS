package com.stormbeats.app.ui.compose

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.stormbeats.app.data.model.ArtistResult
import com.stormbeats.app.data.model.Song
import com.stormbeats.app.ui.home.HomeViewModel
import com.stormbeats.app.ui.theme.*
import com.stormbeats.app.util.PlayerController
import com.stormbeats.app.util.UpdateManager
import kotlinx.coroutines.launch

// ── Mood chip data ────────────────────────────────────────────────────────────
private data class MoodData(val label: String, val icon: ImageVector, val tint: Color, val bg: Color)
private val MOODS = listOf(
    MoodData("Workout",  Icons.Rounded.DirectionsRun, Color(0xFFFF6B6B), Color(0xFF2A0A0A)),
    MoodData("Energize", Icons.Rounded.Bolt,          Color(0xFFFBBF24), Color(0xFF1A1200)),
    MoodData("Relax",    Icons.Rounded.Spa,           Color(0xFF34D399), Color(0xFF001A10)),
    MoodData("Vibes",    Icons.Rounded.MusicNote,     VioletSoft,        Color(0xFF150A2A)),
    MoodData("Chill",    Icons.Rounded.NightsStay,    CyanAccent,        Color(0xFF001A20)),
    MoodData("Focus",    Icons.Rounded.Headphones,    PinkSoft,          Color(0xFF200A14)),
)

// ─────────────────────────────────────────────────────────────────────────────
// HomeScreen
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun HomeScreen(
    onShowPlayer: () -> Unit,
    vm: HomeViewModel = viewModel(),
) {
    val trending    by vm.trending.collectAsState()
    val newReleases by vm.newReleases.collectAsState()
    val recentSongs by vm.recentSongs.collectAsState()
    val artists     by vm.artists.collectAsState()
    val isLoading   by vm.isLoading.collectAsState()

    val currentSong by PlayerController.currentSong.collectAsState()
    val isPlaying   by PlayerController.isPlaying.collectAsState()
    val context     = LocalContext.current
    val scope       = rememberCoroutineScope()

    var showUpdate   by remember { mutableStateOf(false) }
    var updateResult by remember { mutableStateOf<UpdateManager.UpdateResult.UpdateAvailable?>(null) }
    var selectedMood by remember { mutableIntStateOf(-1) }

    LaunchedEffect(Unit) {
        scope.launch {
            val r = UpdateManager.checkForUpdate(context)
            if (r is UpdateManager.UpdateResult.UpdateAvailable) { updateResult = r; showUpdate = true }
        }
    }

    if (showUpdate && updateResult != null) {
        AlertDialog(
            onDismissRequest = { showUpdate = false },
            containerColor = SurfaceCard,
            icon = { Icon(Icons.Rounded.SystemUpdate, null, tint = VioletSoft) },
            title = { Text("Update Available", color = Color.White, fontWeight = FontWeight.Bold) },
            text  = {
                Text(
                    "v${updateResult!!.release.tagName} is ready\n\n${updateResult!!.release.body}",
                    color = Color(0xFF9999BB), style = MaterialTheme.typography.bodyMedium,
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        UpdateManager.downloadAndInstall(context, updateResult!!.downloadUrl, updateResult!!.release.tagName)
                        showUpdate = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VioletPrimary),
                ) { Text("Update Now", fontWeight = FontWeight.Bold) }
            },
            dismissButton = { TextButton(onClick = { showUpdate = false }) { Text("Later", color = Color(0xFF7777AA)) } },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceDark)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState()),
    ) {

        // ── Top bar ──────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 12.dp, top = 16.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // App logo + greeting
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(11.dp))
                            .background(Brush.linearGradient(listOf(VioletPrimary, PinkAccent))),
                        contentAlignment = Alignment.Center,
                    ) { Icon(Icons.Rounded.Bolt, null, tint = Color.White, modifier = Modifier.size(22.dp)) }
                    Column {
                        Text("StormBeats", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("What do you want to play?", style = MaterialTheme.typography.labelSmall, color = Color(0xFF6B6B8A))
                    }
                }
            }
            IconButton(onClick = {}) {
                Icon(Icons.Rounded.Notifications, null, tint = Color(0xFF6B6B8A), modifier = Modifier.size(22.dp))
            }
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(VioletPrimary.copy(0.3f), PinkAccent.copy(0.2f)))),
                contentAlignment = Alignment.Center,
            ) { Icon(Icons.Rounded.Person, null, tint = VioletSoft, modifier = Modifier.size(20.dp)) }
            Spacer(Modifier.width(8.dp))
        }

        // ── Mood chips ───────────────────────────────────────────────────────
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 24.dp),
        ) {
            itemsIndexed(MOODS) { idx, mood ->
                val sel = selectedMood == idx
                Box(
                    modifier = Modifier
                        .height(36.dp)
                        .clip(CircleShape)
                        .background(if (sel) Brush.linearGradient(listOf(VioletPrimary, PinkAccent)) else Brush.linearGradient(listOf(SurfaceCard, SurfaceCard)))
                        .border(1.dp, if (sel) Color.Transparent else Color(0xFF2A2A40), CircleShape)
                        .clickable { selectedMood = if (sel) -1 else idx }
                        .padding(horizontal = 14.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        Icon(mood.icon, null, modifier = Modifier.size(13.dp), tint = if (sel) Color.White else mood.tint)
                        Text(mood.label, style = MaterialTheme.typography.labelMedium, fontSize = 12.sp, color = if (sel) Color.White else Color(0xFF9999BB))
                    }
                }
            }
        }

        // ── Now Playing card ─────────────────────────────────────────────────
        AnimatedVisibility(
            visible = currentSong != null,
            enter = fadeIn(tween(350)) + expandVertically(tween(350)),
            exit  = fadeOut(tween(200)) + shrinkVertically(tween(200)),
        ) {
            currentSong?.let { song ->
                NowPlayingCard(song = song, isPlaying = isPlaying, onTap = onShowPlayer)
                Spacer(Modifier.height(24.dp))
            }
        }

        if (currentSong == null) Spacer(Modifier.height(4.dp))

        // ── Trending section ─────────────────────────────────────────────────
        if (isLoading) {
            Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = VioletPrimary, modifier = Modifier.size(36.dp), strokeWidth = 3.dp)
            }
        } else {
            if (trending.isNotEmpty()) {
                SectionHeader("Trending Now 🔥", "See all") {}
                Spacer(Modifier.height(12.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    itemsIndexed(trending, key = { _, s -> "t${s.id}" }) { _, song ->
                        SongCard(song = song, isPlaying = currentSong?.id == song.id, onClick = {
                            PlayerController.playSong(song, trending)
                            vm.addToRecent(song)
                            onShowPlayer()
                        })
                    }
                }
                Spacer(Modifier.height(28.dp))
            }

            // ── Popular Artists (from API) ────────────────────────────────────
            if (artists.isNotEmpty()) {
                SectionHeader("Popular Artists", "See all") {}
                Spacer(Modifier.height(12.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    itemsIndexed(artists, key = { _, a -> "a${a.id}" }) { _, artist ->
                        ArtistCard(artist = artist)
                    }
                }
                Spacer(Modifier.height(28.dp))
            }

            // ── New Releases (album images from API) ─────────────────────────
            if (newReleases.isNotEmpty()) {
                SectionHeader("New Releases", "See all") {}
                Spacer(Modifier.height(12.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    itemsIndexed(newReleases, key = { _, s -> "n${s.id}" }) { _, song ->
                        AlbumCard(song = song, isPlaying = currentSong?.id == song.id, onClick = {
                            PlayerController.playSong(song, newReleases)
                            vm.addToRecent(song)
                            onShowPlayer()
                        })
                    }
                }
                Spacer(Modifier.height(28.dp))
            }

            // ── Recently Played (real songs) ─────────────────────────────────
            if (recentSongs.isNotEmpty()) {
                SectionHeader("Recently Played", "See all") {}
                Spacer(Modifier.height(12.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    itemsIndexed(recentSongs, key = { _, s -> "r${s.id}" }) { _, song ->
                        RecentCard(song = song, isPlaying = currentSong?.id == song.id, onClick = {
                            PlayerController.playSong(song, recentSongs)
                            vm.addToRecent(song)
                            onShowPlayer()
                        })
                    }
                }
                Spacer(Modifier.height(28.dp))
            }
        }

        Spacer(Modifier.height(110.dp))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Now Playing Card
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun NowPlayingCard(song: Song, isPlaying: Boolean, onTap: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "np")
    val dotAlpha by infiniteTransition.animateFloat(
        0.3f, 1f, infiniteRepeatable(tween(700), RepeatMode.Reverse), label = "dot",
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceCard)
            .border(1.dp, Brush.linearGradient(listOf(VioletPrimary.copy(0.55f), PinkAccent.copy(0.4f))), RoundedCornerShape(20.dp))
            .clickable(onClick = onTap),
    ) {
        // Subtle gradient top strip
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(Brush.horizontalGradient(listOf(Color.Transparent, VioletPrimary, PinkAccent, Color.Transparent)))
        )
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Album art
            Box(
                modifier = Modifier.size(64.dp).clip(RoundedCornerShape(14.dp)).background(SurfaceElevated),
                contentAlignment = Alignment.Center,
            ) {
                val url = song.getImageUrl()
                if (url.isNotEmpty()) {
                    AsyncImage(url, song.name, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                } else {
                    Icon(Icons.Rounded.MusicNote, null, tint = VioletSoft, modifier = Modifier.size(28.dp))
                }
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(Modifier.size(7.dp).clip(CircleShape).background(Brush.radialGradient(listOf(VioletPrimary.copy(dotAlpha), PinkAccent.copy(dotAlpha * 0.6f)))))
                    Text("NOW PLAYING", style = MaterialTheme.typography.labelSmall, color = VioletSoft, letterSpacing = 1.sp, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                }
                Spacer(Modifier.height(3.dp))
                Text(song.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(song.getPrimaryArtist(), style = MaterialTheme.typography.bodySmall, color = Color(0xFF8888AA), maxLines = 1)
            }
            Spacer(Modifier.width(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier.size(34.dp).clip(CircleShape).background(SurfaceElevated).clickable { PlayerController.playPrevious() },
                    contentAlignment = Alignment.Center,
                ) { Icon(Icons.Rounded.SkipPrevious, null, tint = Color(0xFFCCCCDD), modifier = Modifier.size(18.dp)) }
                Box(
                    modifier = Modifier.size(46.dp).clip(CircleShape).background(Brush.linearGradient(listOf(VioletPrimary, PinkAccent))).clickable { PlayerController.togglePlayPause() },
                    contentAlignment = Alignment.Center,
                ) { Icon(if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow, null, tint = Color.White, modifier = Modifier.size(24.dp)) }
                Box(
                    modifier = Modifier.size(34.dp).clip(CircleShape).background(SurfaceElevated).clickable { PlayerController.playNext() },
                    contentAlignment = Alignment.Center,
                ) { Icon(Icons.Rounded.SkipNext, null, tint = Color(0xFFCCCCDD), modifier = Modifier.size(18.dp)) }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Song card (trending) — portrait with play overlay
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun SongCard(song: Song, isPlaying: Boolean, onClick: () -> Unit) {
    val pulseAlpha by rememberInfiniteTransition(label = "sp").animateFloat(
        0.5f, 1f, infiniteRepeatable(tween(800), RepeatMode.Reverse), label = "sp",
    )

    Column(modifier = Modifier.width(140.dp).clickable(onClick = onClick)) {
        Box(modifier = Modifier.size(140.dp).clip(RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center) {
            val url = song.getImageUrl()
            if (url.isNotEmpty()) {
                AsyncImage(url, song.name, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            } else {
                Box(Modifier.fillMaxSize().background(Brush.linearGradient(listOf(SurfaceElevated, SurfaceCard))), contentAlignment = Alignment.Center) {
                    Icon(Icons.Rounded.MusicNote, null, tint = VioletSoft, modifier = Modifier.size(40.dp))
                }
            }
            // Dark scrim
            Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(0.6f)))))
            // Play button
            Box(
                modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp).size(32.dp).clip(CircleShape)
                    .background(if (isPlaying) Brush.linearGradient(listOf(VioletPrimary.copy(pulseAlpha), PinkAccent.copy(pulseAlpha))) else Brush.linearGradient(listOf(Color.Black.copy(0.7f), Color.Black.copy(0.7f)))),
                contentAlignment = Alignment.Center,
            ) {
                Icon(if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow, null, tint = Color.White, modifier = Modifier.size(18.dp))
            }
            if (isPlaying) {
                Box(Modifier.align(Alignment.TopStart).padding(6.dp).clip(RoundedCornerShape(6.dp)).background(VioletPrimary.copy(0.9f)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                    Text("PLAYING", style = MaterialTheme.typography.labelSmall, color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(song.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(song.getPrimaryArtist(), style = MaterialTheme.typography.bodySmall, color = Color(0xFF7777AA), maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Album card — square, real API image
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun AlbumCard(song: Song, isPlaying: Boolean, onClick: () -> Unit) {
    Column(modifier = Modifier.width(130.dp).clickable(onClick = onClick)) {
        Box(modifier = Modifier.size(130.dp).clip(RoundedCornerShape(14.dp)).background(SurfaceElevated)) {
            val url = song.getImageUrl()
            if (url.isNotEmpty()) {
                AsyncImage(url, song.name, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            } else {
                Box(Modifier.fillMaxSize().background(Brush.linearGradient(listOf(Color(0xFF1A0A3A), Color(0xFF2D1566)))), contentAlignment = Alignment.Center) {
                    Icon(Icons.Rounded.Album, null, tint = VioletSoft.copy(0.4f), modifier = Modifier.size(44.dp))
                }
            }
            if (isPlaying) {
                Box(Modifier.fillMaxSize().background(VioletPrimary.copy(0.25f)))
                Icon(Icons.Rounded.VolumeUp, null, tint = Color.White, modifier = Modifier.size(22.dp).align(Alignment.Center))
            }
        }
        Spacer(Modifier.height(7.dp))
        Text(song.album?.name ?: song.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(song.getPrimaryArtist(), style = MaterialTheme.typography.bodySmall, color = Color(0xFF7777AA), maxLines = 1)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Artist card — circle, real API image
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun ArtistCard(artist: ArtistResult) {
    Column(
        modifier = Modifier.width(80.dp).clickable {},
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier.size(76.dp).clip(CircleShape).background(SurfaceElevated)
                .border(2.dp, Brush.linearGradient(listOf(VioletPrimary.copy(0.5f), PinkAccent.copy(0.4f))), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            val url = artist.getImageUrl()
            if (url.isNotEmpty()) {
                AsyncImage(url, artist.name, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            } else {
                Icon(Icons.Rounded.Person, null, tint = Color(0xFF5A5A7A), modifier = Modifier.size(32.dp))
            }
        }
        Spacer(Modifier.height(7.dp))
        Text(
            artist.name,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 11.sp,
            color = Color(0xFFCCCCDD),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Recent card — landscape thumbnail
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun RecentCard(song: Song, isPlaying: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .width(240.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceCard)
            .border(1.dp, if (isPlaying) Brush.linearGradient(listOf(VioletPrimary.copy(0.5f), PinkAccent.copy(0.4f))) else Brush.linearGradient(listOf(Color(0xFF2A2A40), Color(0xFF2A2A40))), RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(10.dp)).background(SurfaceElevated)) {
            val url = song.getImageUrl()
            if (url.isNotEmpty()) AsyncImage(url, song.name, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            else Icon(Icons.Rounded.MusicNote, null, tint = VioletSoft, modifier = Modifier.size(24.dp).align(Alignment.Center))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(song.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = if (isPlaying) VioletSoft else Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(song.getPrimaryArtist(), style = MaterialTheme.typography.bodySmall, color = Color(0xFF7777AA), maxLines = 1)
        }
        Box(
            modifier = Modifier.size(32.dp).clip(CircleShape).background(if (isPlaying) Brush.linearGradient(listOf(VioletPrimary, PinkAccent)) else Brush.linearGradient(listOf(SurfaceElevated, SurfaceElevated))),
            contentAlignment = Alignment.Center,
        ) { Icon(if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow, null, tint = Color.White, modifier = Modifier.size(17.dp)) }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Section header
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun SectionHeader(title: String, action: String, onAction: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.weight(1f))
        TextButton(onClick = onAction) {
            Text(action, style = MaterialTheme.typography.labelMedium, color = VioletSoft)
        }
    }
}
