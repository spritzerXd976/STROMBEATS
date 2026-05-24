package com.stormbeats.app.ui.compose

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.stormbeats.app.util.PlayerController
import com.stormbeats.app.util.UpdateManager
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    onShowPlayer: () -> Unit,
) {
    val currentSong by PlayerController.currentSong.collectAsState()
    val isPlaying by PlayerController.isPlaying.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showUpdateDialog by remember { mutableStateOf(false) }
    var updateResult by remember { mutableStateOf<UpdateManager.UpdateResult.UpdateAvailable?>(null) }

    LaunchedEffect(Unit) {
        scope.launch {
            val result = UpdateManager.checkForUpdate(context)
            if (result is UpdateManager.UpdateResult.UpdateAvailable) {
                updateResult = result
                showUpdateDialog = true
            }
        }
    }

    if (showUpdateDialog && updateResult != null) {
        AlertDialog(
            onDismissRequest = { showUpdateDialog = false },
            icon = { Icon(Icons.Rounded.SystemUpdate, contentDescription = null) },
            title = { Text("New Update Available!") },
            text = {
                Text(
                    "Version ${updateResult!!.release.tagName} is ready.\n\n${updateResult!!.release.body}",
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            confirmButton = {
                Button(onClick = {
                    UpdateManager.downloadAndInstall(
                        context,
                        updateResult!!.downloadUrl,
                        updateResult!!.release.tagName,
                    )
                    showUpdateDialog = false
                }) {
                    Text("Update Now")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUpdateDialog = false }) {
                    Text("Later")
                }
            },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
    ) {
        // App header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "⚡ StormBeats",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = "JioSaavn Music",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 20.dp),
            color = MaterialTheme.colorScheme.outlineVariant,
        )

        Spacer(modifier = Modifier.weight(1f))

        // Empty state when nothing playing
        AnimatedVisibility(
            visible = currentSong == null,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    imageVector = Icons.Rounded.LibraryMusic,
                    contentDescription = null,
                    modifier = Modifier.size(96.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.15f),
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No music playing",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                )
                Text(
                    text = "Search for songs using the tab below",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.25f),
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Mini player bar
        AnimatedVisibility(
            visible = currentSong != null,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
        ) {
            currentSong?.let { song ->
                MiniPlayerBar(
                    song = song,
                    isPlaying = isPlaying,
                    onExpandClick = onShowPlayer,
                    modifier = Modifier.padding(bottom = 4.dp),
                )
            }
        }
    }
}
