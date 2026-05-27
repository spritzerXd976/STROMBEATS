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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.stormbeats.app.data.model.Song
import com.stormbeats.app.ui.home.HomeViewModel
import com.stormbeats.app.ui.theme.*
import com.stormbeats.app.util.PlayerController
import kotlinx.coroutines.delay

private data class MoodChip(val label: String, val icon: ImageVector, val bg: Color, val tint: Color)
private val moods = listOf(
    MoodChip("Workout",  Icons.Rounded.FitnessCenter,      Purple500,            Color.White),
    MoodChip("Energize", Icons.Rounded.Bolt,               Color(0xFF2D2D2D),    Color.White),
    MoodChip("Relax",    Icons.Rounded.SelfImprovement,    Color(0xFFF0F0F0),    Color(0xFF444444)),
    MoodChip("Feel Good",Icons.Rounded.SentimentSatisfied, Color(0xFFF0F0F0),    Color(0xFF444444)),
    MoodChip("Chill",    Icons.Rounded.NightsStay,         Color(0xFFF0F0F0),    Color(0xFF444444)),
    MoodChip("Focus",    Icons.Rounded.Headphones,         Color(0xFFF0F0F0),    Color(0xFF444444)),
)

private data class QuickPick(val icon: ImageVector, val iconTint: Color, val iconBg: Color, val label: String, val sub: String)
private val quickPicks = listOf(
    QuickPick(Icons.Rounded.Search,         Purple500,            Purple100,            "Discover music",         "Search your favourites"),
    QuickPick(Icons.Rounded.HighQuality,    Color(0xFF00C853),    Color(0xFFE8FFF0),    "320kbps HiFi Audio",     "Best quality streaming"),
    QuickPick(Icons.Rounded.Bolt,           Color(0xFFFF6D00),    Color(0xFFFFF3E0),    "StormBeats\nYour player",  "Fast & powerful"),
    QuickPick(Icons.Rounded.Favorite,       Color(0xFFE91E63),    Color(0xFFFFE4EF),    "Favorites\nSongs",       "Tap ♥ while playing"),
)

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onShowPlayer: () -> Unit,
    onSongClick: (Song) -> Unit,
) {
    val featured   by viewModel.featuredSongs.collectAsState()
    val recent     by viewModel.recentSongs.collectAsState()
    val isLoading  by viewModel.isLoading.collectAsState()
    val currentSong by PlayerController.currentSong.collectAsState()
    val isPlaying  by PlayerController.isPlaying.collectAsState()
    var selectedMood by remember { mutableIntStateOf(0) }

    // Position tracker for the now-playing card
    var posMs by remember { mutableLongStateOf(0L) }
    var durMs by remember { mutableLongStateOf(1L) }
    LaunchedEffect(currentSong) {
        while (true) {
            posMs = PlayerController.getCurrentPosition()
            durMs = PlayerController.getDuration().coerceAtLeast(1L)
            delay(500)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState()),
    ) {
        // ── Top Bar ───────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Rounded.Menu, null, tint = MaterialTheme.colorScheme.onBackground, modifier = Modifier.size(26.dp))
            Spacer(Modifier.width(14.dp))
            Text(
                "Home",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
            )
            // Notification bell
            Box {
                Icon(Icons.Rounded.Notifications, null, tint = MaterialTheme.colorScheme.onBackground, modifier = Modifier.size(26.dp))
                Box(
                    Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFF5252))
                        .align(Alignment.TopEnd)
                )
            }
            Spacer(Modifier.width(14.dp))
            // Avatar
            Box(
                Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(Purple500, PinkAccent))),
                contentAlignment = Alignment.Center,
            ) {
                Text("S", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        // ── Search Bar (decorative, taps go to Search tab) ───────
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clickable { /* handled in MainScreen via tab switch */ },
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp,
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Rounded.Search, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(10.dp))
                Text("Search songs, artists, albums...", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.weight(1f))
                Icon(Icons.Rounded.Tune, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
            }
        }

        Spacer(Modifier.height(18.dp))

        // ── Mood Chips ────────────────────────────────────────────
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            itemsIndexed(moods) { i, mood ->
                val selected = selectedMood == i
                val scale by animateFloatAsState(if (selected) 1.04f else 1f, label = "chipScale")
                Surface(
                    modifier = Modifier
                        .graphicsLayerHelper(scale)
                        .clickable { selectedMood = i },
                    shape = RoundedCornerShape(50.dp),
                    color = if (selected) mood.bg else MaterialTheme.colorScheme.surface,
                    shadowElevation = if (selected) 4.dp else 1.dp,
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Icon(
                            mood.icon, null,
                            modifier = Modifier.size(16.dp),
                            tint = if (selected) mood.tint else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            mood.label,
                            style = MaterialTheme.typography.labelLarge,
                            color = if (selected) mood.tint else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(18.dp))

        // ── Now Playing Card ──────────────────────────────────────
        AnimatedContent(
            targetState = currentSong,
            transitionSpec = { fadeIn(tween(400)) togetherWith fadeOut(tween(200)) },
            label = "nowPlayingHero",
        ) { song ->
            if (song != null) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .shadow(6.dp, RoundedCornerShape(20.dp))
                        .clickable(onClick = onShowPlayer),
                    shape     = RoundedCornerShape(20.dp),
                    color     = MaterialTheme.colorScheme.primaryContainer,
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Album art
                            Box(
                                Modifier
                                    .size(72.dp)
                                    .clip(RoundedCornerShape(14.dp))
                            ) {
                                val img = song.getImageUrl()
                                if (img.isNotEmpty()) {
                                    AsyncImage(
                                        model = img,
                                        contentDescription = song.name,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop,
                                    )
                                } else {
                                    Box(
                                        Modifier.fillMaxSize().background(Purple100),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Icon(Icons.Rounded.MusicNote, null, tint = Purple500, modifier = Modifier.size(32.dp))
                                    }
                                }
                            }
                            Spacer(Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                                    val inf = rememberInfiniteTransition(label = "eq")
                                    val alpha by inf.animateFloat(0.4f, 1f, infiniteRepeatable(tween(600), RepeatMode.Reverse), label = "eqA")
                                    repeat(4) { i ->
                                        val barAlpha by inf.animateFloat(
                                            0.3f, 1f,
                                            infiniteRepeatable(tween(400 + i * 80, easing = FastOutSlowInEasing), RepeatMode.Reverse),
                                            label = "bar$i",
                                        )
                                        Box(
                                            Modifier
                                                .width(3.dp)
                                                .height((8 + i * 3).dp)
                                                .clip(RoundedCornerShape(2.dp))
                                                .background(Purple500.copy(alpha = if (isPlaying) barAlpha else 0.3f))
                                        )
                                    }
                                    Spacer(Modifier.width(2.dp))
                                    Text(
                                        "Now Playing",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Purple500,
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                }
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    song.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Text(
                                    song.getPrimaryArtist(),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                )
                            }
                            Spacer(Modifier.width(8.dp))
                            // Play/Pause button
                            Box(
                                Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(Purple500)
                                    .clickable { PlayerController.togglePlayPause() },
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                    null, tint = Color.White, modifier = Modifier.size(28.dp),
                                )
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        // Progress bar
                        val progress = (posMs.toFloat() / durMs.toFloat()).coerceIn(0f, 1f)
                        Column {
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color      = Purple500,
                                trackColor = MaterialTheme.colorScheme.outline,
                            )
                            Spacer(Modifier.height(4.dp))
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(formatMs(posMs), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(formatMs(durMs), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            } else {
                // Empty hero
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .height(110.dp),
                    shape  = RoundedCornerShape(20.dp),
                    color  = MaterialTheme.colorScheme.primaryContainer,
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Icon(Icons.Rounded.LibraryMusic, null, tint = Purple500.copy(0.4f), modifier = Modifier.size(40.dp))
                            Column {
                                Text("Nothing playing yet", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Search music to start", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.6f))
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(26.dp))

        // ── Quick Picks ───────────────────────────────────────────
        SectionHeader(title = "Quick Picks", actionLabel = "See all", onAction = {})
        Spacer(Modifier.height(12.dp))
        // 2x2 grid
        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickPickCard(quickPicks[0], Modifier.weight(1f))
                QuickPickCard(quickPicks[1], Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickPickCard(quickPicks[2], Modifier.weight(1f))
                QuickPickCard(quickPicks[3], Modifier.weight(1f))
            }
        }

        Spacer(Modifier.height(26.dp))

        // ── Recently Played ───────────────────────────────────────
        SectionHeader(title = "Recently Played", actionLabel = "See all", onAction = {})
        Spacer(Modifier.height(12.dp))

        if (isLoading && recent.isEmpty()) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = Purple500, modifier = Modifier.size(32.dp))
            }
        } else {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                itemsIndexed(recent.take(6)) { _, song ->
                    RecentAlbumCard(song = song, isPlaying = currentSong?.id == song.id, onClick = { onSongClick(song) })
                }
            }
        }

        Spacer(Modifier.height(26.dp))

        // ── Featured / Top Hits ───────────────────────────────────
        SectionHeader(title = "Top Hits", actionLabel = "See all", onAction = {})
        Spacer(Modifier.height(12.dp))

        if (isLoading && featured.isEmpty()) {
            Box(Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Purple500, modifier = Modifier.size(32.dp))
            }
        } else {
            Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                featured.take(5).forEach { song ->
                    SongListItem(
                        song      = song,
                        isPlaying = currentSong?.id == song.id,
                        onClick   = { onSongClick(song) },
                    )
                }
            }
        }

        Spacer(Modifier.height(120.dp))
    }
}

