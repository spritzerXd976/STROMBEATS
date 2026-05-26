package com.stormbeats.app.ui.compose

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.stormbeats.app.ui.theme.*
import com.stormbeats.app.util.PlayerController
import com.stormbeats.app.util.UpdateManager

private data class MoodChip(val label: String, val icon: ImageVector, val gradient: List<Color>)

private val moods = listOf(
    MoodChip("Workout",   Icons.Rounded.FitnessCenter,      listOf(Color(0xFFFF6B6B), Color(0xFFEE5A24))),
    MoodChip("Energize",  Icons.Rounded.Bolt,               listOf(Color(0xFFFECA57), Color(0xFFFF9F43))),
    MoodChip("Relax",     Icons.Rounded.SelfImprovement,    listOf(Color(0xFF48CAE4), Color(0xFF0096C7))),
    MoodChip("Vibes",     Icons.Rounded.SentimentSatisfied, listOf(GradientStart, GradientEnd)),
    MoodChip("Chill",     Icons.Rounded.NightsStay,         listOf(Color(0xFF6C5CE7), Color(0xFFA29BFE))),
    MoodChip("Focus",     Icons.Rounded.Headphones,         listOf(Color(0xFF00B894), Color(0xFF00CEC9))),
)

@Composable
fun HomeScreen(onShowPlayer: () -> Unit) {
    val currentSong by PlayerController.currentSong.collectAsState()
    val isPlaying   by PlayerController.isPlaying.collectAsState()
    val context     = LocalContext.current
    var showUpdateDialog by remember { mutableStateOf(false) }
    var updateResult by remember { mutableStateOf<UpdateManager.UpdateResult.UpdateAvailable?>(null) }
    var selectedMood by remember { mutableIntStateOf(-1) }

    LaunchedEffect(Unit) {
        val result = UpdateManager.checkForUpdate(context)
        if (result is UpdateManager.UpdateResult.UpdateAvailable) {
            updateResult = result
            showUpdateDialog = true
        }
    }

    if (showUpdateDialog && updateResult != null) {
        AlertDialog(
            onDismissRequest = { showUpdateDialog = false },
            icon  = { Icon(Icons.Rounded.SystemUpdate, contentDescription = null, tint = VioletPrimary) },
            title = { Text("Update Available", fontWeight = FontWeight.Bold) },
            text  = {
                Text(
                    "Version ${updateResult!!.release.tagName} is ready.\n\n${updateResult!!.release.body}",
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        UpdateManager.downloadAndInstall(context, updateResult!!.downloadUrl, updateResult!!.release.tagName)
                        showUpdateDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VioletPrimary,
                    ),
                ) { Text("Update Now") }
            },
            dismissButton = {
                TextButton(onClick = { showUpdateDialog = false }) { Text("Later") }
            },
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

        // ── Ambient orbs at top ─────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
        ) {
            // Violet orb
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .offset(x = (-40).dp, y = (-60).dp)
                    .blur(80.dp)
                    .background(VioletPrimary.copy(alpha = 0.25f), CircleShape)
            )
            // Pink orb
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 40.dp, y = (-40).dp)
                    .blur(80.dp)
                    .background(PinkAccent.copy(alpha = 0.2f), CircleShape)
            )

            // ── Top Bar ────────────────────────────────────────────
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Logo + Brand
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(VioletPrimary, PinkAccent)
                                    )
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                Icons.Rounded.Bolt,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(26.dp),
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                "StormBeats",
                                style     = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color     = Color.White,
                            )
                            Text(
                                "Good evening ✨",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF8888AA),
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
                        Icon(Icons.Outlined.Notifications, contentDescription = null, tint = Color(0xFF9999BB), modifier = Modifier.size(20.dp))
                    }
                }

                // ── Mood Chips ─────────────────────────────────────
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(moods) { mood ->
                        val idx = moods.indexOf(mood)
                        val isSelected = selectedMood == idx
                        val chipBg = if (isSelected)
                            Brush.linearGradient(mood.gradient)
                        else
                            Brush.linearGradient(listOf(SurfaceElevated, SurfaceElevated))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50.dp))
                                .background(chipBg)
                                .border(
                                    width = if (isSelected) 0.dp else 1.dp,
                                    brush = if (isSelected) Brush.linearGradient(mood.gradient) else Brush.linearGradient(listOf(Color(0xFF2E2E4A), Color(0xFF2E2E4A))),
                                    shape = RoundedCornerShape(50.dp),
                                )
                                .clickable { selectedMood = if (isSelected) -1 else idx }
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp),
                            ) {
                                Icon(
                                    mood.icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(13.dp),
                                    tint = if (isSelected) Color.White else mood.gradient[0],
                                )
                                Text(
                                    mood.label,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (isSelected) Color.White else Color(0xFFAAAACC),
                                    fontSize = 12.sp,
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // ── Now Playing Hero Card ──────────────────────────────────
        AnimatedContent(
            targetState = currentSong,
            transitionSpec = { fadeIn(tween(400)) togetherWith fadeOut(tween(200)) },
            label = "hero",
        ) { song ->
            if (song != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .clickable(onClick = onShowPlayer),
                ) {
                    // Blurred album art background
                    val imageUrl = song.getImageUrl()
                    if (imageUrl.isNotEmpty()) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .blur(40.dp),
                            contentScale = ContentScale.Crop,
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF1A0A3A), Color(0xFF2D1566))
                                    )
                                )
                        )
                    }
                    // Gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xBB0D0D14), Color(0xDD0D0D14))
                                )
                            )
                    )
                    // Top gradient accent
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .align(Alignment.TopCenter)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color.Transparent, VioletPrimary, PinkAccent, Color.Transparent)
                                )
                            )
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // Album art
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .border(
                                    width = 1.5.dp,
                                    brush = Brush.linearGradient(listOf(VioletPrimary.copy(0.5f), PinkAccent.copy(0.5f))),
                                    shape = RoundedCornerShape(16.dp),
                                ),
                        ) {
                            if (imageUrl.isNotEmpty()) {
                                AsyncImage(
                                    model = imageUrl,
                                    contentDescription = song.name,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop,
                                )
                            } else {
                                Box(
                                    Modifier
                                        .fillMaxSize()
                                        .background(Brush.linearGradient(listOf(Color(0xFF1A0A3A), Color(0xFF2D1566)))),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(Icons.Rounded.MusicNote, null, tint = VioletSoft, modifier = Modifier.size(36.dp))
                                }
                            }
                        }
                        Spacer(Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                            val pulse by infiniteTransition.animateFloat(
                                0.4f, 1f,
                                infiniteRepeatable(tween(800), RepeatMode.Reverse),
                                label = "pulse",
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp),
                            ) {
                                Box(
                                    Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.linearGradient(listOf(VioletPrimary, PinkAccent))
                                        )
                                        .then(Modifier.background(VioletPrimary.copy(alpha = pulse), CircleShape))
                                )
                                Text(
                                    "NOW PLAYING",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PinkSoft,
                                    letterSpacing = 1.5.sp,
                                    fontSize = 9.sp,
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(
                                song.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1,
                            )
                            Text(
                                song.getPrimaryArtist(),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF9999BB),
                                maxLines = 1,
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        IconButton(
                            onClick = { PlayerController.playPrevious() },
                            modifier = Modifier.size(36.dp),
                        ) {
                            Icon(Icons.Rounded.SkipPrevious, null, tint = Color(0xFFCCCCDD), modifier = Modifier.size(22.dp))
                        }
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(VioletPrimary, PinkAccent)))
                                .clickable { PlayerController.togglePlayPause() },
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                null,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp),
                            )
                        }
                        IconButton(
                            onClick = { PlayerController.playNext() },
                            modifier = Modifier.size(36.dp),
                        ) {
                            Icon(Icons.Rounded.SkipNext, null, tint = Color(0xFFCCCCDD), modifier = Modifier.size(22.dp))
                        }
                    }
                }
            } else {
                // Empty hero
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .height(160.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(SurfaceCard)
                        .border(
                            width = 1.dp,
                            brush = Brush.linearGradient(listOf(Color(0xFF2E2E4A), Color(0xFF1E1E32))),
                            shape = RoundedCornerShape(24.dp),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(VioletPrimary.copy(0.15f), PinkAccent.copy(0.15f)))),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                Icons.Rounded.LibraryMusic,
                                null,
                                modifier = Modifier.size(28.dp),
                                tint = VioletSoft,
                            )
                        }
                        Text(
                            "Nothing playing yet",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF7777AA),
                        )
                        Text(
                            "Search for music to start vibing",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF44445A),
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        // ── Quick Picks ────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "Quick picks",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.weight(1f),
            )
            Text(
                "Play all",
                style = MaterialTheme.typography.labelLarge,
                color = VioletSoft,
                modifier = Modifier.clickable {},
            )
        }
        Spacer(Modifier.height(12.dp))
        QuickPickRow(Icons.Rounded.MusicNote,  listOf(Color(0xFF8B5CF6), Color(0xFFA78BFA)), "Search your favourites", "Use the Search tab")
        QuickPickRow(Icons.Rounded.History,     listOf(Color(0xFF06B6D4), Color(0xFF67E8F9)), "Recently played",       "Will appear here automatically")
        QuickPickRow(Icons.Rounded.Favorite,    listOf(Color(0xFFEC4899), Color(0xFFF9A8D4)), "Liked songs",           "Tap ♥ while playing")

        Spacer(Modifier.height(32.dp))

        // ── Keep Listening ─────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "Keep listening",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.weight(1f),
            )
            Text(
                "See all",
                style = MaterialTheme.typography.labelLarge,
                color = VioletSoft,
                modifier = Modifier.clickable {},
            )
        }
        Spacer(Modifier.height(16.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            val cards = listOf(
                Triple(listOf(Color(0xFF1A0A3A), Color(0xFF3D1A7A)), listOf(VioletPrimary, VioletSoft), "Deep Focus"),
                Triple(listOf(Color(0xFF0A1A2A), Color(0xFF0D3060)), listOf(CyanAccent, Color(0xFF0EA5E9)), "Late Night"),
                Triple(listOf(Color(0xFF2A0A1A), Color(0xFF60103A)), listOf(PinkAccent, PinkSoft), "Bharat Hits"),
                Triple(listOf(Color(0xFF0A0A2A), Color(0xFF1A1060)), listOf(Color(0xFF6366F1), Color(0xFF818CF8)), "Old School"),
                Triple(listOf(Color(0xFF1A0A0A), Color(0xFF4A1508)), listOf(Color(0xFFFF6B6B), Color(0xFFFF8E53)), "Party Mix"),
            )
            val subs = listOf("Instrumental", "R&B Vibes", "Bollywood", "90s Classics", "Dance & EDM")
            items(cards.size) { i ->
                val (bgs, accents, title) = cards[i]
                AlbumCard(bgs, accents, title, subs[i])
            }
        }

        Spacer(Modifier.height(32.dp))

        // ── HiFi Badge ─────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(SurfaceCard)
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(listOf(VioletPrimary.copy(0.4f), PinkAccent.copy(0.3f))),
                    shape = RoundedCornerShape(20.dp),
                )
                .padding(16.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Brush.linearGradient(listOf(VioletPrimary.copy(0.2f), PinkAccent.copy(0.2f)))),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Rounded.HighQuality, null, tint = VioletSoft, modifier = Modifier.size(28.dp))
                }
                Column {
                    Text(
                        "320kbps HiFi Audio",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                    Text(
                        "Lossless streaming via JioSaavn",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF7777AA),
                    )
                }
                Spacer(Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(Brush.linearGradient(listOf(VioletPrimary, PinkAccent)))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                ) {
                    Text("HiFi", style = MaterialTheme.typography.labelSmall, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(Modifier.height(120.dp))
    }
}

@Composable
private fun QuickPickRow(icon: ImageVector, gradient: List<Color>, title: String, subtitle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {}
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Brush.linearGradient(gradient.map { it.copy(alpha = 0.15f) }))
                .border(
                    1.dp,
                    Brush.linearGradient(gradient.map { it.copy(0.3f) }),
                    RoundedCornerShape(14.dp),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, tint = gradient[0], modifier = Modifier.size(24.dp))
        }
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
    Column(modifier = Modifier.width(148.dp)) {
        Box(
            modifier = Modifier
                .size(148.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Brush.linearGradient(bgGradient))
                .border(
                    1.dp,
                    Brush.linearGradient(accentGradient.map { it.copy(0.3f) }),
                    RoundedCornerShape(20.dp),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Rounded.Album, null,
                tint = Color.White.copy(alpha = 0.08f),
                modifier = Modifier.size(80.dp),
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(10.dp)
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(accentGradient)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Rounded.PlayArrow, null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(title,    style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = Color.White, maxLines = 1)
        Text(subtitle, style = MaterialTheme.typography.bodySmall,  color = Color(0xFF6666AA),       maxLines = 1)
    }
}
