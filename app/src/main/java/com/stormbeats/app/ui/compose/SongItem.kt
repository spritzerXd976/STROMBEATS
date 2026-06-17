package com.stormbeats.app.ui.compose

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.stormbeats.app.data.model.Song
import com.stormbeats.app.ui.theme.*

@Composable
fun SongItem(
    song: Song,
    isPlaying: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bgColor by animateColorAsState(
        targetValue = if (isPlaying) VioletPrimary.copy(alpha = 0.06f) else Color.Transparent,
        animationSpec = tween(300),
        label = "songBg",
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Active gradient bar on the left
        if (isPlaying) {
            val infiniteTransition = rememberInfiniteTransition(label = "activeBar")
            val barAlpha by infiniteTransition.animateFloat(
                0.5f, 1f,
                infiniteRepeatable(tween(800), RepeatMode.Reverse),
                label = "barA",
            )
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(44.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(VioletPrimary.copy(barAlpha), PinkAccent.copy(barAlpha * 0.8f))
                        )
                    )
            )
            Spacer(Modifier.width(10.dp))
        }

        // Album art
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(14.dp))
                .then(
                    if (isPlaying) Modifier.border(
                        1.dp,
                        Brush.linearGradient(listOf(VioletPrimary.copy(0.5f), PinkAccent.copy(0.4f))),
                        RoundedCornerShape(14.dp),
                    ) else Modifier
                ),
            contentAlignment = Alignment.Center,
        ) {
            val imageUrl = song.getImageUrl()
            if (imageUrl.isNotEmpty()) {
                AsyncImage(model = imageUrl, contentDescription = song.name, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            } else {
                Box(
                    Modifier.fillMaxSize().background(Color(0xFF0E0E1A)),
                    contentAlignment = Alignment.Center,
                ) { Icon(Icons.Rounded.MusicNote, null, tint = Color(0xFF3E3E60), modifier = Modifier.size(24.dp)) }
            }

            // Playing overlay with equalizer
            if (isPlaying) {
                val infiniteTransition = rememberInfiniteTransition(label = "eq")
                val b1 by infiniteTransition.animateFloat(0.3f, 1f, infiniteRepeatable(tween(380), RepeatMode.Reverse), label = "sb1")
                val b2 by infiniteTransition.animateFloat(0.5f, 0.8f, infiniteRepeatable(tween(500), RepeatMode.Reverse), label = "sb2")
                val b3 by infiniteTransition.animateFloat(0.2f, 0.9f, infiniteRepeatable(tween(420), RepeatMode.Reverse), label = "sb3")
                Box(
                    Modifier.fillMaxSize().background(SurfaceDark.copy(alpha = 0.55f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalAlignment = Alignment.Bottom,
                        modifier = Modifier.height(16.dp),
                    ) {
                        listOf(b1, b2, b3).forEach { h ->
                            Box(
                                modifier = Modifier
                                    .width(3.dp)
                                    .fillMaxHeight(h)
                                    .clip(RoundedCornerShape(1.dp))
                                    .background(Brush.verticalGradient(listOf(PinkAccent, VioletPrimary)))
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = song.name,
                style = MaterialTheme.typography.titleSmall.copy(
                    brush = if (isPlaying) Brush.horizontalGradient(listOf(VioletSoft, PinkSoft)) else null
                ),
                fontWeight = if (isPlaying) FontWeight.Bold else FontWeight.Medium,
                color = if (isPlaying) Color.Unspecified else Color.White,
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                if (song.explicitContent) {
                    Surface(shape = RoundedCornerShape(3.dp), color = SurfaceElevated) {
                        Text("E", style = MaterialTheme.typography.labelSmall, color = Color(0xFF5858A0), modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                    }
                }
                Text(song.getPrimaryArtist(), style = MaterialTheme.typography.bodySmall, color = Color(0xFF6666A0), maxLines = 1, overflow = TextOverflow.Ellipsis)
                song.album?.name?.takeIf { it.isNotEmpty() }?.let { albumName ->
                    Text("·", style = MaterialTheme.typography.bodySmall, color = Color(0xFF2E2E4A))
                    Text(albumName, style = MaterialTheme.typography.bodySmall, color = Color(0xFF4A4A70), maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }

        Spacer(Modifier.width(8.dp))

        // Duration with music note prefix
        song.duration?.let { dur ->
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                Icon(Icons.Rounded.MusicNote, null, tint = Color(0xFF2E2E4A), modifier = Modifier.size(10.dp))
                Text("%d:%02d".format(dur / 60, dur % 60), style = MaterialTheme.typography.labelSmall, color = Color(0xFF3E3E5A))
            }
            Spacer(Modifier.width(4.dp))
        }

        // Glass more button
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(SurfaceGlass.copy(alpha = 0.5f))
                .border(0.5.dp, GlassBorderLight.copy(0.2f), CircleShape)
                .clickable {},
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Rounded.MoreVert, null, tint = Color(0xFF3E3E5A), modifier = Modifier.size(16.dp))
        }
    }
}
