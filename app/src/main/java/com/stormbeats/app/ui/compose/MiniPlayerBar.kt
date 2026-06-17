package com.stormbeats.app.ui.compose

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.stormbeats.app.data.model.Song
import com.stormbeats.app.ui.theme.*
import com.stormbeats.app.util.PlayerController

@Composable
fun MiniPlayerBar(
    song: Song,
    isPlaying: Boolean,
    onExpandClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "mini")

    // Animated border gradient hue shift
    val borderShift by infiniteTransition.animateFloat(
        0f, 1f,
        infiniteRepeatable(tween(3000, easing = LinearEasing)),
        label = "borderShift",
    )

    // Equalizer bar heights (4 bars)
    val bar1 by infiniteTransition.animateFloat(0.3f, 1f, infiniteRepeatable(tween(400), RepeatMode.Reverse), label = "b1")
    val bar2 by infiniteTransition.animateFloat(0.5f, 0.8f, infiniteRepeatable(tween(550), RepeatMode.Reverse), label = "b2")
    val bar3 by infiniteTransition.animateFloat(0.2f, 0.9f, infiniteRepeatable(tween(350), RepeatMode.Reverse), label = "b3")
    val bar4 by infiniteTransition.animateFloat(0.4f, 1f, infiniteRepeatable(tween(480), RepeatMode.Reverse), label = "b4")

    var positionMs by remember { mutableLongStateOf(0L) }
    var durationMs by remember { mutableLongStateOf(1L) }

    LaunchedEffect(song) {
        while (true) {
            positionMs = PlayerController.getCurrentPosition()
            durationMs = PlayerController.getDuration().coerceAtLeast(1L)
            kotlinx.coroutines.delay(500)
        }
    }

    val progress = (positionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)

    // Border colors shift when playing
    val borderColors = if (isPlaying) {
        val c1 = if (borderShift < 0.5f) VioletPrimary.copy(0.5f) else PinkAccent.copy(0.5f)
        val c2 = if (borderShift < 0.5f) PinkAccent.copy(0.4f) else CyanAccent.copy(0.3f)
        listOf(c1, c2)
    } else {
        listOf(GlassBorderLight, GlassBorderDark)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(SurfaceGlass.copy(alpha = 0.9f))
            .border(1.dp, Brush.linearGradient(borderColors), RoundedCornerShape(22.dp))
            .clickable(onClick = onExpandClick),
    ) {
        // Progress at the TOP — thin gradient line
        Box(
            modifier = Modifier
                .fillMaxWidth(progress)
                .height(2.dp)
                .align(Alignment.TopStart)
                .background(Brush.horizontalGradient(listOf(VioletPrimary, PinkAccent)))
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Album art with equalizer bars overlay
            Box(modifier = Modifier.size(52.dp), contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .border(
                            1.dp,
                            Brush.linearGradient(
                                if (isPlaying) listOf(VioletPrimary.copy(0.6f), PinkAccent.copy(0.5f))
                                else listOf(GlassBorderLight, GlassBorderLight)
                            ),
                            RoundedCornerShape(14.dp),
                        ),
                ) {
                    val imageUrl = song.getImageUrl()
                    if (imageUrl.isNotEmpty()) {
                        AsyncImage(model = imageUrl, contentDescription = song.name, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    } else {
                        Box(
                            Modifier.fillMaxSize().background(Brush.linearGradient(listOf(Color(0xFF120828), Color(0xFF2D1566)))),
                            contentAlignment = Alignment.Center,
                        ) { Icon(Icons.Rounded.MusicNote, null, tint = VioletSoft, modifier = Modifier.size(22.dp)) }
                    }

                    // Equalizer bars overlay when playing
                    if (isPlaying) {
                        Box(
                            modifier = Modifier.fillMaxSize().background(SurfaceDark.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(3.dp),
                                verticalAlignment = Alignment.Bottom,
                                modifier = Modifier.height(20.dp),
                            ) {
                                val bars = listOf(bar1, bar2, bar3, bar4)
                                bars.forEach { h ->
                                    Box(
                                        modifier = Modifier
                                            .width(3.dp)
                                            .fillMaxHeight(h)
                                            .clip(RoundedCornerShape(1.5.dp))
                                            .background(
                                                Brush.verticalGradient(listOf(PinkAccent, VioletPrimary))
                                            )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    song.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    song.getPrimaryArtist(),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF6666A0),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Spacer(Modifier.width(6.dp))

            // Controls
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(SurfaceElevated)
                    .border(0.5.dp, GlassBorderLight.copy(0.3f), CircleShape)
                    .clickable { PlayerController.playPrevious() },
                contentAlignment = Alignment.Center,
            ) { Icon(Icons.Rounded.SkipPrevious, null, tint = Color(0xFFCCCCDD), modifier = Modifier.size(18.dp)) }

            Spacer(Modifier.width(8.dp))

            // Play button with press scale
            val playInteraction = remember { MutableInteractionSource() }
            val playPressed by playInteraction.collectIsPressedAsState()
            val playScale by animateFloatAsState(
                targetValue = if (playPressed) 0.88f else 1f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label = "miniPlayScale",
            )
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .scale(playScale)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(VioletPrimary, PinkAccent)))
                    .border(1.dp, Brush.linearGradient(listOf(VioletSoft.copy(0.3f), PinkSoft.copy(0.3f))), CircleShape)
                    .clickable(interactionSource = playInteraction, indication = null) { PlayerController.togglePlayPause() },
                contentAlignment = Alignment.Center,
            ) { Icon(if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow, null, tint = Color.White, modifier = Modifier.size(24.dp)) }

            Spacer(Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(SurfaceElevated)
                    .border(0.5.dp, GlassBorderLight.copy(0.3f), CircleShape)
                    .clickable { PlayerController.playNext() },
                contentAlignment = Alignment.Center,
            ) { Icon(Icons.Rounded.SkipNext, null, tint = Color(0xFFCCCCDD), modifier = Modifier.size(18.dp)) }
        }

        // Expand hint chevron at bottom center
        Icon(
            Icons.Rounded.KeyboardArrowUp,
            contentDescription = "Expand",
            tint = Color(0xFF3E3E60),
            modifier = Modifier
                .size(16.dp)
                .align(Alignment.BottomCenter)
                .offset(y = (-2).dp),
        )
    }
}
