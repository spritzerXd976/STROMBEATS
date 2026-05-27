package com.stormbeats.app.ui.compose

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stormbeats.app.ui.theme.*
import com.stormbeats.app.util.PlayerController

data class NavItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)

val navItems = listOf(
    NavItem("Home",    Icons.Filled.Home,          Icons.Outlined.Home),
    NavItem("Search",  Icons.Filled.Search,        Icons.Outlined.Search),
    NavItem("Library", Icons.Rounded.LibraryMusic, Icons.Rounded.LibraryMusic),
)

@Composable
fun MainScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showPlayer  by remember { mutableStateOf(false) }
    val currentSong by PlayerController.currentSong.collectAsState()
    val isPlaying   by PlayerController.isPlaying.collectAsState()

    Scaffold(
        containerColor = SurfaceDark,
        bottomBar = {
            Column {
                // Mini player — slides up when song plays
                AnimatedVisibility(
                    visible = currentSong != null,
                    enter = slideInVertically { it } + fadeIn(tween(300)),
                    exit  = slideOutVertically { it } + fadeOut(tween(300)),
                ) {
                    currentSong?.let { song ->
                        MiniPlayerBar(song = song, isPlaying = isPlaying, onExpandClick = { showPlayer = true })
                    }
                }

                // Navigation bar with gradient top border
                Box(
                    modifier = Modifier.fillMaxWidth().background(SurfaceDark)
                        .border(
                            width = 1.dp,
                            brush = Brush.horizontalGradient(listOf(Color.Transparent, Color(0xFF2E2E4A), Color.Transparent)),
                            shape = RoundedCornerShape(0.dp),
                        ),
                ) {
                    NavigationBar(
                        containerColor = Color.Transparent,
                        tonalElevation = 0.dp,
                        modifier = Modifier.navigationBarsPadding(),
                    ) {
                        navItems.forEachIndexed { index, item ->
                            val isSelected = selectedTab == index
                            NavigationBarItem(
                                selected = isSelected,
                                onClick  = { selectedTab = index },
                                icon = {
                                    Box(
                                        modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp))
                                            .background(
                                                if (isSelected)
                                                    Brush.linearGradient(listOf(VioletPrimary.copy(0.2f), PinkAccent.copy(0.15f)))
                                                else
                                                    Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
                                            ),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Icon(
                                            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                            contentDescription = item.label,
                                            modifier = Modifier.size(22.dp),
                                            tint = if (isSelected) VioletSoft else Color(0xFF4A4A6A),
                                        )
                                    }
                                },
                                label = {
                                    Text(
                                        item.label,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                        color = if (isSelected) VioletSoft else Color(0xFF4A4A6A),
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor   = VioletSoft,
                                    selectedTextColor   = VioletSoft,
                                    unselectedIconColor = Color(0xFF4A4A6A),
                                    unselectedTextColor = Color(0xFF4A4A6A),
                                    indicatorColor      = Color.Transparent,
                                ),
                            )
                        }
                    }
                }
            }
        },
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = { fadeIn(tween(220)) togetherWith fadeOut(tween(220)) },
                label = "tabs",
            ) { tab ->
                when (tab) {
                    0 -> HomeScreen(onShowPlayer = { showPlayer = true })
                    1 -> SearchScreen(onSongClick = { showPlayer = true })
                    2 -> LibraryScreen()
                }
            }
        }
    }

    // Full-screen player overlay
    AnimatedVisibility(
        visible = showPlayer && currentSong != null,
        enter = slideInVertically(tween(400)) { it } + fadeIn(tween(300)),
        exit  = slideOutVertically(tween(350)) { it } + fadeOut(tween(250)),
    ) {
        currentSong?.let { song ->
            PlayerSheet(song = song, isPlaying = isPlaying, onDismiss = { showPlayer = false })
        }
    }
}

@Composable
private fun LibraryScreen() {
    Box(
        modifier = Modifier.fillMaxSize().background(SurfaceDark).statusBarsPadding(),
        contentAlignment = Alignment.Center,
    ) {
        // Ambient glow
        Box(
            modifier = Modifier.size(320.dp).offset(y = (-80).dp)
                .background(VioletPrimary.copy(0.08f), CircleShape)
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(
                modifier = Modifier.size(84.dp).clip(CircleShape)
                    .background(Brush.linearGradient(listOf(VioletPrimary.copy(0.2f), PinkAccent.copy(0.15f)))),
                contentAlignment = Alignment.Center,
            ) { Icon(Icons.Rounded.LibraryMusic, null, modifier = Modifier.size(42.dp), tint = VioletSoft) }
            Text("Your Library", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Coming soon", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF6666AA))
        }
    }
}
