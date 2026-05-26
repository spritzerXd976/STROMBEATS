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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.stormbeats.app.util.PlayerController
import com.stormbeats.app.util.UpdateManager

private data class MoodChip(val label: String, val icon: ImageVector, val color: Color)

private val moods = listOf(
    MoodChip("Workout",   Icons.Rounded.FitnessCenter,       Color(0xFFFF5252)),
    MoodChip("Energize",  Icons.Rounded.Bolt,                Color(0xFFFFD740)),
    MoodChip("Relax",     Icons.Rounded.SelfImprovement,     Color(0xFF64B5F6)),
    MoodChip("Feel good", Icons.Rounded.SentimentSatisfied,  Color(0xFFBA68C8)),
    MoodChip("Chill",     Icons.Rounded.NightsStay,          Color(0xFF4DD0E1)),
    MoodChip("Focus",     Icons.Rounded.Headphones,          Color(0xFF81C784)),
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
            icon  = { Icon(Icons.Rounded.SystemUpdate, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
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
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                ) { Text("Update Now") }
            },
            dismissButton = {
                TextButton(onClick = { showUpdateDialog = false }) { Text("Later") }
            },
            containerColor = Color(0xFF1A1A1A),
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState()),
    ) {

        // ── Top Bar ────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Logo + Brand
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFFFF0000), Color(0xFFCC0000))
                            )
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Rounded.Bolt,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp),
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
                        "Good evening",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF888888),
                    )
                }
            }
            IconButton(onClick = {}) {
                Icon(Icons.Outlined.Notifications, contentDescription = null, tint = Color(0xFF888888))
            }
        }

        // ── Mood Chips ─────────────────────────────────────────────
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(moods) { mood ->
                val idx = moods.indexOf(mood)
                val isSelected = selectedMood == idx
                FilterChip(
                    selected = isSelected,
                    onClick  = { selectedMood = if (isSelected) -1 else idx },
                    label = {
                        Text(
                            mood.label,
                            style = MaterialTheme.typography.labelMedium,
                            color = if (isSelected) Color.White else Color(0xFFAAAAAA),
                        )
                    },
                    leadingIcon = {
                        Icon(
                            mood.icon,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = if (isSelected) Color.White else mood.color,
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = mood.color.copy(alpha = 0.25f),
                        containerColor = Color(0xFF1A1A1A),
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = Color(0xFF2A2A2A),
                        selectedBorderColor = mood.color.copy(alpha = 0.5f),
                        borderWidth = 1.dp,
                        selectedBorderWidth = 1.dp,
                    ),
                )
            }
        }

        Spacer(Modifier.height(24.dp))

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
                        .clip(RoundedCornerShape(20.dp))
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
                                .height(160.dp)
                                .blur(30.dp),
                            contentScale = ContentScale.Crop,
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF1A0000), Color(0xFF330000))
                                    )
                                )
                        )
                    }
                    // Dark overlay
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .background(Color(0xCC000000))
                    )
                    // Red top accent line
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .align(Alignment.TopCenter)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color.Transparent, Color(0xFFFF0000), Color.Transparent)
                                )
                            )
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // Album art
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(12.dp)),
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
                                        .background(Color(0xFF2A0000)),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(Icons.Rounded.MusicNote, null, tint = Color(0xFFFF0000), modifier = Modifier.size(32.dp))
                                }
                            }
                        }
                        Spacer(Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                            val pulse by infiniteTransition.animateFloat(
                                0.5f, 1f,
                                infiniteRepeatable(tween(700), RepeatMode.Reverse),
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
                                        .background(Color(0xFFFF0000).copy(alpha = pulse))
                                )
                                Text(
                                    "NOW PLAYING",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFFFF0000),
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
                                color = Color(0xFFAAAAAA),
                                maxLines = 1,
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        IconButton(
                            onClick = { PlayerController.playPrevious() },
                            modifier = Modifier.size(36.dp),
                        ) {
                            Icon(Icons.Rounded.SkipPrevious, null, tint = Color(0xFFCCCCCC), modifier = Modifier.size(22.dp))
                        }
                        FilledIconButton(
                            onClick = { PlayerController.togglePlayPause() },
                            modifier = Modifier.size(48.dp),
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = Color(0xFFFF0000),
                                contentColor   = Color.White,
                            ),
                            shape = CircleShape,
                        ) {
                            Icon(
                                if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                null,
                                modifier = Modifier.size(26.dp),
                            )
                        }
                        IconButton(
                            onClick = { PlayerController.playNext() },
                            modifier = Modifier.size(36.dp),
                        ) {
                            Icon(Icons.Rounded.SkipNext, null, tint = Color(0xFFCCCCCC), modifier = Modifier.size(22.dp))
                        }
                    }
                }
            } else {
                // Empty hero
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .height(140.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF141414))
                        .border(1.dp, Color(0xFF2A2A2A), RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            Icons.Rounded.LibraryMusic,
                            null,
                            modifier = Modifier.size(48.dp),
                            tint = Color(0xFF333333),
                        )
                        Text(
                            "Nothing playing yet",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF555555),
                        )
                        Text(
                            "Search for music to start listening",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF3A3A3A),
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
            TextButton(onClick = {}) {
                Text("Play all", style = MaterialTheme.typography.labelLarge, color = Color(0xFF888888))
            }
        }
        Spacer(Modifier.height(8.dp))
        QuickPickRow(Icons.Rounded.MusicNote,  Color(0xFFCE93D8), Color(0xFF2A0050), "Search your favourites", "Use the Search tab")
        QuickPickRow(Icons.Rounded.History,     Color(0xFF90CAF9), Color(0xFF001A3A), "Recently played",       "Will appear here automatically")
        QuickPickRow(Icons.Rounded.Favorite,    Color(0xFFEF9A9A), Color(0xFF3A0000), "Liked songs",           "Tap ♥ while playing")

        Spacer(Modifier.height(32.dp))

        // ── Keep Listening ─────────────────────────────────────────
        Text(
            "Keep listening",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 20.dp),
        )
        Spacer(Modifier.height(16.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            val cards = listOf(
                Triple(Color(0xFF0D0D2B), Color(0xFF2A0060), "Deep Focus"),
                Triple(Color(0xFF0A1A0A), Color(0xFF003320), "Late Night"),
                Triple(Color(0xFF1A0A0A), Color(0xFF3A1200), "Bharat Hits"),
                Triple(Color(0xFF0A0A1A), Color(0xFF00104A), "Old School"),
                Triple(Color(0xFF1A1A0A), Color(0xFF3A2A00), "Party Mix"),
            )
            val subs = listOf("Instrumental", "R&B Vibes", "Bollywood", "90s Classics", "Dance & EDM")
            items(cards.size) { i ->
                val (bg1, bg2, title) = cards[i]
                AlbumCard(bg1, bg2, title, subs[i])
            }
        }

        Spacer(Modifier.height(32.dp))

        // ── HiFi Badge ─────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF0F0F0F))
                .border(1.dp, Color(0xFF2A0000), RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Icon(Icons.Rounded.HighQuality, null, tint = Color(0xFFFF0000), modifier = Modifier.size(32.dp))
            Column {
                Text(
                    "320kbps HiFi Audio",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF0000),
                )
                Text(
                    "Lossless streaming via JioSaavn",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF888888),
                )
            }
        }

        Spacer(Modifier.height(120.dp))
    }
}

@Composable
private fun QuickPickRow(icon: ImageVector, iconTint: Color, iconBg: Color, title: String, subtitle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {}
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, tint = iconTint, modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title,    style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = Color.White)
            Text(subtitle, style = MaterialTheme.typography.bodySmall,  color = Color(0xFF777777))
        }
        Icon(Icons.Rounded.ChevronRight, null, tint = Color(0xFF444444), modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun AlbumCard(gradientStart: Color, gradientEnd: Color, title: String, subtitle: String) {
    Column(modifier = Modifier.width(140.dp)) {
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Brush.linearGradient(listOf(gradientStart, gradientEnd))),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Rounded.Album, null,
                tint = Color.White.copy(alpha = 0.15f),
                modifier = Modifier.size(70.dp),
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFF0000)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Rounded.PlayArrow, null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(title,    style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = Color.White, maxLines = 1)
        Text(subtitle, style = MaterialTheme.typography.bodySmall,  color = Color(0xFF777777),       maxLines = 1)
    }
}
