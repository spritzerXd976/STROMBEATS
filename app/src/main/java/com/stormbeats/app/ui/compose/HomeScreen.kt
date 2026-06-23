package com.stormbeats.app.ui.compose

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.stormbeats.app.ui.theme.*
import com.stormbeats.app.util.PlayerController
import com.stormbeats.app.util.UpdateManager
import kotlinx.coroutines.launch
import java.util.Calendar

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
    AlbumCardData(listOf(Color(0xFF0E0428), Color(0xFF2D1566)), listOf(VioletPrimary, VioletSoft),        "Deep Focus",  "Instrumental"),
    AlbumCardData(listOf(Color(0xFF040E1C), Color(0xFF0B2850)), listOf(CyanAccent, Color(0xFF0EA5E9)),    "Late Night",  "R&B Vibes"),
    AlbumCardData(listOf(Color(0xFF1C040E), Color(0xFF500824)), listOf(PinkAccent, PinkSoft),             "Bharat Hits", "Bollywood"),
    AlbumCardData(listOf(Color(0xFF060620), Color(0xFF12084A)), listOf(Color(0xFF6366F1), Color(0xFF818CF8)), "Old School", "90s Classics"),
    AlbumCardData(listOf(Color(0xFF140604), Color(0xFF3A1006)), listOf(Color(0xFFFF6B6B), Color(0xFFFF8E53)), "Party Mix",  "Dance & EDM"),
)

