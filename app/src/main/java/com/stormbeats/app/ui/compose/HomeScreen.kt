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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.stormbeats.app.ui.theme.*
import com.stormbeats.app.util.PlayerController
import com.stormbeats.app.util.UpdateManager
import kotlinx.coroutines.launch

private data class MoodData(val label: String, val icon: ImageVector, val colors: List<Color>)

private val MOODS = listOf(
    MoodData("Workout",  Icons.Rounded.DirectionsRun, listOf(Color(0xFFFF6B6B), Color(0xFFEE5A24))),
    MoodData("Energize", Icons.Rounded.Bolt,          listOf(Color(0xFFFECA57), Color(0xFFFF9F43))),
    MoodData("Relax",    Icons.Rounded.Spa,           listOf(Color(0xFF48CAE4), Color(0xFF0096C7))),
    MoodData("Vibes",    Icons.Rounded.MusicNote,     listOf(GradientStart, GradientEnd)),
    MoodData("Chill",    Icons.Rounded.NightsStay,    listOf(Color(0xFF6C5CE7), Color(0xFFA29BFE))),
    MoodData("Focus",    Icons.Rounded.Headphones,    listOf(Color(0xFF00B894), Color(0xFF00CEC9))),
)

private data class AlbumCardData(val bg: List<Color>, val accent: List<Color>, val title: String, val subtitle: String)

private val ALBUM_CARDS = listOf(
    AlbumCardData(listOf(Color(0xFF1A0A3A), Color(0xFF3D1A7A)), listOf(VioletPrimary, VioletSoft),        "Deep Focus",  "Instrumental"),
    AlbumCardData(listOf(Color(0xFF0A1A2A), Color(0xFF0D3060)), listOf(CyanAccent, Color(0xFF0EA5E9)),    "Late Night",  "R&B Vibes"),
    AlbumCardData(listOf(Color(0xFF2A0A1A), Color(0xFF60103A)), listOf(PinkAccent, PinkSoft),             "Bharat Hits", "Bollywood"),
    AlbumCardData(listOf(Color(0xFF0A0A2A), Color(0xFF1A1060)), listOf(Color(0xFF6366F1), Color(0xFF818CF8)), "Old School", "90s Classics"),
    AlbumCardData(listOf(Color(0xFF1A0A0A), Color(0xFF4A1508)), listOf(Color(0xFFFF6B6B), Color(0xFFFF8E53)), "Party Mix",  "Dance & EDM"),
)

