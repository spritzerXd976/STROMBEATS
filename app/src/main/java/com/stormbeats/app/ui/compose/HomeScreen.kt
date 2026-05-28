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

// ── Mood chips ────────────────────────────────────────────────────────────────
private data class Mood(val label: String, val icon: ImageVector)
private val MOODS = listOf(
    Mood("Workout",  Icons.Rounded.DirectionsRun),
    Mood("Energize", Icons.Rounded.Bolt),
    Mood("Relax",    Icons.Rounded.Spa),
    Mood("Vibes",    Icons.Rounded.MusicNote),
    Mood("Chill",    Icons.Rounded.NightsStay),
    Mood("Focus",    Icons.Rounded.Headphones),
)

// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun HomeScreen(
    onShowPlayer: () -> Unit,
    vm: HomeViewModel = viewModel(),
) {
    val trending    by vm.trending.collectAsState()
    val newReleases by vm.newReleases.collectAsState()
    val topCharts   by vm.topCharts.collectAsState()
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

    // Update dialog
    if (showUpdate && updateResult != null) {
        AlertDialog(
            onDismissRequest = { showUpdate = false },
            containerColor   = Surface1,
            icon    = { Icon(Icons.Rounded.SystemUpdate, null, tint = PurpleLight) },
            title   = { Text("Update Available", color = OnBg, fontWeight = FontWeight.Bold) },
            text    = { Text("v${updateResult!!.release.tagName} is ready\n\n${updateResult!!.release.body}", color = OnBgSec, style = MaterialTheme.typography.bodyMedium) },
            confirmButton = {
                Button(onClick = {
                    UpdateManager.downloadAndInstall(context, updateResult!!.downloadUrl, updateResult!!.release.tagName)
                    showUpdate = false
                }, colors = ButtonDefaults.buttonColors(containerColor = Purple)) { Text("Update Now") }
            },
            dismissButton = { TextButton(onClick = { showUpdate = false }) { Text("Later", color = OnBgSec) } },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState()),
    ) {
        // ── Top Bar ───────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 16.dp, top = 18.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Logo
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Brush.linearGradient(listOf(Purple, Pink))),
                contentAlignment = Alignment.Center,
            ) { Icon(Icons.Rounded.Bolt, null, tint = Color.White, modifier = Modifier.size(24.dp)) }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("StormBeats", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = OnBg)
                Text("What's your vibe today?", style = MaterialTheme.typography.labelSmall, color = OnBgTer)
            }
            IconButton(onClick = {}) {
                Icon(Icons.Rounded.Notifications, null, tint = OnBgSec, modifier = Modifier.size(22.dp))
            }
            Box(
                modifier = Modifier.size(36.dp).clip(CircleShape)
                    .background(Brush.linearGradient(listOf(Purple.copy(.3f), Pink.copy(.2f)))),
                contentAlignment = Alignment.Center,
            ) { Icon(Icons.Rounded.Person, null, tint = PurpleLight, modifier = Modifier.size(18.dp)) }
        }

        Spacer(Modifier.height(14.dp))

        // ── Mood chips ────────────────────────────────────────────────────────
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            itemsIndexed(MOODS) { i, mood ->
                val sel = selectedMood == i
                Box(
                    modifier = Modifier
                        .height(34.dp)
                        .clip(CircleShape)
                        .background(if (sel) Brush.linearGradient(listOf(Purple, Pink)) else Brush.linearGradient(listOf(Surface1, Surface1)))
                        .border(1.dp, if (sel) Color.Transparent else Surface3, CircleShape)
                        .clickable { selectedMood = if (sel) -1 else i }
                        .padding(horizontal = 14.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        Icon(mood.icon, null, modifier = Modifier.size(12.dp), tint = if (sel) Color.White else OnBgSec)
                        Text(mood.label, style = MaterialTheme.typography.labelMedium, fontSize = 12.sp, color = if (sel) Color.White else OnBgSec)
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // ── Now Playing card ──────────────────────────────────────────────────
        AnimatedVisibility(
            visible = currentSong != null,
            enter = fadeIn(tween(300)) + expandVertically(tween(300)),
            exit  = fadeOut(tween(200)) + shrinkVertically(tween(200)),
        ) {
            currentSong?.let { song ->
                NowPlayingCard(song, isPlaying, onShowPlayer)
                Spacer(Modifier.height(24.dp))
            }
        }

        // ── Loading shimmer ───────────────────────────────────────────────────
        if (isLoading) {
            Box(Modifier.fillMaxWidth().height(220.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    CircularProgressIndicator(color = Purple, modifier = Modifier.size(36.dp), strokeWidth = 3.dp)
                    Text("Loading music…", style = MaterialTheme.typography.bodySmall, color = OnBgTer)
                }
            }
        } else {

            // ── Trending Now ──────────────────────────────────────────────────
            if (trending.isNotEmpty()) {
                SectionHeader("Trending Now 🔥", "See all")
                Spacer(Modifier.height(12.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    itemsIndexed(trending, key = { _, s -> "tr${s.id}" }) { _, song ->
                        SongCard(
                            song = song,
                            isCurrentPlaying = currentSong?.id == song.id && isPlaying,
                            onClick = { PlayerController.playSong(song, trending); vm.addToRecent(song); onShowPlayer() },
                        )
                    }
                }
                Spacer(Modifier.height(28.dp))
            }

            // ── Popular Artists (real API images) ─────────────────────────────
            if (artists.isNotEmpty()) {
                SectionHeader("Popular Artists", "See all")
                Spacer(Modifier.height(12.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    itemsIndexed(artists, key = { _, a -> "ar${a.id}" }) { _, artist ->
                        ArtistCard(artist)
                    }
                }
                Spacer(Modifier.height(28.dp))
            }

            // ── New Releases (real album art) ─────────────────────────────────
            if (newReleases.isNotEmpty()) {
                SectionHeader("New Releases", "See all")
                Spacer(Modifier.height(12.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    itemsIndexed(newReleases, key = { _, s -> "nr${s.id}" }) { _, song ->
                        AlbumCard(
                            song = song,
                            isPlaying = currentSong?.id == song.id,
                            onClick = { PlayerController.playSong(song, newReleases); vm.addToRecent(song); onShowPlayer() },
                        )
                    }
                }
                Spacer(Modifier.height(28.dp))
            }

            // ── Top Charts ────────────────────────────────────────────────────
            if (topCharts.isNotEmpty()) {
                SectionHeader("Top Charts 🎵", "See all")
                Spacer(Modifier.height(12.dp))
                topCharts.take(5).forEachIndexed { index, song ->
                    ChartRow(
                        rank   = index + 1,
                        song   = song,
                        isPlaying = currentSong?.id == song.id,
                        onClick = { PlayerController.playSong(song, topCharts); vm.addToRecent(song); onShowPlayer() },
                    )
                }
                Spacer(Modifier.height(28.dp))
            }

            // ── Recently Played ───────────────────────────────────────────────
            if (recentSongs.isNotEmpty()) {
                SectionHeader("Recently Played", "See all")
                Spacer(Modifier.height(12.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    itemsIndexed(recentSongs, key = { _, s -> "rc${s.id}" }) { _, song ->
                        RecentCard(
                            song = song,
                            isPlaying = currentSong?.id == song.id,
                            onClick = { PlayerController.playSong(song, recentSongs); vm.addToRecent(song); onShowPlayer() },
                        )
                    }
                }
                Spacer(Modifier.height(28.dp))
            }
        }

        Spacer(Modifier.height(120.dp))
    }
}

// ── Section header ────────────────────────────────────────────────────────────
@Composable
private fun SectionHeader(title: String, action: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = OnBg, modifier = Modifier.weight(1f))
        Text(action, style = MaterialTheme.typography.labelMedium, color = PurpleLight, modifier = Modifier.clickable {})
    }
}

// ── Now Playing card ──────────────────────────────────────────────────────────
@Composable
private fun NowPlayingCard(song: Song, isPlaying: Boolean, onTap: () -> Unit) {
    val inf = rememberInfiniteTransition(label = "np")
    val dot by inf.animateFloat(0.3f, 1f, infiniteRepeatable(tween(700), RepeatMode.Reverse), label = "d")

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onTap),
        shape    = RoundedCornerShape(18.dp),
        color    = Surface0,
        border   = BorderStroke(1.dp, Brush.linearGradient(listOf(Purple.copy(.5f), Pink.copy(.3f)))),
        shadowElevation = 8.dp,
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            // Album art
            Box(
                modifier = Modifier.size(60.dp).clip(RoundedCornerShape(12.dp)).background(Surface2),
                contentAlignment = Alignment.Center,
            ) {
                val url = song.getImageUrl()
                if (url.isNotEmpty()) AsyncImage(url, song.name, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                else Icon(Icons.Rounded.MusicNote, null, tint = PurpleLight, modifier = Modifier.size(26.dp))
            }
            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Pulsing "Now Playing" label
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    Box(Modifier.size(6.dp).clip(CircleShape).background(Brush.radialGradient(listOf(Purple.copy(dot), Pink.copy(dot * .7f)))))
                    Text("NOW PLAYING", style = MaterialTheme.typography.labelSmall, color = PurpleLight, letterSpacing = 1.sp, fontSize = 9.sp)
                }
                Spacer(Modifier.height(2.dp))
                Text(song.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = OnBg, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(song.getPrimaryArtist(), style = MaterialTheme.typography.bodySmall, color = OnBgSec, maxLines = 1)
            }

            Spacer(Modifier.width(8.dp))

            // Controls
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                SmallCtrlBtn(Icons.Rounded.SkipPrevious, Surface2) { PlayerController.playPrevious() }
                Box(
                    modifier = Modifier.size(44.dp).clip(CircleShape)
                        .background(Brush.linearGradient(listOf(Purple, Pink)))
                        .clickable { PlayerController.togglePlayPause() },
                    contentAlignment = Alignment.Center,
                ) { Icon(if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow, null, tint = Color.White, modifier = Modifier.size(24.dp)) }
                SmallCtrlBtn(Icons.Rounded.SkipNext, Surface2) { PlayerController.playNext() }
            }
        }
    }
}