private fun getGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when {
        hour < 5  -> "Night Owl ⚡"
        hour < 12 -> "Good Morning ☀️"
        hour < 17 -> "Good Afternoon 🎵"
        hour < 21 -> "Good Evening 🌙"
        else      -> "Late Night 🎧"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
            icon = { Icon(Icons.Rounded.SystemUpdate, null) },
            title = { Text("Update Available") },
            text  = { Text("Version ${updateResult!!.release.tagName} is ready.\n\n${updateResult!!.release.body}") },
            confirmButton = {
                Button(onClick = { UpdateManager.downloadAndInstall(context, updateResult!!.downloadUrl, updateResult!!.release.tagName); showUpdate = false }) {
                    Text("Update Now")
                }
            },
            dismissButton = { TextButton(onClick = { showUpdate = false }) { Text("Later") } },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState()),
    ) {
        // ── Top bar with gradient greeting ──────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = getGreeting(),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        brush = Brush.horizontalGradient(listOf(MaterialTheme.colorScheme.onBackground, MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary))
                    ),
                    fontWeight = FontWeight.Bold,
                )
            }
            FilledTonalIconButton(onClick = {}) { Icon(Icons.Rounded.History, null) }
            FilledTonalIconButton(onClick = {}) { Icon(Icons.Rounded.TrendingUp, null) }
            FilledTonalIconButton(onClick = {}) { Icon(Icons.Rounded.AccountCircle, null) }
        }

        // ── Mood chips ─────────────────────────────────────────────────────
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 20.dp),
        ) {
            itemsIndexed(MOODS) { idx, mood ->
                val selected = selectedMood == idx
                FilterChip(
                    selected = selected,
                    onClick = { selectedMood = if (selected) -1 else idx },
                    label = { Text(mood.label) },
                    leadingIcon = { Icon(mood.icon, contentDescription = null, modifier = Modifier.size(FilterChipDefaults.IconSize)) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = selected,
                        borderColor = MaterialTheme.colorScheme.outlineVariant,
                        selectedBorderColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = CircleShape,
                )
            }
        }

        // ── Now playing hero card ──────────────────────────────────────────
        AnimatedVisibility(
            visible = currentSong != null,
            enter = fadeIn(tween(400)) + expandVertically(tween(400)),
            exit  = fadeOut(tween(250)) + shrinkVertically(tween(250)),
        ) {
            currentSong?.let { song ->
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    onClick = onShowPlayer,
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    ),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp),
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Brush.linearGradient(listOf(MaterialTheme.colorScheme.surfaceContainerHigh, MaterialTheme.colorScheme.surfaceContainerHighest)))
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center,
                            ) {
                                val url = song.getImageUrl()
                                if (url.isNotEmpty()) {
                                    AsyncImage(url, song.name, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                                } else {
                                    Icon(Icons.Rounded.MusicNote, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
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
                                        Modifier.size(8.dp).clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = dotPulse))
                                    )
                                    Text("Now Playing", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                                }
                                Spacer(Modifier.height(4.dp))
                                Text(song.name, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(song.getPrimaryArtist(), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                            }
                            // Pulsing play button
                            val playPulse by rememberInfiniteTransition(label = "pp").animateFloat(
                                1f, 1.08f,
                                infiniteRepeatable(tween(1000), RepeatMode.Reverse),
                                label = "ppScale",
                            )
                            FilledIconButton(
                                onClick = { PlayerController.togglePlayPause() },
                                modifier = Modifier
                                    .size(48.dp)
                                    .scale(if (isPlaying) playPulse else 1f),
                                colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow, null, modifier = Modifier.size(24.dp))
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // ── Quick picks — 2-column grid ───────────────────────────────
        SectionHeader("Quick picks", action = "Play all")

        Spacer(Modifier.height(10.dp))

        data class QuickItem(val icon: ImageVector, val title: String, val subtitle: String)
        val quickItems = listOf(
            QuickItem(Icons.Rounded.Search,     "Discover music",    "Search JioSaavn"),
            QuickItem(Icons.Rounded.AudioFile,  "320kbps HiFi Audio", "Lossless quality"),
            QuickItem(Icons.Rounded.Bolt,       "StormBeats",       "Your music player"),
            QuickItem(Icons.Rounded.QueueMusic, "Queue songs",      "Tap a song to play"),
        )

        // 2-column grid
        Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            for (row in quickItems.chunked(2)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    row.forEach { item ->
                        QuickPickCard(
                            icon = item.icon,
                            title = item.title,
                            subtitle = item.subtitle,
                            modifier = Modifier.weight(1f),
                        )
                    }
                    // Fill remaining space if odd count
                    if (row.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }

        Spacer(Modifier.height(28.dp))

        // ── Featured — larger album cards ──────────────────────────────────
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

        Spacer(Modifier.height(28.dp))

        // ── Popular Artists ─────────────────
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
            horizontalArrangement = Arrangement.spacedBy(16.dp),
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
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(24.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(MaterialTheme.colorScheme.primary)
        )
        Spacer(Modifier.width(10.dp))
        Text(title, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        if (action != null) {
            TextButton(onClick = {}) {
                Text(action, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuickPickCard(icon: ImageVector, title: String, subtitle: String, modifier: Modifier = Modifier) {
    ElevatedCard(
        onClick = {},
        modifier = modifier.height(88.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center,
            ) { Icon(icon, null, tint = MaterialTheme.colorScheme.onSecondaryContainer, modifier = Modifier.size(24.dp)) }
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AlbumCard(bgGradient: List<Color>, accentGradient: List<Color>, title: String, subtitle: String) {
    Card(
        onClick = {},
        modifier = Modifier.size(170.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(18.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(bgGradient))
        ) {
            Box(
                modifier = Modifier.size(80.dp).align(Alignment.TopEnd)
                    .offset(x = 10.dp, y = (-10).dp)
                    .clip(CircleShape)
                    .background(Brush.radialGradient(accentGradient.map { it.copy(0.2f) }))
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .align(Alignment.BottomCenter)
                    .background(Brush.verticalGradient(listOf(Color.Transparent, bgGradient.last().copy(0.9f))))
            )
            Column(
                modifier = Modifier.align(Alignment.BottomStart).padding(14.dp),
            ) {
                Text(title,    style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold, maxLines = 1)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha=0.8f), maxLines = 1)
            }
            FilledIconButton(
                onClick = { },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .size(36.dp),
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Icon(Icons.Rounded.PlayArrow, null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
private fun ArtistChip(name: String, gradient: List<Color>) {
    val infiniteTransition = rememberInfiniteTransition(label = "artistRing")
    val ringRotation by infiniteTransition.animateFloat(
        0f, 360f,
        infiniteRepeatable(tween(8000, easing = LinearEasing)),
        label = "artistRingRot",
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(90.dp).clickable {},
    ) {
        Box(contentAlignment = Alignment.Center) {
            // Animated gradient ring
            Box(
                modifier = Modifier
                    .size(86.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.sweepGradient(
                            listOf(
                                gradient.first().copy(0.4f),
                                gradient.last().copy(0.6f),
                                Color.Transparent,
                                gradient.first().copy(0.4f),
                            )
                        )
                    )
                    .padding(2.dp)
            )
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(3.dp)
            )
            Box(
                modifier = Modifier
                    .size(74.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(gradient.map { it.copy(0.2f) })),
                contentAlignment = Alignment.Center,
            ) { Icon(Icons.Rounded.Person, null, tint = Color.White.copy(0.7f), modifier = Modifier.size(34.dp)) }
        }
        Spacer(Modifier.height(8.dp))
        Text(name, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
    }
}