@Composable
fun HomeScreen(onShowPlayer: () -> Unit) {
    val currentSong  by PlayerController.currentSong.collectAsState()
    val isPlaying    by PlayerController.isPlaying.collectAsState()
    val context      = LocalContext.current
    val scope        = rememberCoroutineScope()
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
            text  = { Text("Version ${updateResult!!.release.tagName} is ready.\n\n${updateResult!!.release.body}", color = Color(0xFF8888BB), style = MaterialTheme.typography.bodyMedium) },
            confirmButton = {
                Button(
                    onClick = { UpdateManager.downloadAndInstall(context, updateResult!!.downloadUrl, updateResult!!.release.tagName); showUpdate = false },
                    colors  = ButtonDefaults.buttonColors(containerColor = VioletPrimary),
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
        // ── Top bar ────────────────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Home", style = MaterialTheme.typography.headlineMedium, color = Color.White, modifier = Modifier.weight(1f))
            IconButton(onClick = {}) { Icon(Icons.Rounded.History, null, tint = Color.White) }
            IconButton(onClick = {}) { Icon(Icons.Rounded.TrendingUp, null, tint = Color.White) }
            IconButton(onClick = {}) { Icon(Icons.Rounded.AccountCircle, null, tint = Color.White) }
        }

        // ── Mood chips ─────────────────────────────────────────────────────────
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(bottom = 20.dp),
        ) {
            itemsIndexed(MOODS) { idx, mood ->
                val selected = selectedMood == idx
                val chipBg = if (selected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.surfaceVariant

                Box(
                    modifier = Modifier
                        .height(40.dp)
                        .clip(CircleShape)
                        .background(chipBg)
                        .border(
                            width = 1.dp,
                            color = if (selected) Color.Transparent else Color(0xFF2E2E4A),
                            shape = CircleShape,
                        )
                        .clickable { selectedMood = if (selected) -1 else idx }
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Icon(mood.icon, null, modifier = Modifier.size(16.dp), tint = if (selected) Color.White else Color(0xFF7777AA))
                        Text(
                            mood.label,
                            style = MaterialTheme.typography.labelLarge,
                            color = if (selected) Color.White else Color(0xFF9999BB),
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                        )
                    }
                }
            }
        }

        // ── Now playing hero card (when song is playing) ───────────────────────
        AnimatedVisibility(
            visible = currentSong != null,
            enter = fadeIn(tween(400)) + expandVertically(tween(400)),
            exit  = fadeOut(tween(250)) + shrinkVertically(tween(250)),
        ) {
            currentSong?.let { song ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(SurfaceCard)
                        .clickable(onClick = onShowPlayer),
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(60.dp).clip(RoundedCornerShape(12.dp)).background(SurfaceElevated),
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
                            val infiniteTransition = rememberInfiniteTransition(label = "dot")
                            val dotPulse by infiniteTransition.animateFloat(
                                0.4f, 1f, infiniteRepeatable(tween(800), RepeatMode.Reverse), label = "dot"
                            )
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Box(
                                    Modifier.size(7.dp).clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = dotPulse))
                                )
                                Text("Now Playing", style = MaterialTheme.typography.labelSmall, color = VioletSoft, fontWeight = FontWeight.SemiBold)
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(song.name, style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(song.getPrimaryArtist(), style = MaterialTheme.typography.bodySmall, color = Color(0xFF7777AA), maxLines = 1)
                        }
                        Box(
                            modifier = Modifier.size(44.dp).clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                                .clickable { PlayerController.togglePlayPause() },
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow, null, tint = Color.White, modifier = Modifier.size(24.dp))
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // ── Quick picks ────────────────────────────────────────────────────────
        SectionHeader("Quick picks", action = "Play all")

        Spacer(Modifier.height(8.dp))

        data class QuickItem(val icon: ImageVector, val bg: List<Color>, val title: String, val subtitle: String)
        listOf(
            QuickItem(Icons.Rounded.Search,     listOf(Color(0xFF1A1A3A), SurfaceElevated), "Discover music",    "Search JioSaavn"),
            QuickItem(Icons.Rounded.AudioFile,  listOf(Color(0xFF0D2010), Color(0xFF0A3018)), "320kbps HiFi Audio", "Lossless quality"),
            QuickItem(Icons.Rounded.Bolt,       listOf(Color(0xFF2A1A00), Color(0xFF4A2E00)), "StormBeats",       "Your music player"),
            QuickItem(Icons.Rounded.QueueMusic, listOf(Color(0xFF1A0A2A), Color(0xFF2D1566)), "Queue songs",      "Tap a song to play"),
        ).forEach { item ->
            QuickPickRow(icon = item.icon, gradient = item.bg, title = item.title, subtitle = item.subtitle)
        }

        Spacer(Modifier.height(24.dp))

        // ── Keep listening / Album cards ───────────────────────────────────────
        SectionHeader("Featured", action = null)

        Spacer(Modifier.height(12.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.padding(bottom = 8.dp),
        ) {
            itemsIndexed(ALBUM_CARDS) { _, card ->
                AlbumCard(bgGradient = card.bg, accentGradient = card.accent, title = card.title, subtitle = card.subtitle)
            }
        }

        Spacer(Modifier.height(24.dp))

        // ── Artists row ────────────────────────────────────────────────────────
        SectionHeader("Popular Artists", action = null)

        Spacer(Modifier.height(12.dp))

        val artists = listOf(
            "Arijit Singh"    to listOf(Color(0xFF6C5CE7), Color(0xFFA29BFE)),
            "Pritam"          to listOf(Color(0xFFFF7675), Color(0xFFD63031)),
            "AR Rahman"       to listOf(Color(0xFF00B894), Color(0xFF00CEC9)),
            "Neha Kakkar"     to listOf(Color(0xFFEC4899), Color(0xFFF472B6)),
            "Atif Aslam"      to listOf(Color(0xFF0EA5E9), Color(0xFF38BDF8)),
            "Shreya Ghoshal"  to listOf(Color(0xFFF59E0B), Color(0xFFFBBF24)),
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.padding(bottom = 8.dp),
        ) {
            itemsIndexed(artists) { _, (name, colors) ->
                ArtistChip(name = name, gradient = colors)
            }
        }

        Spacer(Modifier.height(100.dp))
    }
}

@Composable
private fun SectionHeader(title: String, action: String?) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(title, style = MaterialTheme.typography.titleLarge, color = VioletSoft, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        if (action != null) {
            TextButton(onClick = {}) {
                Text(action, color = Color(0xFF9999BB), style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
private fun QuickPickRow(icon: ImageVector, gradient: List<Color>, title: String, subtitle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {}
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier.size(56.dp).clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) { Icon(icon, null, tint = Color.White.copy(0.85f), modifier = Modifier.size(26.dp)) }
        Column(modifier = Modifier.weight(1f)) {
            Text(title,    style = MaterialTheme.typography.titleSmall, color = Color.White, fontWeight = FontWeight.SemiBold, maxLines = 1)
            Text(subtitle, style = MaterialTheme.typography.bodySmall,  color = Color(0xFF5555AA), maxLines = 1)
        }
        Icon(Icons.Rounded.MoreVert, null, tint = Color(0xFF3E3E5E), modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun AlbumCard(bgGradient: List<Color>, accentGradient: List<Color>, title: String, subtitle: String) {
    Box(
        modifier = Modifier
            .size(150.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable {},
    ) {
        Box(
            modifier = Modifier.size(70.dp).align(Alignment.TopEnd)
                .clip(CircleShape)
        )
        Column(
            modifier = Modifier.align(Alignment.BottomStart).padding(12.dp),
        ) {
            Text(title,    style = MaterialTheme.typography.titleSmall, color = Color.White, fontWeight = FontWeight.Bold, maxLines = 1)
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = Color(0xFF8888AA), maxLines = 1)
        }
        Box(
            modifier = Modifier.size(34.dp).align(Alignment.BottomEnd).padding(end = 10.dp, bottom = 10.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center,
        ) { Icon(Icons.Rounded.PlayArrow, null, tint = Color.White, modifier = Modifier.size(18.dp)) }
    }
}

@Composable
private fun ArtistChip(name: String, gradient: List<Color>) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp).clickable {},
    ) {
        Box(
            modifier = Modifier.size(72.dp).clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(2.dp, MaterialTheme.colorScheme.outline, CircleShape),
            contentAlignment = Alignment.Center,
        ) { Icon(Icons.Rounded.Person, null, tint = Color.White.copy(0.7f), modifier = Modifier.size(32.dp)) }
        Spacer(Modifier.height(6.dp))
        Text(name, style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, color = Color(0xFF9999BB), maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}
