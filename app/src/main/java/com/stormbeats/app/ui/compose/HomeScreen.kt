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
import androidx.compose.ui.draw.blur
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
import coil.compose.AsyncImage
import com.stormbeats.app.ui.theme.*
import com.stormbeats.app.util.PlayerController
import com.stormbeats.app.util.UpdateManager

// ─────────────────────────────────────────────────────────────────────────────
// Data
// ─────────────────────────────────────────────────────────────────────────────

private data class MoodData(val label: String, val icon: ImageVector, val colors: List<Color>)

private val MOODS = listOf(
    MoodData("Workout",  Icons.Rounded.FitnessCenter,   listOf(Color(0xFFFF6B6B), Color(0xFFEE5A24))),
    MoodData("Energize", Icons.Rounded.Bolt,            listOf(Color(0xFFFECA57), Color(0xFFFF9F43))),
    MoodData("Relax",    Icons.Rounded.SelfImprovement, listOf(Color(0xFF48CAE4), Color(0xFF0096C7))),
    MoodData("Vibes",    Icons.Rounded.MusicNote,       listOf(GradientStart, GradientEnd)),
    MoodData("Chill",    Icons.Rounded.NightsStay,      listOf(Color(0xFF6C5CE7), Color(0xFFA29BFE))),
    MoodData("Focus",    Icons.Rounded.Headphones,      listOf(Color(0xFF00B894), Color(0xFF00CEC9))),
)

private val ALBUM_CARDS = listOf(
    Triple(listOf(Color(0xFF1A0A3A), Color(0xFF3D1A7A)), listOf(VioletPrimary, VioletSoft),         "Deep Focus",   "Instrumental"),
    Triple(listOf(Color(0xFF0A1A2A), Color(0xFF0D3060)), listOf(CyanAccent, Color(0xFF0EA5E9)),    "Late Night",   "R&B Vibes"),
    Triple(listOf(Color(0xFF2A0A1A), Color(0xFF60103A)), listOf(PinkAccent, PinkSoft),             "Bharat Hits",  "Bollywood"),
    Triple(listOf(Color(0xFF0A0A2A), Color(0xFF1A1060)), listOf(Color(0xFF6366F1), Color(0xFF818CF8)), "Old School","90s Classics"),
    Triple(listOf(Color(0xFF1A0A0A), Color(0xFF4A1508)), listOf(Color(0xFFFF6B6B), Color(0xFFFF8E53)), "Party Mix", "Dance & EDM"),
)

