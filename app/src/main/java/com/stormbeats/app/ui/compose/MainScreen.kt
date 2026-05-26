package com.stormbeats.app.ui.compose

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.People
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.stormbeats.app.util.PlayerController

data class NavItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)

val navItems = listOf(
    NavItem("Home",    Icons.Filled.Home,    Icons.Outlined.Home),
    NavItem("Search",  Icons.Filled.Search,  Icons.Outlined.Search),
    NavItem("Library", Icons.Rounded.LibraryMusic, Icons.Rounded.LibraryMusic),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showPlayer  by remember { mutableStateOf(false) }
    val currentSong by PlayerController.currentSong.collectAsState()
    val isPlaying   by PlayerController.isPlaying.collectAsState()

    Scaffold(
        bottomBar = {
            Column {
                // Mini player bar above nav
                AnimatedVisibility(
                    visible = currentSong != null,
                    enter = slideInVertically { it } + fadeIn(),
                    exit  = slideOutVertically { it } + fadeOut(),
                ) {
                    currentSong?.let { song ->
                        MiniPlayerBar(
                            song = song,
                            isPlaying = isPlaying,
                            onExpandClick = { showPlayer = true },
                        )
                    }
                }

                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp,
                    modifier = Modifier.clip(RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp)),
                ) {
                    navItems.forEachIndexed { index, item ->
                        NavigationBarItem(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            icon = {
                                Icon(
                                    imageVector = if (selectedTab == index) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label,
                                    modifier = Modifier.size(24.dp),
                                )
                            },
                            label = {
                                Text(
                                    text = item.label,
                                    style = MaterialTheme.typography.labelSmall,
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor   = MaterialTheme.colorScheme.onBackground,
                                selectedTextColor   = MaterialTheme.colorScheme.onBackground,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor      = Color.Transparent,
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
                transitionSpec = {
                    fadeIn(animationSpec = tween(220)) togetherWith
                            fadeOut(animationSpec = tween(220))
                },
                label = "tabTransition",
            ) { tab ->
                when (tab) {
                    0 -> HomeScreen(onShowPlayer = { showPlayer = true })
                    1 -> SearchScreen(onSongClick = { showPlayer = true })
                    2 -> LibraryPlaceholder()
                }
            }
        }
    }

    if (showPlayer) {
        currentSong?.let { song ->
            PlayerSheet(
                song = song,
                isPlaying = isPlaying,
                onDismiss = { showPlayer = false },
            )
        }
    }
}

@Composable
private fun LibraryPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                Icons.Rounded.LibraryMusic,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
            )
            Text(
                "Your Library",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
            )
            Text(
                "Coming soon",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.25f),
            )
        }
    }
}
