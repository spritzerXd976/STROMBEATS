package com.stormbeats.app.ui.compose

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
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
import com.stormbeats.app.util.PlayerController

@Composable
fun MiniPlayerBar(
    song: Song,
    isPlaying: Boolean,
    onExpandClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerX by infiniteTransition.animateFloat(
        initialValue = -400f, targetValue = 800f,
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing)),
        label = "shimmerX",
    )

    // Progress for seekbar in mini player
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
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF161616))
            .clickable(onClick = onExpandClick),
    ) {
        // Progress bar at bottom
        Box(
            modifier = Modifier
                .fillMaxWidth(progress)
                .height(2.dp)
                .align(Alignment.BottomStart)
                .background(Color(0xFFFF0000))
        )

        // Shimmer when playing
        if (isPlaying) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .align(Alignment.TopCenter)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color.Transparent, Color(0x99FF0000), Color.Transparent),
                            startX = shimmerX,
                            endX   = shimmerX + 400f,
                        )
                    )
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Album art with pulse ring when playing
            Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                if (isPlaying) {
                    val pulse by infiniteTransition.animateFloat(
                        1f, 1.15f,
                        infiniteRepeatable(tween(800), RepeatMode.Reverse),
                        label = "artPulse",
                    )
                    Box(
                        Modifier
                            .size((48 * pulse).dp)
                            .clip(RoundedCornerShape((12 * pulse).dp))
                            .background(Color(0x33FF0000))
                    )
                }
                Box(modifier = Modifier.size(46.dp).clip(RoundedCornerShape(11.dp))) {
                    val imageUrl = song.getImageUrl()
                    if (imageUrl.isNotEmpty()) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = song.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                        )
                    } else {
                        Box(
                            Modifier.fillMaxSize().background(Color(0xFF2A0000)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Rounded.MusicNote, null, tint = Color(0xFFFF0000))
                        }
                    }
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    song.name,
                    style      = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color      = Color.White,
                    maxLines   = 1,
                    overflow   = TextOverflow.Ellipsis,
                )
                Text(
                    song.getPrimaryArtist(),
                    style    = MaterialTheme.typography.bodySmall,
                    color    = Color(0xFF888888),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Spacer(Modifier.width(4.dp))

            IconButton(
                onClick = { PlayerController.playPrevious() },
                modifier = Modifier.size(36.dp),
            ) {
                Icon(Icons.Rounded.SkipPrevious, null, tint = Color(0xFFCCCCCC), modifier = Modifier.size(22.dp))
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFF0000))
                    .clickable { PlayerController.togglePlayPause() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    null, tint = Color.White, modifier = Modifier.size(22.dp),
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
}