// ─────────────────────────────────────────────────────────────────────────────
// HomeScreen
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun HomeScreen(onShowPlayer: () -> Unit) {
    val currentSong  by PlayerController.currentSong.collectAsState()
    val isPlaying    by PlayerController.isPlaying.collectAsState()
    val context      = LocalContext.current
    var showUpdate   by remember { mutableStateOf(false) }
    var updateResult by remember { mutableStateOf<UpdateManager.UpdateResult.UpdateAvailable?>(null) }
    var selectedMood by remember { mutableIntStateOf(-1) }

    LaunchedEffect(Unit) {
        val r = UpdateManager.checkForUpdate(context)
        if (r is UpdateManager.UpdateResult.UpdateAvailable) { updateResult = r; showUpdate = true }
    }

    // Update dialog
    if (showUpdate && updateResult != null) {
        AlertDialog(
            onDismissRequest = { showUpdate = false },
            icon  = { Icon(Icons.Rounded.SystemUpdate, null, tint = VioletPrimary) },
            title = { Text("Update Available", fontWeight = FontWeight.Bold) },
            text  = { Text("v${updateResult!!.release.tagName} is ready.\n\n${updateResult!!.release.body}", style = MaterialTheme.typography.bodyMedium) },
            confirmButton = {
                Button(
                    onClick = { UpdateManager.downloadAndInstall(context, updateResult!!.downloadUrl, updateResult!!.release.tagName); showUpdate = false },
                    colors = ButtonDefaults.buttonColors(containerColor = VioletPrimary),
                ) { Text("Update Now") }
            },
            dismissButton = { TextButton(onClick = { showUpdate = false }) { Text("Later") } },
            containerColor = SurfaceCard,
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceDark)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState()),
    ) {

        // ── Ambient header with orbs ───────────────────────────────
        Box(modifier = Modifier.fillMaxWidth().height(230.dp)) {
            // Violet orb
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .offset(x = (-50).dp, y = (-70).dp)
                    .blur(90.dp)
                    .background(VioletPrimary.copy(alpha = 0.28f), CircleShape)
            )
            // Pink orb
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 50.dp, y = (-50).dp)
                    .blur(90.dp)
                    .background(PinkAccent.copy(alpha = 0.22f), CircleShape)
            )

            Column(modifier = Modifier.fillMaxWidth()) {
                // ── Top Bar ────────────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Logo
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(15.dp))
                            .background(Brush.linearGradient(listOf(VioletPrimary, PinkAccent))),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Rounded.Bolt, null, tint = Color.White, modifier = Modifier.size(28.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("StormBeats", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = Color.White)
                        Text("Good evening ✨", style = MaterialTheme.typography.labelSmall, color = Color(0xFF7777AA))
                    }
                    // Notification icon
                    Box(
                        modifier = Modifier.size(40.dp).clip(CircleShape).background(SurfaceElevated.copy(alpha = 0.7f)).clickable {},
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Rounded.Notifications, null, tint = Color(0xFF9999BB), modifier = Modifier.size(20.dp))
                    }
                    Spacer(Modifier.width(8.dp))
                    // Cast icon
                    Box(
                        modifier = Modifier.size(40.dp).clip(CircleShape).background(SurfaceElevated.copy(alpha = 0.7f)).clickable {},
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Rounded.Person, null, tint = Color(0xFF9999BB), modifier = Modifier.size(20.dp))
                    }
                }

                // ── Mood Filter Chips ──────────────────────────────
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    itemsIndexed(MOODS) { idx, mood ->
                        val selected = selectedMood == idx
                        val chipBg = if (selected)
                            Brush.linearGradient(mood.colors)
                        else
                            Brush.linearGradient(listOf(SurfaceElevated, SurfaceElevated))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50.dp))
                                .background(chipBg)
                                .border(
                                    width = if (selected) 0.dp else 1.dp,
                                    brush = Brush.linearGradient(
                                        if (selected) mood.colors
                                        else listOf(Color(0xFF2E2E4A), Color(0xFF2E2E4A))
                                    ),
                                    shape = RoundedCornerShape(50.dp),
                                )
                                .clickable { selectedMood = if (selected) -1 else idx }
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp),
                            ) {
                                Icon(mood.icon, null, modifier = Modifier.size(13.dp), tint = if (selected) Color.White else mood.colors[0])
                                Text(
                                    mood.label,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (selected) Color.White else Color(0xFFAAAACC),
                                    fontSize = 12.sp,
                                )
                            }
                        }
                    }
                }
            }
        }

        // ── Now Playing Hero ───────────────────────────────────────
        AnimatedContent(
            targetState = currentSong,
            transitionSpec = { fadeIn(tween(400)) togetherWith fadeOut(tween(200)) },
            label = "hero",
        ) { song ->
            if (song != null) {
                val imageUrl = song.getImageUrl()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .clickable(onClick = onShowPlayer),
                ) {
                    // Blurred art background
                    if (imageUrl.isNotEmpty()) {
                        AsyncImage(
                            model = imageUrl, contentDescription = null,
                            modifier = Modifier.fillMaxWidth().height(190.dp).blur(40.dp),
                            contentScale = ContentScale.Crop,
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(190.dp)
                                .background(Brush.linearGradient(listOf(Color(0xFF1A0A3A), Color(0xFF2D1566))))
                        )
                    }
                    // Dark scrim
                    Box(
                        modifier = Modifier.fillMaxWidth().height(190.dp)
                            .background(Brush.verticalGradient(listOf(Color(0xBB0D0D14), Color(0xEE0D0D14))))
                    )
                    // Gradient top accent bar
                    Box(
                        modifier = Modifier.fillMaxWidth().height(2.dp)
                            .background(Brush.horizontalGradient(listOf(Color.Transparent, VioletPrimary, PinkAccent, Color.Transparent)))
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // Album art square
                        Box(
                            modifier = Modifier
                                .size(82.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .border(
                                    1.5.dp,
                                    Brush.linearGradient(listOf(VioletPrimary.copy(0.6f), PinkAccent.copy(0.6f))),
                                    RoundedCornerShape(16.dp),
                                ),
                        ) {
                            if (imageUrl.isNotEmpty()) {
                                AsyncImage(
                                    model = imageUrl, contentDescription = song.name,
                                    modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop,
                                )
                            } else {
                                Box(
                                    Modifier.fillMaxSize()
                                        .background(Brush.linearGradient(listOf(Color(0xFF1A0A3A), Color(0xFF2D1566)))),
                                    contentAlignment = Alignment.Center,
                                ) { Icon(Icons.Rounded.MusicNote, null, tint = VioletSoft, modifier = Modifier.size(36.dp)) }
                            }
                        }

                        Spacer(Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            val infiniteTransition = rememberInfiniteTransition(label = "dot")
                            val dotPulse by infiniteTransition.animateFloat(
                                0.4f, 1f, infiniteRepeatable(tween(800), RepeatMode.Reverse), label = "dot"
                            )
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                                Box(
                                    Modifier.size(7.dp).clip(CircleShape)
                                        .background(Brush.linearGradient(listOf(VioletPrimary.copy(dotPulse), PinkAccent.copy(dotPulse))))
                                )
                                Text("NOW PLAYING", style = MaterialTheme.typography.labelSmall, color = PinkSoft, letterSpacing = 1.5.sp, fontSize = 9.sp)
                            }
                            Spacer(Modifier.height(5.dp))
                            Text(song.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(song.getPrimaryArtist(), style = MaterialTheme.typography.bodySmall, color = Color(0xFF9999BB), maxLines = 1)
                        }

                        Spacer(Modifier.width(6.dp))

                        // Compact controls
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(36.dp).clip(CircleShape)
                                    .background(SurfaceElevated.copy(0.5f)).clickable { PlayerController.playPrevious() },
                                contentAlignment = Alignment.Center,
                            ) { Icon(Icons.Rounded.SkipPrevious, null, tint = Color(0xFFCCCCDD), modifier = Modifier.size(20.dp)) }

                            Spacer(Modifier.width(8.dp))

                            Box(
                                modifier = Modifier.size(52.dp).clip(CircleShape)
                                    .background(Brush.linearGradient(listOf(VioletPrimary, PinkAccent)))
                                    .clickable { PlayerController.togglePlayPause() },
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                    null, tint = Color.White, modifier = Modifier.size(28.dp),
                                )
                            }

                            Spacer(Modifier.width(8.dp))

                            Box(
                                modifier = Modifier.size(36.dp).clip(CircleShape)
                                    .background(SurfaceElevated.copy(0.5f)).clickable { PlayerController.playNext() },
                                contentAlignment = Alignment.Center,
                            ) { Icon(Icons.Rounded.SkipNext, null, tint = Color(0xFFCCCCDD), modifier = Modifier.size(20.dp)) }
                        }
                    }
                }
            } else {
                // Empty state card
                Box(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).height(150.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(SurfaceCard)
                        .border(1.dp, Brush.linearGradient(listOf(Color(0xFF2E2E4A), Color(0xFF1E1E32))), RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(
                            modifier = Modifier.size(52.dp).clip(CircleShape)
                                .background(Brush.linearGradient(listOf(VioletPrimary.copy(0.15f), PinkAccent.copy(0.12f)))),
                            contentAlignment = Alignment.Center,
                        ) { Icon(Icons.Rounded.LibraryMusic, null, tint = VioletSoft, modifier = Modifier.size(26.dp)) }
                        Text("Nothing playing yet", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = Color(0xFF7777AA))
                        Text("Search for music to start vibing", style = MaterialTheme.typography.labelSmall, color = Color(0xFF44445A))
                    }
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        // ── Quick Picks ────────────────────────────────────────────
        SectionHeader("Quick picks", "Play all")
        Spacer(Modifier.height(10.dp))

        QuickPickRow(Icons.Rounded.MusicNote, listOf(Color(0xFF8B5CF6), Color(0xFFA78BFA)), "Search your favourites", "Use the Search tab")
        QuickPickRow(Icons.Rounded.History,   listOf(Color(0xFF06B6D4), Color(0xFF67E8F9)), "Recently played",        "Will appear here automatically")
        QuickPickRow(Icons.Rounded.Favorite,  listOf(Color(0xFFEC4899), Color(0xFFF9A8D4)), "Liked songs",            "Tap ♥ while playing")

        Spacer(Modifier.height(32.dp))

        // ── Keep Listening ─────────────────────────────────────────
        SectionHeader("Keep listening", "See all")
        Spacer(Modifier.height(16.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            itemsIndexed(ALBUM_CARDS) { _, (bgs, accents, title, sub) ->
                AlbumCard(bgs, accents, title, sub)
            }
        }

        Spacer(Modifier.height(32.dp))

        // ── Trending Artists Row ───────────────────────────────────
        SectionHeader("Top Artists", "View all")
        Spacer(Modifier.height(14.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            val artists = listOf(
                "Arijit Singh" to listOf(Color(0xFF7C3AED), Color(0xFFEC4899)),
                "The Weeknd"   to listOf(Color(0xFF0D3060), Color(0xFF06B6D4)),
                "A.R. Rahman"  to listOf(Color(0xFF1A3A0A), Color(0xFF22C55E)),
                "Dua Lipa"     to listOf(Color(0xFF5A0A1A), Color(0xFFEC4899)),
                "Drake"        to listOf(Color(0xFF1A1060), Color(0xFF6366F1)),
            )
            itemsIndexed(artists) { _, (name, colors) ->
                ArtistChip(name, colors)
            }
        }

        Spacer(Modifier.height(32.dp))

        // ── HiFi Badge ─────────────────────────────────────────────
        Box(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(SurfaceCard)
                .border(
                    1.dp,
                    Brush.linearGradient(listOf(VioletPrimary.copy(0.5f), PinkAccent.copy(0.4f))),
                    RoundedCornerShape(20.dp),
                )
                .padding(16.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(
                    modifier = Modifier.size(50.dp).clip(RoundedCornerShape(14.dp))
                        .background(Brush.linearGradient(listOf(VioletPrimary.copy(0.2f), PinkAccent.copy(0.2f)))),
                    contentAlignment = Alignment.Center,
                ) { Icon(Icons.Rounded.HighQuality, null, tint = VioletSoft, modifier = Modifier.size(28.dp)) }
                Column(modifier = Modifier.weight(1f)) {
                    Text("320kbps HiFi Audio", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Lossless streaming · JioSaavn", style = MaterialTheme.typography.bodySmall, color = Color(0xFF7777AA))
                }
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(50.dp))
                        .background(Brush.linearGradient(listOf(VioletPrimary, PinkAccent)))
                        .padding(horizontal = 14.dp, vertical = 7.dp),
                ) { Text("HiFi", style = MaterialTheme.typography.labelSmall, color = Color.White, fontWeight = FontWeight.Bold) }
            }
        }

        Spacer(Modifier.height(130.dp))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Sub-composables
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SectionHeader(title: String, action: String?) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.weight(1f))
        if (action != null) {
            Text(action, style = MaterialTheme.typography.labelLarge, color = VioletSoft, modifier = Modifier.clickable {})
        }
    }
}

