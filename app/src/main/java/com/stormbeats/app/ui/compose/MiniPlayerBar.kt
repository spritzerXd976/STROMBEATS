package com.stormbeats.app.ui.compose

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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

    // Shimmer sweep
    val shimmerX by infiniteTransition.animateFloat(
        -500f, 1000f,
        infiniteRepeatable(tween(2200, easing = LinearEasing)),
        label = "shimmerX",
    )
    // Breathing ring
    val breathe by infiniteTransition.animateFloat(
        0.5f, 1f,
        infiniteRepeatable(tween(1200), RepeatMode.Reverse),
        label = "breathe",
    )

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

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceCard)
            .border(
                1.dp,
                Brush.linearGradient(
                    if (isPlaying) listOf(VioletPrimary.copy(0.45f), PinkAccent.copy(0.35f))
                    else listOf(Color(0xFF2E2E4A), Color(0xFF1E1E32))
                ),
                RoundedCornerShape(20.dp),
            )
            .clickable(onClick = onExpandClick),
    ) {
        // Progress fill at bottom
        Box(
            modifier = Modifier.fillMaxWidth(progress).height(2.dp).align(Alignment.BottomStart)
                .background(Brush.horizontalGradient(listOf(VioletPrimary, PinkAccent)))
        )
        // Shimmer when playing
        if (isPlaying) {
            Box(
                modifier = Modifier.fillMaxWidth().height(1.dp).align(Alignment.TopCenter)
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color.Transparent, VioletPrimary.copy(0.7f), PinkAccent.copy(0.7f), Color.Transparent),
                            startX = shimmerX, endX = shimmerX + 500f,
                        )
                    )
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Album art with breathing ring when playing
            Box(modifier = Modifier.size(52.dp), contentAlignment = Alignment.Center) {
                if (isPlaying) {
                    Box(
                        modifier = Modifier.size((52 * (0.95f + breathe * 0.1f)).dp)
                            .clip(RoundedCornerShape((14 * (0.95f + breathe * 0.1f)).dp))
                            .background(Brush.linearGradient(listOf(VioletPrimary.copy(breathe * 0.3f), PinkAccent.copy(breathe * 0.25f))))
                    )
                }
                Box(
                    modifier = Modifier.size(50.dp).clip(RoundedCornerShape(13.dp))
                        .border(
                            1.dp,
                            Brush.linearGradient(
                                if (isPlaying) listOf(VioletPrimary.copy(0.6f), PinkAccent.copy(0.6f))
                                else listOf(Color(0xFF2E2E4A), Color(0xFF2E2E4A))
                            ),
                            RoundedCornerShape(13.dp),
                        ),
                ) {
                    val imageUrl = song.getImageUrl()
                    if (imageUrl.isNotEmpty()) {
                        AsyncImage(model = imageUrl, contentDescription = song.name, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    } else {
                        Box(
                            Modifier.fillMaxSize().background(Brush.linearGradient(listOf(Color(0xFF1A0A3A), Color(0xFF2D1566)))),
                            contentAlignment = Alignment.Center,
                        ) { Icon(Icons.Rounded.MusicNote, null, tint = VioletSoft, modifier = Modifier.size(22.dp)) }
                    }
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(song.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(song.getPrimaryArtist(), style = MaterialTheme.typography.bodySmall, color = Color(0xFF7777AA), maxLines = 1, overflow = TextOverflow.Ellipsis)
            }

            Spacer(Modifier.width(6.dp))

            // Controls
            Box(
                modifier = Modifier.size(34.dp).clip(CircleShape).background(SurfaceElevated).clickable { PlayerController.playPrevious() },
                contentAlignment = Alignment.Center,
            ) { Icon(Icons.Rounded.SkipPrevious, null, tint = Color(0xFFCCCCDD), modifier = Modifier.size(18.dp)) }

            Spacer(Modifier.width(8.dp))

            Box(
                modifier = Modifier.size(46.dp).clip(CircleShape)
                    .background(Brush.linearGradient(listOf(VioletPrimary, PinkAccent)))
                    .clickable { PlayerController.togglePlayPause() },
                contentAlignment = Alignment.Center,
            ) { Icon(if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow, null, tint = Color.White, modifier = Modifier.size(24.dp)) }

            Spacer(Modifier.width(8.dp))

            Box(
                modifier = Modifier.size(34.dp).clip(CircleShape).background(SurfaceElevated).clickable { PlayerController.playNext() },
                contentAlignment = Alignment.Center,
            ) { Icon(Icons.Rounded.SkipNext, null, tint = Color(0xFFCCCCDD), modifier = Modifier.size(18.dp)) }
        }
    }
}
