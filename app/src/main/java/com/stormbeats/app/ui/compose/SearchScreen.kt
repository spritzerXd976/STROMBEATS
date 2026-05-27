package com.stormbeats.app.ui.compose

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.stormbeats.app.ui.theme.Purple500
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
    val focus       = remember { FocusRequester() }
    val keyboard    = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding(),
    ) {
        Spacer(Modifier.height(16.dp))
        Text(
            "Search",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 20.dp),
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = query,
            onValueChange = { query = it; viewModel.search(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .focusRequester(focus),
            placeholder = { Text("Songs, artists, albums…", color = MaterialTheme.colorScheme.onSurfaceVariant) },
            leadingIcon  = { Icon(Icons.Rounded.Search, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
            trailingIcon = {
                AnimatedVisibility(visible = query.isNotEmpty()) {
                    IconButton(onClick = { query = ""; viewModel.search("") }) {
                        Icon(Icons.Rounded.Close, null)
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor      = Purple500,
                unfocusedBorderColor    = MaterialTheme.colorScheme.outline,
                focusedContainerColor   = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                cursorColor             = Purple500,
                focusedTextColor        = MaterialTheme.colorScheme.onBackground,
                unfocusedTextColor      = MaterialTheme.colorScheme.onBackground,
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { keyboard?.hide() }),
        )

        Spacer(Modifier.height(8.dp))

        AnimatedVisibility(visible = isLoading) {
            LinearProgressIndicator(
                modifier   = Modifier.fillMaxWidth().padding(horizontal = 20.dp).height(2.dp),
                color      = Purple500,
                trackColor = MaterialTheme.colorScheme.outline,
            )
        }

        AnimatedVisibility(visible = songs.isNotEmpty()) {
            Text(
                "${songs.size} results",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 22.dp, vertical = 6.dp),
            )
        }

        when {
            query.isEmpty()                -> SearchEmptyHint()
            isLoading && songs.isEmpty()   -> { /* loading indicator shown */ }
            songs.isEmpty()                -> SearchNoResults(query)
            else -> {
                LazyColumn(contentPadding = PaddingValues(bottom = 160.dp, top = 4.dp)) {
                    items(songs, key = { it.id }) { song ->
                        SongItem(
                            song      = song,
                            isPlaying = currentSong?.id == song.id,
                            onClick   = {
                                PlayerController.playSong(song, songs)
                                onSongClick(song)
                            },
                        )
                        HorizontalDivider(Modifier.padding(horizontal = 20.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchEmptyHint() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Rounded.Search, null, modifier = Modifier.size(72.dp), tint = MaterialTheme.colorScheme.outline)
            Text("Find your music", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Powered by JioSaavn", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun SearchNoResults(query: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Rounded.SearchOff, null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
            Text("No results for \"$query\"", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
