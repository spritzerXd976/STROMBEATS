package com.stormbeats.app.ui.compose

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.stormbeats.app.ui.home.HomeViewModel
import com.stormbeats.app.util.PlayerController

private data class NavItem(val label: String, val selected: ImageVector, val unselected: ImageVector)

private val navItems = listOf(
    NavItem("Home",    Icons.Filled.Home,       Icons.Outlined.Home),
    NavItem("Search",  Icons.Rounded.Search,    Icons.Rounded.Search),
    NavItem("Library", Icons.Rounded.LibraryMusic, Icons.Rounded.LibraryMusic),
)

@Composable
fun MainScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showPlayer  by remember { mutableStateOf(false) }
    val currentSong by PlayerController.currentSong.collectAsState()
    val isPlaying   by PlayerController.isPlaying.collectAsState()
    val homeVm      = androidx.lifecycle.viewmodel.compose.viewModel<HomeViewModel>()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                AnimatedVisibility(
                    visible = currentSong != null,
                    enter = slideInVertically { it } + fadeIn(tween(250)),
                    exit  = slideOutVertically { it } + fadeOut(tween(200)),
                ) {
                    currentSong?.let { song ->
                        MiniPlayerBar(
                            song          = song,
                            isPlaying     = isPlaying,
                            onExpandClick = { showPlayer = true },
                        )
                    }
                }
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp,
                ) {
                    navItems.forEachIndexed { idx, item ->
                        NavigationBarItem(
                            selected = selectedTab == idx,
                            onClick  = { selectedTab = idx },
                            icon = {
                                Icon(
                                    if (selectedTab == idx) item.selected else item.unselected,
                                    contentDescription = item.label,
                                )
                            },
                            label = { Text(item.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor   = MaterialTheme.colorScheme.primary,
                                selectedTextColor   = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor      = MaterialTheme.colorScheme.primaryContainer,
                            ),
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(150)) },
                label = "tabTransition",
            ) { tab ->
                when (tab) {
                    0 -> HomeScreen(
                        viewModel     = homeVm,
                        onShowPlayer  = { showPlayer = true },
                        onSongClick   = { song ->
                            PlayerController.playSong(song, homeVm.featuredSongs.value + homeVm.recentSongs.value)
                            homeVm.addToRecent(song)
                            showPlayer = true
                        },
                    )
                    1 -> SearchScreen(
                        onSongClick = { song ->
                            homeVm.addToRecent(song)
                            showPlayer = true
                        },
                    )
                    2 -> LibraryScreen()
                }
            }
        }
    }

    // Full-screen player overlay
    AnimatedVisibility(
        visible = showPlayer && currentSong != null,
        enter = slideInVertically(tween(380)) { it } + fadeIn(tween(280)),
        exit  = slideOutVertically(tween(320)) { it } + fadeOut(tween(220)),
    ) {
        currentSong?.let { song ->
            PlayerSheet(
                song      = song,
                isPlaying = isPlaying,
                queue     = PlayerController.getQueue(),
                onDismiss = { showPlayer = false },
            )
        }
    }
}

@Composable
fun LibraryScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = androidx.compose.ui.Alignment.Center,
    ) {
        Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
            Icon(
                Icons.Rounded.LibraryMusic,
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
            )
            Spacer(Modifier.height(12.dp))
            Text("Library", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)
            Text("Coming soon", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
