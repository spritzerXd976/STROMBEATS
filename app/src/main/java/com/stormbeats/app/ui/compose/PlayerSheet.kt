package com.stormbeats.app.ui.compose

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
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
import com.stormbeats.app.ui.theme.*
import com.stormbeats.app.util.PlayerController
import kotlinx.coroutines.delay

@Composable
fun PlayerSheet(
    song: Song,
    isPlaying: Boolean,
    onDismiss: () -> Unit,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "aurora")

    // Drifting aurora orb offsets
    val orbX1 by infiniteTransition.animateFloat(
        -80f, 60f,
        infiniteRepeatable(tween(6000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "orbX1",
    )
    val orbY1 by infiniteTransition.animateFloat(
        -60f, 40f,
        infiniteRepeatable(tween(8000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "orbY1",
    )
    val orbX2 by infiniteTransition.animateFloat(
        40f, -50f,
        infiniteRepeatable(tween(7000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "orbX2",
    )
    val orbY2 by infiniteTransition.animateFloat(
        30f, -40f,
        infiniteRepeatable(tween(5500, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "orbY2",
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        // Base vertical gradient
        Box(
            modifier = Modifier.fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.background,
                        )
                    )
                )
        )
        // Drifting aurora orb 1
        Box(
            modifier = Modifier
                .size(320.dp)
                .offset(x = orbX1.dp, y = orbY1.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(listOf(MaterialTheme.colorScheme.primary.copy(alpha = 0.18f), Color.Transparent))
                )
        )
        // Drifting aurora orb 2
        Box(
            modifier = Modifier
                .size(260.dp)
                .align(Alignment.BottomEnd)
                .offset(x = orbX2.dp, y = orbY2.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(listOf(MaterialTheme.colorScheme.secondary.copy(alpha = 0.14f), Color.Transparent))
                )
        )
        // Subtle accent — top right
        Box(
            modifier = Modifier
                .size(180.dp)
                .align(Alignment.TopEnd)
                .offset(x = 60.dp, y = (-20).dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(listOf(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.06f), Color.Transparent))
                )
        )

        PlayerContent(song = song, isPlaying = isPlaying, onDismiss = onDismiss)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerContent(song: Song, isPlaying: Boolean, onDismiss: () -> Unit) {
    var positionMs    by remember { mutableLongStateOf(0L) }
    var durationMs    by remember { mutableLongStateOf(0L) }
    var isUserSeeking by remember { mutableStateOf(false) }
    var isShuffleOn   by remember { mutableStateOf(false) }
    var repeatMode    by remember { mutableIntStateOf(0) }
    var isLiked       by remember { mutableStateOf(false) }
    val isPlayingState by PlayerController.isPlaying.collectAsState()

    LaunchedEffect(Unit) {
        while (true) {
            if (!isUserSeeking) {
                positionMs = PlayerController.getCurrentPosition()
                durationMs = PlayerController.getDuration()
            }
            delay(500)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // ── Top Bar ─────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onDismiss) {
                Icon(Icons.Rounded.KeyboardArrowDown, null, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(32.dp))
            }

            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("NOW PLAYING", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, letterSpacing = 3.sp, fontSize = 9.sp)
                song.album?.name?.takeIf { it.isNotEmpty() }?.let {
                    Text(it, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }

            IconButton(onClick = {}) {
                Icon(Icons.Rounded.MoreVert, null, tint = MaterialTheme.colorScheme.onSurface)
            }
        }

        Spacer(Modifier.height(12.dp))

        // ── Album Art — vinyl rotation ──────────────────────────────────────
        val artScale by animateFloatAsState(
            targetValue = if (isPlayingState) 1f else 0.88f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
            label = "artScale",
        )
        val infiniteTransition = rememberInfiniteTransition(label = "vinyl")
        val vinylAngle by infiniteTransition.animateFloat(
            0f, 360f,
            infiniteRepeatable(tween(30000, easing = LinearEasing)),
            label = "vinylAngle",
        )
        val glowAlpha by infiniteTransition.animateFloat(
            0.05f, 0.35f,
            infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
            label = "glowAlpha",
        )
        // Glow ring color shift
        val glowHue by infiniteTransition.animateFloat(
            0f, 1f,
            infiniteRepeatable(tween(4000, easing = LinearEasing), RepeatMode.Restart),
            label = "glowHue",
        )

        Box(
            modifier = Modifier.size((320 * artScale).dp),
            contentAlignment = Alignment.Center,
        ) {
            // Layered glow rings when playing
            if (isPlayingState) {
                val glowColor1 = if (glowHue < 0.5f)
                    MaterialTheme.colorScheme.primary.copy(glowAlpha)
                else
                    MaterialTheme.colorScheme.secondary.copy(glowAlpha)
                val glowColor2 = if (glowHue < 0.5f)
                    MaterialTheme.colorScheme.secondary.copy(glowAlpha * 0.6f)
                else
                    MaterialTheme.colorScheme.tertiary.copy(glowAlpha * 0.5f)
                Box(
                    modifier = Modifier
                        .size((340 * artScale).dp)
                        .clip(RoundedCornerShape(34.dp))
                        .background(
                            Brush.radialGradient(listOf(glowColor1, glowColor2, Color.Transparent))
                        )
                )
            }
            ElevatedCard(
                modifier = Modifier
                    .size((320 * artScale).dp)
                    .graphicsLayer {
                        if (isPlayingState) rotationZ = vinylAngle
                    },
                shape = RoundedCornerShape(30.dp),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = if (isPlayingState) 12.dp else 4.dp),
            ) {
                val imgUrl = song.getImageUrl()
                if (imgUrl.isNotEmpty()) {
                    AsyncImage(
                        model = imgUrl,
                        contentDescription = song.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize()
                            .background(Brush.linearGradient(listOf(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.surface))),
                        contentAlignment = Alignment.Center,
                    ) { Icon(Icons.Rounded.MusicNote, null, modifier = Modifier.size(80.dp), tint = MaterialTheme.colorScheme.primary) }
                }
            }
        }

        Spacer(Modifier.height(28.dp))

        // ── Song Info ────────────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 28.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    song.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                // Gradient accent line under title
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Brush.horizontalGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary, Color.Transparent)))
                )
                Spacer(Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    if (song.explicitContent) {
                        Surface(shape = RoundedCornerShape(4.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                            Text("E", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp))
                        }
                    }
                    Text(
                        song.getPrimaryArtist(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    // Quality badge
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                    ) {
                        Text(
                            "HiFi",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            fontSize = 9.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        )
                    }
                }
            }
            Spacer(Modifier.width(12.dp))
            // Like button with animated state
            val likeScale by animateFloatAsState(
                targetValue = if (isLiked) 1.15f else 1f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label = "likeScale",
            )
            FilledIconToggleButton(
                checked = isLiked,
                onCheckedChange = { isLiked = it },
                modifier = Modifier.scale(likeScale).size(50.dp),
                colors = IconButtonDefaults.filledIconToggleButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    checkedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                )
            ) {
                Icon(
                    if (isLiked) Icons.Rounded.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Like",
                    tint = if (isLiked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp),
                )
            }
        }

        Spacer(Modifier.height(28.dp))

        // ── Seekbar ──────────────────────────────────
        val progress = if (durationMs > 0) positionMs.toFloat() / durationMs.toFloat() else 0f
        Column(modifier = Modifier.padding(horizontal = 28.dp)) {
            Slider(
                value = progress,
                onValueChange = { v -> isUserSeeking = true; positionMs = (v * durationMs).toLong() },
                onValueChangeFinished = { PlayerController.seekTo(positionMs); isUserSeeking = false },
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
            )
            Row(Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(fmtTime(positionMs), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(fmtTime(durationMs), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Spacer(Modifier.height(20.dp))

        // ── Controls ─────────────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconToggleButton(
                checked = isShuffleOn,
                onCheckedChange = { isShuffleOn = it },
                modifier = Modifier.size(48.dp),
                colors = IconButtonDefaults.iconToggleButtonColors(
                    checkedContentColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Icon(Icons.Rounded.Shuffle, contentDescription = "Shuffle", modifier = Modifier.size(24.dp))
            }

            FilledTonalIconButton(
                onClick = { PlayerController.playPrevious() },
                modifier = Modifier.size(64.dp),
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Icon(Icons.Rounded.SkipPrevious, contentDescription = "Previous", modifier = Modifier.size(32.dp))
            }

            // Large play button
            Box(contentAlignment = Alignment.Center) {
                if (isPlayingState) {
                    val ringAlpha by rememberInfiniteTransition(label = "ring").animateFloat(
                        0.1f, 0.3f,
                        infiniteRepeatable(tween(1200), RepeatMode.Reverse),
                        label = "ringA",
                    )
                    Box(
                        modifier = Modifier.size(100.dp).clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = ringAlpha))
                    )
                }
                val playInteraction = remember { MutableInteractionSource() }
                val isPressed by playInteraction.collectIsPressedAsState()
                val playScale by animateFloatAsState(
                    targetValue = if (isPressed) 0.9f else 1f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    label = "playScale",
                )
                FilledIconButton(
                    onClick = { PlayerController.togglePlayPause() },
                    modifier = Modifier.size(80.dp).scale(playScale),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    interactionSource = playInteraction
                ) {
                    Icon(if (isPlayingState) Icons.Rounded.Pause else Icons.Rounded.PlayArrow, contentDescription = "Play/Pause", modifier = Modifier.size(40.dp))
                }
            }

            FilledTonalIconButton(
                onClick = { PlayerController.playNext() },
                modifier = Modifier.size(64.dp),
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Icon(Icons.Rounded.SkipNext, contentDescription = "Next", modifier = Modifier.size(32.dp))
            }

            IconButton(
                onClick = { repeatMode = (repeatMode + 1) % 3 },
                modifier = Modifier.size(48.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = if (repeatMode == 0) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(if (repeatMode == 2) Icons.Rounded.RepeatOne else Icons.Rounded.Repeat, contentDescription = "Repeat", modifier = Modifier.size(24.dp))
            }
        }

        Spacer(Modifier.height(32.dp))

        // ── Utility row ───────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            UtilButton(Icons.Rounded.QueueMusic, "Queue")
            UtilButton(Icons.Rounded.DarkMode,   "Sleep")
            UtilButton(Icons.Rounded.Description,"Lyrics")
            UtilButton(Icons.Rounded.Equalizer,  "EQ")
            UtilButton(Icons.Rounded.Share,      "Share")
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun UtilButton(icon: ImageVector, label: String) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "utilScale",
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier
            .scale(scale)
            .clickable(interactionSource = interactionSource, indication = null) {},
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
            contentAlignment = Alignment.Center,
        ) { Icon(icon, null, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(24.dp)) }
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
    }
}

private fun fmtTime(ms: Long): String {
    val secs = ms / 1000
    return "%d:%02d".format(secs / 60, secs % 60)
}