@Composable
private fun SmallCtrlBtn(icon: ImageVector, bg: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier.size(32.dp).clip(CircleShape).background(bg).clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) { Icon(icon, null, tint = OnBgSec, modifier = Modifier.size(17.dp)) }
}

// ── Song card (trending) — portrait ──────────────────────────────────────────
@Composable
private fun SongCard(song: Song, isCurrentPlaying: Boolean, onClick: () -> Unit) {
    val pulse by rememberInfiniteTransition(label = "p").animateFloat(
        .6f, 1f, infiniteRepeatable(tween(900), RepeatMode.Reverse), label = "p"
    )
    Column(modifier = Modifier.width(138.dp).clickable(onClick = onClick)) {
        Box(modifier = Modifier.size(138.dp).clip(RoundedCornerShape(16.dp)).background(Surface1)) {
            val url = song.getImageUrl()
            if (url.isNotEmpty()) AsyncImage(url, song.name, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            else Box(Modifier.fillMaxSize().background(Surface2), contentAlignment = Alignment.Center) {
                Icon(Icons.Rounded.MusicNote, null, tint = PurpleLight.copy(.5f), modifier = Modifier.size(42.dp))
            }
            // Bottom scrim
            Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(.55f)))))
            // Play badge
            Box(
                modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp).size(30.dp).clip(CircleShape)
                    .background(if (isCurrentPlaying) Brush.linearGradient(listOf(Purple.copy(pulse), Pink.copy(pulse))) else Brush.linearGradient(listOf(Color.Black.copy(.65f), Color.Black.copy(.65f)))),
                contentAlignment = Alignment.Center,
            ) { Icon(if (isCurrentPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow, null, tint = Color.White, modifier = Modifier.size(16.dp)) }
            // Playing chip
            if (isCurrentPlaying) {
                Box(
                    Modifier.align(Alignment.TopStart).padding(6.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Purple.copy(.9f))
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                ) { Text("♪ PLAYING", style = MaterialTheme.typography.labelSmall, color = Color.White, fontSize = 8.sp, letterSpacing = .5.sp) }
            }
        }
        Spacer(Modifier.height(7.dp))
        Text(song.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = OnBg, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(song.getPrimaryArtist(), style = MaterialTheme.typography.bodySmall, color = OnBgSec, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

// ── Album card (new releases) — square ───────────────────────────────────────
@Composable
private fun AlbumCard(song: Song, isPlaying: Boolean, onClick: () -> Unit) {
    Column(modifier = Modifier.width(128.dp).clickable(onClick = onClick)) {
        Box(modifier = Modifier.size(128.dp).clip(RoundedCornerShape(14.dp)).background(Surface1)) {
            val url = song.getImageUrl()
            if (url.isNotEmpty()) AsyncImage(url, song.name, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            else Box(Modifier.fillMaxSize().background(Brush.linearGradient(listOf(Surface1, Surface2))), contentAlignment = Alignment.Center) {
                Icon(Icons.Rounded.Album, null, tint = PurpleLight.copy(.3f), modifier = Modifier.size(44.dp))
            }
            if (isPlaying) {
                Box(Modifier.fillMaxSize().background(Purple.copy(.22f)))
                Icon(Icons.Rounded.VolumeUp, null, tint = Color.White, modifier = Modifier.size(20.dp).align(Alignment.Center))
            }
        }
        Spacer(Modifier.height(7.dp))
        Text(song.album?.name ?: song.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = OnBg, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(song.getPrimaryArtist(), style = MaterialTheme.typography.bodySmall, color = OnBgSec, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

// ── Artist card — circle with real API image ──────────────────────────────────
@Composable
private fun ArtistCard(artist: ArtistResult) {
    Column(modifier = Modifier.width(78.dp).clickable {}, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier.size(74.dp).clip(CircleShape).background(Surface1)
                .border(2.dp, Brush.linearGradient(listOf(Purple.copy(.55f), Pink.copy(.4f))), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            val url = artist.getImageUrl()
            if (url.isNotEmpty()) AsyncImage(url, artist.name, Modifier.fillMaxSize().clip(CircleShape), contentScale = ContentScale.Crop)
            else Icon(Icons.Rounded.Person, null, tint = OnBgTer, modifier = Modifier.size(30.dp))
        }
        Spacer(Modifier.height(6.dp))
        Text(artist.name, style = MaterialTheme.typography.labelSmall, fontSize = 11.sp, color = OnBgSec, maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
    }
}

// ── Chart row ─────────────────────────────────────────────────────────────────
@Composable
private fun ChartRow(rank: Int, song: Song, isPlaying: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(if (isPlaying) Purple.copy(.06f) else Color.Transparent)
            .padding(horizontal = 20.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Rank number
        Box(modifier = Modifier.width(28.dp), contentAlignment = Alignment.Center) {
            Text(
                "$rank",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = if (rank <= 3) Purple else OnBgTer,
                fontSize = if (rank <= 3) 16.sp else 13.sp,
            )
        }
        // Art
        Box(modifier = Modifier.size(50.dp).clip(RoundedCornerShape(10.dp)).background(Surface1)) {
            val url = song.getImageUrl()
            if (url.isNotEmpty()) AsyncImage(url, song.name, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            else Icon(Icons.Rounded.MusicNote, null, tint = PurpleLight.copy(.5f), modifier = Modifier.size(22.dp).align(Alignment.Center))
            if (isPlaying) {
                Box(Modifier.fillMaxSize().background(Purple.copy(.22f)))
                Icon(Icons.Rounded.VolumeUp, null, tint = Color.White, modifier = Modifier.size(16.dp).align(Alignment.Center))
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(song.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = if (isPlaying) PurpleLight else OnBg, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(song.getPrimaryArtist(), style = MaterialTheme.typography.bodySmall, color = OnBgSec, maxLines = 1)
        }
        song.duration?.let { d ->
            Text("%d:%02d".format(d / 60, d % 60), style = MaterialTheme.typography.labelSmall, color = OnBgTer)
        }
        Icon(Icons.Rounded.MoreVert, null, tint = OnBgTer, modifier = Modifier.size(16.dp))
    }
}

// ── Recent card — horizontal pill ────────────────────────────────────────────
@Composable
private fun RecentCard(song: Song, isPlaying: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.width(210.dp).clip(RoundedCornerShape(14.dp)).clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = Surface0,
        border = if (isPlaying) BorderStroke(1.dp, Brush.linearGradient(listOf(Purple.copy(.5f), Pink.copy(.4f))))
                 else BorderStroke(1.dp, Surface3),
    ) {
        Row(
            modifier = Modifier.padding(9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(modifier = Modifier.size(46.dp).clip(RoundedCornerShape(10.dp)).background(Surface2)) {
                val url = song.getImageUrl()
                if (url.isNotEmpty()) AsyncImage(url, song.name, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                else Icon(Icons.Rounded.MusicNote, null, tint = PurpleLight.copy(.5f), modifier = Modifier.size(22.dp).align(Alignment.Center))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(song.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = if (isPlaying) PurpleLight else OnBg, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(song.getPrimaryArtist(), style = MaterialTheme.typography.bodySmall, color = OnBgSec, maxLines = 1)
            }
            Box(
                modifier = Modifier.size(30.dp).clip(CircleShape)
                    .background(if (isPlaying) Brush.linearGradient(listOf(Purple, Pink)) else Brush.linearGradient(listOf(Surface2, Surface2))),
                contentAlignment = Alignment.Center,
            ) { Icon(if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow, null, tint = Color.White, modifier = Modifier.size(15.dp)) }
        }
    }
}
