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
import com.stormbeats.app.ui.theme.*
import com.stormbeats.app.util.PlayerController

@Composable
fun SearchScreen(
    onSongClick: (Song) -> Unit,
    vm: SearchViewModel = viewModel(),
) {
    val songs   by vm.songs.collectAsState()
    val loading by vm.isLoading.collectAsState()
    val current by PlayerController.currentSong.collectAsState()
    var query   by remember { mutableStateOf("") }
    val focusReq = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current

    Column(modifier = Modifier.fillMaxSize().background(Bg).statusBarsPadding()) {
        Spacer(Modifier.height(18.dp))
        Text("Search", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = OnBg, modifier = Modifier.padding(horizontal = 20.dp))
        Spacer(Modifier.height(12.dp))

        // Search field
        OutlinedTextField(
            value = query,
            onValueChange = { query = it; vm.search(it) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).focusRequester(focusReq),
            placeholder = { Text("Songs, artists, albums…", color = OnBgTer) },
            leadingIcon  = { Icon(Icons.Rounded.Search, null, tint = OnBgSec) },
            trailingIcon = {
                AnimatedVisibility(query.isNotEmpty()) {
                    IconButton(onClick = { query = ""; vm.search("") }) {
                        Icon(Icons.Rounded.Close, null, tint = OnBgSec)
                    }
                }
            },
            singleLine = true,
            shape = MaterialTheme.shapes.extraLarge,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor      = Purple,
                unfocusedBorderColor    = Surface3,
                focusedContainerColor   = Surface0,
                unfocusedContainerColor = Surface0,
                cursorColor             = Purple,
                focusedTextColor        = OnBg,
                unfocusedTextColor      = OnBg,
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { keyboard?.hide() }),
        )
        Spacer(Modifier.height(8.dp))

        AnimatedVisibility(loading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).height(2.dp), color = Purple, trackColor = Surface2)
        }
        AnimatedVisibility(songs.isNotEmpty()) {
            Text("${songs.size} results", style = MaterialTheme.typography.labelSmall, color = OnBgTer, modifier = Modifier.padding(horizontal = 22.dp, vertical = 4.dp))
        }

        when {
            query.isEmpty()             -> SearchHint()
            loading && songs.isEmpty()  -> {}
            songs.isEmpty()             -> NoResults(query)
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 160.dp, top = 4.dp),
                ) {
                    items(songs, key = { it.id }) { song ->
                        SongItem(
                            song = song,
                            isPlaying = current?.id == song.id,
                            onClick   = { PlayerController.playSong(song, songs); onSongClick(song) },
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp), thickness = 0.5.dp, color = Surface2)
                    }
                }
            }
        }
    }
}

@Composable private fun SearchHint() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(Icons.Rounded.Search, null, modifier = Modifier.size(68.dp), tint = Surface3)
            Text("Find your music", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = OnBgTer)
            Text("Powered by JioSaavn", style = MaterialTheme.typography.bodySmall, color = Surface3)
        }
    }
}

@Composable private fun NoResults(q: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Rounded.SearchOff, null, modifier = Modifier.size(64.dp), tint = Surface3)
            Text("No results for \"$q\"", style = MaterialTheme.typography.titleMedium, color = OnBgTer)
        }
    }
}