@Composable
private fun QuickPickRow(icon: ImageVector, gradient: List<Color>, title: String, subtitle: String) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable {}.padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.size(56.dp).clip(RoundedCornerShape(14.dp))
                .background(Brush.linearGradient(gradient.map { it.copy(alpha = 0.15f) }))
                .border(1.dp, Brush.linearGradient(gradient.map { it.copy(0.3f) }), RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center,
        ) { Icon(icon, null, tint = gradient[0], modifier = Modifier.size(24.dp)) }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title,    style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = Color.White)
            Text(subtitle, style = MaterialTheme.typography.bodySmall,  color = Color(0xFF6666AA))
        }
        Icon(Icons.Rounded.ChevronRight, null, tint = Color(0xFF3A3A5A), modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun AlbumCard(bgGradient: List<Color>, accentGradient: List<Color>, title: String, subtitle: String) {
    Column(modifier = Modifier.width(150.dp)) {
        Box(
            modifier = Modifier.size(150.dp).clip(RoundedCornerShape(20.dp))
                .background(Brush.linearGradient(bgGradient))
                .border(1.dp, Brush.linearGradient(accentGradient.map { it.copy(0.35f) }), RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Rounded.Album, null, tint = Color.White.copy(0.06f), modifier = Modifier.size(80.dp))
            Box(
                modifier = Modifier.align(Alignment.BottomEnd).padding(10.dp)
                    .size(38.dp).clip(CircleShape)
                    .background(Brush.linearGradient(accentGradient)),
                contentAlignment = Alignment.Center,
            ) { Icon(Icons.Rounded.PlayArrow, null, tint = Color.White, modifier = Modifier.size(22.dp)) }
        }
        Spacer(Modifier.height(10.dp))
        Text(title,    style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = Color.White, maxLines = 1)
        Text(subtitle, style = MaterialTheme.typography.bodySmall,  color = Color(0xFF6666AA), maxLines = 1)
    }
}

@Composable
private fun ArtistChip(name: String, gradient: List<Color>) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(80.dp)) {
        Box(
            modifier = Modifier.size(80.dp).clip(CircleShape)
                .background(Brush.linearGradient(gradient))
                .border(1.5.dp, Brush.linearGradient(gradient.map { it.copy(0.5f) }), CircleShape)
                .clickable {},
            contentAlignment = Alignment.Center,
        ) { Icon(Icons.Rounded.Person, null, tint = Color.White.copy(0.4f), modifier = Modifier.size(36.dp)) }
        Spacer(Modifier.height(6.dp))
        Text(name, style = MaterialTheme.typography.labelSmall, color = Color(0xFFCCCCDD), maxLines = 2, fontSize = 10.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
    }
}
