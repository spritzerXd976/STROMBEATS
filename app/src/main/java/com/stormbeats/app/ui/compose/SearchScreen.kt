package com.stormbeats.app.ui.compose

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stormbeats.app.data.model.Song
import com.stormbeats.app.ui.search.SearchViewModel
import com.stormbeats.app.util.PlayerController

@OptIn(ExperimentalMaterial3Api::class)
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
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding(),
    ) {
        Spacer(Modifier.height(12.dp))

        Text(
            text = "Search",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
        )

        // Search bar
        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                viewModel.search(it)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .focusRequester(focusRequester),
            placeholder = {
                Text(
                    "Songs, artists, albums…",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF555555),
                )
            },
            leadingIcon = {
                Icon(Icons.Rounded.Search, null, tint = Color(0xFF666666))
            },
            trailingIcon = {
                AnimatedVisibility(visible = query.isNotEmpty()) {
                    IconButton(onClick = { query = ""; viewModel.search("") }) {
                        Icon(Icons.Rounded.Close, null, tint = Color(0xFF666666))
                    }
                }
            },
            singleLine = true,
            shape = MaterialTheme.shapes.extraLarge,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor      = Color(0xFFFF0000),
                unfocusedBorderColor    = Color(0xFF2A2A2A),
                focusedContainerColor   = Color(0xFF181818),
                unfocusedContainerColor = Color(0xFF141414),
                cursorColor             = Color(0xFFFF0000),
                focusedTextColor        = Color.White,
                unfocusedTextColor      = Color.White,
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { keyboard?.hide() }),
        )

        Spacer(Modifier.height(8.dp))

        // Loading bar
        AnimatedVisibility(visible = isLoading) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(2.dp),
                color      = Color(0xFFFF0000),
                trackColor = Color(0xFF1A1A1A),
            )
        }

        AnimatedVisibility(visible = songs.isNotEmpty()) {
            Text(
                "${songs.size} results",
                style    = MaterialTheme.typography.labelSmall,
                color    = Color(0xFF555555),
                modifier = Modifier.padding(horizontal = 22.dp, vertical = 6.dp),
            )
        }

        when {
            query.isEmpty()           -> EmptySearchHint()
            isLoading && songs.isEmpty() -> {}
            songs.isEmpty()           -> NoResultsHint(query)
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 160.dp, top = 4.dp),
                ) {
                    items(songs, key = { it.id }) { song ->
                        SongItem(
                            song      = song,
                            isPlaying = currentSong?.id == song.id,
                            onClick   = {
                                PlayerController.playSong(song, songs)
                                onSongClick(song)
                            },
                        )
                        HorizontalDivider(
                            modifier  = Modifier.padding(horizontal = 20.dp),
                            thickness = 0.5.dp,
                            color     = Color(0xFF1C1C1C),
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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(Icons.Rounded.Search, null, modifier = Modifier.size(72.dp), tint = Color(0xFF2A2A2A))
            Text("Find your music", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = Color(0xFF444444))
            Text("Powered by JioSaavn", style = MaterialTheme.typography.bodySmall, color = Color(0xFF333333))
        }
    }
}

@Composable
private fun NoResultsHint(query: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(Icons.Rounded.SearchOff, null, modifier = Modifier.size(64.dp), tint = Color(0xFF2A2A2A))
            Text("No results for \"$query\"", style = MaterialTheme.typography.titleMedium, color = Color(0xFF555555))
        }
    }
}
