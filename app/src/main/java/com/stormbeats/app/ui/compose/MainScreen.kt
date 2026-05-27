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

data class NavItem(val label: String, val selIcon: ImageVector, val unselIcon: ImageVector)

private val NAV_ITEMS = listOf(
    NavItem("Home",    Icons.Filled.Home,          Icons.Outlined.Home),
    NavItem("Search",  Icons.Filled.Search,        Icons.Outlined.Search),
    NavItem("Library", Icons.Rounded.LibraryMusic, Icons.Rounded.LibraryMusic),
)

@Composable
fun MainScreen() {
    var tab        by remember { mutableIntStateOf(0) }
    var showPlayer by remember { mutableStateOf(false) }
    val song   by PlayerController.currentSong.collectAsState()
    val playing by PlayerController.isPlaying.collectAsState()

    Scaffold(
        containerColor = SurfaceDark,
        bottomBar = {
            Column {
                AnimatedVisibility(song != null, enter = slideInVertically { it } + fadeIn(tween(300)), exit = slideOutVertically { it } + fadeOut(tween(300))) {
                    song?.let { s -> MiniPlayerBar(s, playing) { showPlayer = true } }
                }
                Box(Modifier.fillMaxWidth().background(SurfaceDark).border(1.dp, Brush.horizontalGradient(listOf(Color.Transparent, Color(0xFF2A2A40), Color.Transparent)), RoundedCornerShape(0.dp))) {
                    NavigationBar(containerColor = Color.Transparent, tonalElevation = 0.dp, modifier = Modifier.navigationBarsPadding()) {
                        NAV_ITEMS.forEachIndexed { i, item ->
                            val sel = tab == i
                            NavigationBarItem(
                                selected = sel, onClick = { tab = i },
                                icon = {
                                    Box(
                                        Modifier.size(38.dp).clip(RoundedCornerShape(11.dp))
                                            .background(if (sel) Brush.linearGradient(listOf(VioletPrimary.copy(0.18f), PinkAccent.copy(0.12f))) else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))),
                                        contentAlignment = Alignment.Center,
                                    ) { Icon(if (sel) item.selIcon else item.unselIcon, item.label, Modifier.size(21.dp), tint = if (sel) VioletSoft else Color(0xFF4A4A6A)) }
                                },
                                label = {
                                    Text(item.label, style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Normal, color = if (sel) VioletSoft else Color(0xFF4A4A6A))
                                },
                                colors = NavigationBarItemDefaults.colors(selectedIconColor = VioletSoft, selectedTextColor = VioletSoft, unselectedIconColor = Color(0xFF4A4A6A), unselectedTextColor = Color(0xFF4A4A6A), indicatorColor = Color.Transparent),
                            )
                        }
                    }
                }
            }
        },
    ) { pad ->
        Box(Modifier.padding(pad)) {
            AnimatedContent(tab, transitionSpec = { fadeIn(tween(220)) togetherWith fadeOut(tween(220)) }, label = "tab") { t ->
                when (t) {
                    0 -> HomeScreen(onShowPlayer = { showPlayer = true })
                    1 -> SearchScreen(onSongClick = { showPlayer = true })
                    2 -> LibraryPlaceholder()
                }
            }
        }
    }

    AnimatedVisibility(showPlayer && song != null, enter = slideInVertically(tween(380)) { it } + fadeIn(tween(280)), exit = slideOutVertically(tween(340)) { it } + fadeOut(tween(240))) {
        song?.let { s -> PlayerSheet(s, playing) { showPlayer = false } }
    }
}

@Composable
private fun LibraryPlaceholder() {
    Box(Modifier.fillMaxSize().background(SurfaceDark).statusBarsPadding(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Box(Modifier.size(80.dp).clip(CircleShape).background(Brush.linearGradient(listOf(VioletPrimary.copy(0.2f), PinkAccent.copy(0.15f)))), contentAlignment = Alignment.Center) {
                Icon(Icons.Rounded.LibraryMusic, null, Modifier.size(40.dp), tint = VioletSoft)
            }
            Text("Your Library", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Coming soon", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF6666AA))
        }
    }
}