@Composable
private fun SectionHeader(title: String, actionLabel: String, onAction: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f),
        )
        TextButton(onClick = onAction, contentPadding = PaddingValues(horizontal = 4.dp)) {
            Text(actionLabel, style = MaterialTheme.typography.labelLarge, color = Purple500)
        }
    }
}

@Composable
private fun QuickPickCard(pick: QuickPick, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .shadow(3.dp, RoundedCornerShape(16.dp))
            .clickable {},
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(pick.iconBg),
                contentAlignment = Alignment.Center,
            ) {
                Icon(pick.icon, null, tint = pick.iconTint, modifier = Modifier.size(22.dp))
            }
            Text(
                pick.label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 2,
                lineHeight = 17.sp,
            )
        }
    }
}

@Composable
private fun RecentAlbumCard(song: Song, isPlaying: Boolean, onClick: () -> Unit) {
    Column(modifier = Modifier.width(120.dp)) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(16.dp))
                .clickable(onClick = onClick),
        ) {
            val img = song.getImageUrl()
            if (img.isNotEmpty()) {
                AsyncImage(
                    model = img,
                    contentDescription = song.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            } else {
                Box(
                    Modifier.fillMaxSize().background(Brush.linearGradient(listOf(Purple100, MaterialTheme.colorScheme.primaryContainer))),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Rounded.Album, null, tint = Purple500, modifier = Modifier.size(48.dp))
                }
            }
            // Play overlay
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color(0x33000000))
            )
            Box(
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(if (isPlaying) Purple500 else Color.White),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    null,
                    tint = if (isPlaying) Color.White else Purple500,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            song.name,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            song.getPrimaryArtist(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun SongListItem(song: Song, isPlaying: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = Color.Transparent,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                val img = song.getImageUrl()
                if (img.isNotEmpty()) {
                    AsyncImage(img, song.name, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                } else {
                    Box(Modifier.fillMaxSize().background(Purple100), contentAlignment = Alignment.Center) {
                        Icon(Icons.Rounded.MusicNote, null, tint = Purple500, modifier = Modifier.size(24.dp))
                    }
                }
                if (isPlaying) {
                    Box(Modifier.fillMaxSize().background(Color(0x55000000)))
                    Icon(Icons.Rounded.VolumeUp, null, tint = Color.White, modifier = Modifier.size(16.dp).align(Alignment.Center))
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    song.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = if (isPlaying) FontWeight.Bold else FontWeight.Medium,
                    color = if (isPlaying) Purple500 else MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    song.getPrimaryArtist(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                )
            }
            song.duration?.let { dur ->
                Text(
                    "%d:%02d".format(dur / 60, dur % 60),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.width(4.dp))
            }
            Icon(Icons.Rounded.MoreVert, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
        }
    }
}

private fun formatMs(ms: Long): String {
    val s = ms / 1000
    return "%d:%02d".format(s / 60, s % 60)
}

// Helper to apply graphicsLayer scale
@Composable
private fun Modifier.graphicsLayerHelper(scale: Float): Modifier =
    this.graphicsLayer(scaleX = scale, scaleY = scale)
