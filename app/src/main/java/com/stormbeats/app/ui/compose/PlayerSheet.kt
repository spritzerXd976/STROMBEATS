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
            .background(SurfaceDark),
    ) {
        // Base vertical gradient
        Box(
            modifier = Modifier.fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            VioletPrimary.copy(alpha = 0.12f),
                            SurfaceDark,
                            SurfaceDark,
                        )
                    )
                )
        )
        // Drifting aurora orb 1 — violet
        Box(
            modifier = Modifier
                .size(320.dp)
                .offset(x = orbX1.dp, y = orbY1.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(listOf(VioletPrimary.copy(alpha = 0.18f), Color.Transparent))
                )
        )
        // Drifting aurora orb 2 — pink
        Box(
            modifier = Modifier
                .size(260.dp)
                .align(Alignment.BottomEnd)
                .offset(x = orbX2.dp, y = orbY2.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(listOf(PinkAccent.copy(alpha = 0.14f), Color.Transparent))
                )
        )
        // Subtle cyan accent — top right
        Box(
            modifier = Modifier
                .size(180.dp)
                .align(Alignment.TopEnd)
                .offset(x = 60.dp, y = (-20).dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(listOf(CyanAccent.copy(alpha = 0.06f), Color.Transparent))
                )
        )

        PlayerContent(song = song, isPlaying = isPlaying, onDismiss = onDismiss)
    }
}

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
        // ── Top Bar — frosted glass ─────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(SurfaceGlass.copy(alpha = 0.8f))
                    .border(1.dp, GlassBorderLight.copy(alpha = 0.4f), CircleShape)
                    .clickable(onClick = onDismiss),
                contentAlignment = Alignment.Center,
            ) { Icon(Icons.Rounded.KeyboardArrowDown, null, tint = Color.White, modifier = Modifier.size(26.dp)) }

            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("NOW PLAYING", style = MaterialTheme.typography.labelSmall, color = Color(0xFF5858A0), letterSpacing = 3.sp, fontSize = 9.sp)
                song.album?.name?.takeIf { it.isNotEmpty() }?.let {
                    Text(it, style = MaterialTheme.typography.labelMedium, color = Color(0xFFCCCCDD), maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(SurfaceGlass.copy(alpha = 0.8f))
                    .border(1.dp, GlassBorderLight.copy(alpha = 0.4f), CircleShape)
                    .clickable {},
                contentAlignment = Alignment.Center,
            ) { Icon(Icons.Rounded.MoreVert, null, tint = Color.White, modifier = Modifier.size(20.dp)) }
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
                    VioletPrimary.copy(glowAlpha)
                else
                    PinkAccent.copy(glowAlpha)
                val glowColor2 = if (glowHue < 0.5f)
                    PinkAccent.copy(glowAlpha * 0.6f)
                else
                    CyanAccent.copy(glowAlpha * 0.5f)
                Box(
                    modifier = Modifier
                        .size((340 * artScale).dp)
                        .clip(RoundedCornerShape(34.dp))
                        .background(
                            Brush.radialGradient(listOf(glowColor1, glowColor2, Color.Transparent))
                        )
                )
            }
            Box(
                modifier = Modifier
                    .size((320 * artScale).dp)
                    .clip(RoundedCornerShape(30.dp))
                    .border(
                        width = if (isPlayingState) 2.dp else 1.dp,
                        brush = Brush.linearGradient(
                            if (isPlayingState)
                                listOf(VioletPrimary.copy(0.8f), PinkAccent.copy(0.7f), CyanAccent.copy(0.4f))
                            else
                                listOf(GlassBorderLight, GlassBorderDark)
                        ),
                        shape = RoundedCornerShape(30.dp),
                    )
                    .graphicsLayer {
                        if (isPlayingState) rotationZ = vinylAngle
                    },
                contentAlignment = Alignment.Center,
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
                            .background(Brush.linearGradient(listOf(Color(0xFF120828), Color(0xFF2D1566)))),
                        contentAlignment = Alignment.Center,
                    ) { Icon(Icons.Rounded.MusicNote, null, modifier = Modifier.size(80.dp), tint = VioletSoft) }
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
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                // Gradient accent line under title
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Brush.horizontalGradient(listOf(VioletPrimary, PinkAccent, Color.Transparent)))
                )
                Spacer(Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    if (song.explicitContent) {
                        Surface(shape = RoundedCornerShape(4.dp), color = SurfaceElevated) {
                            Text("E", style = MaterialTheme.typography.labelSmall, color = Color(0xFF6666AA), modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp))
                        }
                    }
                    Text(
                        song.getPrimaryArtist(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF8888BB),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    // Quality badge
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = GoldAccent.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, GoldAccent.copy(0.3f)),
                    ) {
                        Text(
                            "HiFi",
                            style = MaterialTheme.typography.labelSmall,
                            color = GoldAccent,
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
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .scale(likeScale)
                    .clip(CircleShape)
                    .background(
                        if (isLiked) Brush.linearGradient(listOf(VioletPrimary.copy(0.2f), PinkAccent.copy(0.18f)))
                        else Brush.linearGradient(listOf(SurfaceGlass, SurfaceGlass))
                    )
                    .border(
                        1.dp,
                        if (isLiked) Brush.linearGradient(listOf(VioletPrimary.copy(0.5f), PinkAccent.copy(0.5f)))
                        else Brush.linearGradient(listOf(GlassBorderLight, GlassBorderDark)),
                        CircleShape,
                    )
                    .clickable { isLiked = !isLiked },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    if (isLiked) Icons.Rounded.Favorite else Icons.Outlined.FavoriteBorder,
                    null,
                    tint = if (isLiked) PinkAccent else Color(0xFF5858A0),
                    modifier = Modifier.size(22.dp),
                )
            }
        }

        Spacer(Modifier.height(28.dp))

        // ── Seekbar — custom rounded track ──────────────────────────────────
        val progress = if (durationMs > 0) positionMs.toFloat() / durationMs.toFloat() else 0f
        Column(modifier = Modifier.padding(horizontal = 28.dp)) {
            // Custom seekbar canvas
            val seekHeight = 6.dp
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(seekHeight + 24.dp) // extra touch area
                    .padding(vertical = 12.dp),
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val trackHeight = seekHeight.toPx()
                    val y = (size.height - trackHeight) / 2f
                    // Inactive track
                    drawRoundRect(
                        color = Color(0xFF1A1A2E),
                        topLeft = Offset(0f, y),
                        size = Size(size.width, trackHeight),
                        cornerRadius = CornerRadius(trackHeight / 2f),
                    )
                    // Active track — gradient
                    val activeWidth = size.width * progress.coerceIn(0f, 1f)
                    if (activeWidth > 0f) {
                        drawRoundRect(
                            brush = Brush.horizontalGradient(
                                listOf(VioletPrimary, PinkAccent),
                                endX = size.width,
                            ),
                            topLeft = Offset(0f, y),
                            size = Size(activeWidth, trackHeight),
                            cornerRadius = CornerRadius(trackHeight / 2f),
                        )
                    }
                    // Thumb glow
                    if (activeWidth > 0f) {
                        drawCircle(
                            brush = Brush.radialGradient(
                                listOf(Color.White.copy(0.9f), VioletPrimary.copy(0.3f), Color.Transparent),
                            ),
                            radius = trackHeight * 1.8f,
                            center = Offset(activeWidth, y + trackHeight / 2f),
                        )
                        drawCircle(
                            color = Color.White,
                            radius = trackHeight * 0.9f,
                            center = Offset(activeWidth, y + trackHeight / 2f),
                        )
                    }
                }
                // Invisible slider for interaction
                Slider(
                    value = progress,
                    onValueChange = { v -> isUserSeeking = true; positionMs = (v * durationMs).toLong() },
                    onValueChangeFinished = { PlayerController.seekTo(positionMs); isUserSeeking = false },
                    modifier = Modifier.fillMaxWidth().matchParentSize(),
                    colors = SliderDefaults.colors(
                        thumbColor = Color.Transparent,
                        activeTrackColor = Color.Transparent,
                        inactiveTrackColor = Color.Transparent,
                        activeTickColor = Color.Transparent,
                        inactiveTickColor = Color.Transparent,
                    ),
                    thumb = {},
                )
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(fmtTime(positionMs), style = MaterialTheme.typography.labelSmall, color = Color(0xFF5858A0))
                Text(fmtTime(durationMs), style = MaterialTheme.typography.labelSmall, color = Color(0xFF5858A0))
            }
        }

        Spacer(Modifier.height(20.dp))

        // ── Controls ─────────────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PressableControlBtn(
                Icons.Rounded.Shuffle, size = 48, iconSize = 22,
                onClick = { isShuffleOn = !isShuffleOn },
                bg = if (isShuffleOn) Brush.linearGradient(listOf(VioletPrimary.copy(0.2f), PinkAccent.copy(0.15f)))
                     else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent)),
                tint = if (isShuffleOn) VioletSoft else Color(0xFF3E3E60),
            )
            PressableControlBtn(
                Icons.Rounded.SkipPrevious, size = 60, iconSize = 32,
                onClick = { PlayerController.playPrevious() },
                bg = Brush.linearGradient(listOf(SurfaceGlass, SurfaceGlass)),
                tint = Color.White,
                borderBrush = Brush.linearGradient(listOf(GlassBorderLight.copy(0.5f), GlassBorderDark.copy(0.3f))),
            )
            // Large play button with double-ring glow
            Box(contentAlignment = Alignment.Center) {
                if (isPlayingState) {
                    val ringAlpha by rememberInfiniteTransition(label = "ring").animateFloat(
                        0.1f, 0.3f,
                        infiniteRepeatable(tween(1200), RepeatMode.Reverse),
                        label = "ringA",
                    )
                    Box(
                        modifier = Modifier.size(92.dp).clip(CircleShape)
                            .background(Brush.radialGradient(listOf(VioletPrimary.copy(ringAlpha), Color.Transparent)))
                    )
                }
                val playInteraction = remember { MutableInteractionSource() }
                val isPressed by playInteraction.collectIsPressedAsState()
                val playScale by animateFloatAsState(
                    targetValue = if (isPressed) 0.9f else 1f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    label = "playScale",
                )
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .scale(playScale)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(VioletPrimary, PinkAccent)))
                        .border(2.dp, Brush.linearGradient(listOf(VioletSoft.copy(0.5f), PinkSoft.copy(0.4f))), CircleShape)
                        .clickable(interactionSource = playInteraction, indication = null) { PlayerController.togglePlayPause() },
                    contentAlignment = Alignment.Center,
                ) { Icon(if (isPlayingState) Icons.Rounded.Pause else Icons.Rounded.PlayArrow, null, tint = Color.White, modifier = Modifier.size(40.dp)) }
            }
            PressableControlBtn(
                Icons.Rounded.SkipNext, size = 60, iconSize = 32,
                onClick = { PlayerController.playNext() },
                bg = Brush.linearGradient(listOf(SurfaceGlass, SurfaceGlass)),
                tint = Color.White,
                borderBrush = Brush.linearGradient(listOf(GlassBorderLight.copy(0.5f), GlassBorderDark.copy(0.3f))),
            )
            PressableControlBtn(
                icon = if (repeatMode == 2) Icons.Rounded.RepeatOne else Icons.Rounded.Repeat,
                size = 48, iconSize = 22,
                onClick = { repeatMode = (repeatMode + 1) % 3 },
                bg = if (repeatMode != 0) Brush.linearGradient(listOf(CyanAccent.copy(0.15f), CyanAccent.copy(0.08f)))
                     else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent)),
                tint = if (repeatMode == 0) Color(0xFF3E3E60) else CyanAccent,
            )
        }

        Spacer(Modifier.height(32.dp))

        // ── Utility row — glass pills ───────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            GlassUtilButton(Icons.Rounded.QueueMusic, "Queue",  VioletSoft)
            GlassUtilButton(Icons.Rounded.DarkMode,   "Sleep",  Color(0xFF60A5FA))
            GlassUtilButton(Icons.Rounded.Description,"Lyrics", PinkSoft)
            GlassUtilButton(Icons.Rounded.Equalizer,  "EQ",     CyanAccent)
            GlassUtilButton(Icons.Rounded.Share,      "Share",  Color(0xFF8888BB))
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun PressableControlBtn(
    icon: ImageVector,
    size: Int,
    iconSize: Int,
    onClick: () -> Unit,
    bg: Brush,
    tint: Color,
    borderBrush: Brush? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "ctrlScale",
    )
    Box(
        modifier = Modifier
            .size(size.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(bg)
            .then(
                if (borderBrush != null) Modifier.border(1.dp, borderBrush, CircleShape)
                else Modifier
            )
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) { Icon(icon, null, tint = tint, modifier = Modifier.size(iconSize.dp)) }
}

@Composable
private fun GlassUtilButton(icon: ImageVector, label: String, tint: Color) {
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
                .background(SurfaceGlass.copy(alpha = 0.85f))
                .border(
                    1.dp,
                    Brush.linearGradient(listOf(GlassBorderLight.copy(0.5f), GlassBorderDark.copy(0.2f))),
                    RoundedCornerShape(16.dp),
                ),
            contentAlignment = Alignment.Center,
        ) { Icon(icon, null, tint = tint, modifier = Modifier.size(20.dp)) }
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color(0xFF5050A0), fontSize = 10.sp)
    }
}

private fun fmtTime(ms: Long): String {
    val secs = ms / 1000
    return "%d:%02d".format(secs / 60, secs % 60)
}
