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
        Spacer(Modifier.height(8.dp))

        // Title
        Text(
            text = "Search",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
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
                .padding(horizontal = 16.dp)
                .focusRequester(focusRequester),
            placeholder = {
                Text(
                    "Songs, artists, albums…",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Rounded.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            trailingIcon = {
                AnimatedVisibility(visible = query.isNotEmpty()) {
                    IconButton(onClick = { query = ""; viewModel.search("") }) {
                        Icon(Icons.Rounded.Close, contentDescription = "Clear", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            },
            singleLine = true,
            shape = MaterialTheme.shapes.extraLarge,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor     = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor   = MaterialTheme.colorScheme.outline,
                focusedContainerColor  = MaterialTheme.colorScheme.surfaceContainerHigh,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                cursorColor            = MaterialTheme.colorScheme.primary,
                focusedTextColor       = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor     = MaterialTheme.colorScheme.onSurface,
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
                    .padding(horizontal = 16.dp)
                    .height(2.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            )
        }

        // Results count
        AnimatedVisibility(visible = songs.isNotEmpty()) {
            Text(
                "${songs.size} results",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
            )
        }

        when {
            songs.isEmpty() && query.isEmpty() -> EmptySearchHint()
            songs.isEmpty() && !isLoading      -> NoResultsHint(query)
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 160.dp, top = 4.dp),
                ) {
                    items(songs, key = { it.id }) { song ->
                        SongItem(
                            song = song,
                            isPlaying = currentSong?.id == song.id,
                            onClick = {
                                PlayerController.playSong(song, songs)
                                onSongClick(song)
                            },
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptySearchHint() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                Icons.Rounded.Search,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
            )
            Text(
                "Find your music",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
            )
            Text(
                "Powered by JioSaavn",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.25f),
            )
        }
    }
}

@Composable
private fun NoResultsHint(query: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                Icons.Rounded.SearchOff,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
            )
            Text(
                "No results for \"$query\"",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            )
        }
    }
}
