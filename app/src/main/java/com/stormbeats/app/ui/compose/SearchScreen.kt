package com.stormbeats.app.ui.compose

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stormbeats.app.data.model.Song
import com.stormbeats.app.ui.search.SearchViewModel
import com.stormbeats.app.ui.theme.*
import com.stormbeats.app.util.PlayerController

@Composable
fun SearchScreen(
    onSongClick: (Song) -> Unit,
    viewModel: SearchViewModel = viewModel(),
) {
    val songs       by viewModel.songs.collectAsState()
    val isLoading   by viewModel.isLoading.collectAsState()
    val currentSong by PlayerController.currentSong.collectAsState()
    var query       by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val keyboard       = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceDark)
            .statusBarsPadding(),
    ) {
        Spacer(Modifier.height(12.dp))

        // Gradient section header
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(22.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Brush.verticalGradient(listOf(VioletPrimary, PinkAccent)))
            )
            Spacer(Modifier.width(10.dp))
            Text(
                "Search",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
        }

        // Search bar — glass morphism
        OutlinedTextField(
            value = query,
            onValueChange = { query = it; viewModel.search(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .focusRequester(focusRequester),
            placeholder = {
                Text("Songs, artists, albums…", style = MaterialTheme.typography.bodyLarge, color = Color(0xFF3E3E5E))
            },
            leadingIcon = {
                Icon(Icons.Rounded.Search, null, tint = Color(0xFF6666A0))
            },
            trailingIcon = {
                AnimatedVisibility(visible = query.isNotEmpty()) {
                    IconButton(onClick = { query = ""; viewModel.search("") }) {
                        Icon(Icons.Rounded.Close, null, tint = Color(0xFF6666A0))
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(20.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor      = VioletPrimary,
                unfocusedBorderColor    = GlassBorderLight,
                focusedContainerColor   = SurfaceGlass.copy(alpha = 0.85f),
                unfocusedContainerColor = SurfaceGlass.copy(alpha = 0.7f),
                cursorColor             = VioletPrimary,
                focusedTextColor        = Color.White,
                unfocusedTextColor      = Color.White,
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { keyboard?.hide() }),
        )

        Spacer(Modifier.height(8.dp))

        // Loading — animated gradient shimmer
        AnimatedVisibility(visible = isLoading) {
            val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
            val shimmerX by infiniteTransition.animateFloat(
                -500f, 1500f,
                infiniteRepeatable(tween(1500, easing = LinearEasing)),
                label = "shimmerX",
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(SurfaceElevated)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color.Transparent, VioletPrimary.copy(0.7f), PinkAccent.copy(0.6f), Color.Transparent),
                                startX = shimmerX, endX = shimmerX + 600f,
                            )
                        )
                )
            }
        }

        // Results count as glass pill badge
        AnimatedVisibility(visible = songs.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 22.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceGlass.copy(alpha = 0.6f))
                    .border(0.5.dp, GlassBorderLight.copy(0.3f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            ) {
                Text(
                    "${songs.size} results",
                    style = MaterialTheme.typography.labelSmall,
                    brush = Brush.horizontalGradient(listOf(VioletSoft, PinkSoft)),
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        when {
            query.isEmpty()              -> EmptySearchHint()
            isLoading && songs.isEmpty() -> {}
            songs.isEmpty()              -> NoResultsHint(query)
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 160.dp, top = 4.dp),
                ) {
                    items(songs, key = { it.id }) { song ->
                        SongItem(
                            song = song,
                            isPlaying = currentSong?.id == song.id,
                            onClick = { PlayerController.playSong(song, songs); onSongClick(song) },
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 20.dp),
                            thickness = 0.5.dp,
                            color = GlassBorderDark,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptySearchHint() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Pulsing rings behind search icon
            Box(contentAlignment = Alignment.Center) {
                val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                val ring1 by infiniteTransition.animateFloat(0.6f, 1f, infiniteRepeatable(tween(1500), RepeatMode.Reverse), label = "r1")
                val ring2 by infiniteTransition.animateFloat(0.4f, 0.8f, infiniteRepeatable(tween(2000), RepeatMode.Reverse), label = "r2")
                Box(
                    modifier = Modifier
                        .size((110 * ring2).dp)
                        .clip(CircleShape)
                        .background(VioletPrimary.copy(alpha = 0.04f))
                )
                Box(
                    modifier = Modifier
                        .size((80 * ring1).dp)
                        .clip(CircleShape)
                        .background(VioletPrimary.copy(alpha = 0.08f))
                )
                Icon(Icons.Rounded.Search, null, modifier = Modifier.size(52.dp), tint = Color(0xFF2A2A48))
            }
            Text("Find your music", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = Color(0xFF3E3E5E))

            // Trending suggestion chips
            Text("TRY", style = MaterialTheme.typography.labelSmall, color = Color(0xFF3E3E5E), letterSpacing = 2.sp, fontSize = 9.sp)
            LazyRow(
                contentPadding = PaddingValues(horizontal = 40.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                val suggestions = listOf("Arijit Singh", "Bollywood Hits", "Lofi", "Trending", "Romantic")
                items(suggestions) { chip ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceGlass.copy(alpha = 0.6f))
                            .border(0.5.dp, GlassBorderLight.copy(0.3f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                    ) {
                        Text(chip, style = MaterialTheme.typography.labelMedium, color = Color(0xFF6666A0))
                    }
                }
            }
            Spacer(Modifier.height(4.dp))
            Text("Powered by JioSaavn", style = MaterialTheme.typography.bodySmall, color = Color(0xFF2A2A40), fontSize = 10.sp)
        }
    }
}

@Composable
private fun NoResultsHint(query: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(SurfaceGlass.copy(alpha = 0.5f))
                    .border(1.dp, GlassBorderLight.copy(0.2f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Rounded.SearchOff, null, modifier = Modifier.size(40.dp), tint = Color(0xFF2A2A48))
            }
            Spacer(Modifier.height(4.dp))
            Text("No results for \"$query\"", style = MaterialTheme.typography.titleMedium, color = Color(0xFF4A4A70))
            Text("Try different keywords", style = MaterialTheme.typography.bodySmall, color = Color(0xFF3A3A58))
        }
    }
}
