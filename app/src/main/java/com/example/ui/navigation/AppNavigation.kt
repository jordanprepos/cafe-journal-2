package com.example.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.ThemeRepository
import com.example.ui.add.AddCafeScreen
import com.example.ui.detail.CafeDetailScreen
import com.example.ui.home.HomeScreen
import com.example.ui.locations.LocationsScreen
import com.example.ui.profile.ProfileScreen
import com.example.ui.stats.StatsScreen
import com.example.ui.theme.DMSansFontFamily
import com.example.ui.wrapped.WrappedScreen

const val ROUTE_HOME = "home"
const val ROUTE_LOCATIONS = "locations"
const val ROUTE_STATS = "stats"
const val ROUTE_PROFILE = "profile"
const val ROUTE_ADD_CAFE = "add_cafe"
const val ROUTE_CAFE_DETAIL = "cafe_detail/{cafeId}"
const val ROUTE_EDIT_CAFE = "edit_cafe/{cafeId}"
const val ROUTE_WRAPPED = "wrapped"

data class BottomNavItem(val name: String, val route: String, val icon: ImageVector)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val themeRepository = remember { ThemeRepository(context) }
    val isDarkModeState by themeRepository.isDarkMode.collectAsState(initial = null)
    val systemDark = isSystemInDarkTheme()
    val isDark = isDarkModeState ?: systemDark

    val navItems = listOf(
        BottomNavItem("Journal", ROUTE_HOME, Icons.Default.Home),
        BottomNavItem("Places", ROUTE_LOCATIONS, Icons.Default.LocationOn),
        BottomNavItem("Stats", ROUTE_STATS, Icons.Default.BarChart),
        BottomNavItem("Profile", ROUTE_PROFILE, Icons.Default.Person)
    )

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            val currentRoute = currentDestination?.route
            val showBottomBar = currentRoute in listOf(ROUTE_HOME, ROUTE_LOCATIONS, ROUTE_STATS, ROUTE_PROFILE)

            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                NavigationBar(
                    containerColor = if (isDark) Color(0xFF2C221D) else Color.White,
                    tonalElevation = 0.dp
                ) {
                    navItems.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any {
                            it.route == item.route
                        } == true
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.name) },
                            label = {
                                Text(
                                    text = item.name,
                                    fontFamily = DMSansFontFamily,
                                    fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
                                    fontSize = 10.sp
                                )
                            },
                            selected = selected,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFFC05A3B),
                                selectedTextColor = Color(0xFFC05A3B),
                                indicatorColor = Color(0x33C05A3B),
                                unselectedIconColor = if (isDark) Color(0xFF8A7B73) else Color(0xFFD6CFC7),
                                unselectedTextColor = if (isDark) Color(0xFFD5C2B9).copy(alpha = 0.6f) else Color(0xFF2E241E).copy(alpha = 0.45f)
                            ),
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = ROUTE_HOME,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(ROUTE_HOME) {
                HomeScreen(
                    onAddClick = { navController.navigate(ROUTE_ADD_CAFE) },
                    onCafeClick = { cafeId -> navController.navigate("cafe_detail/$cafeId") }
                )
            }
            composable(ROUTE_LOCATIONS) {
                LocationsScreen(
                    onCafeClick = { cafeId -> navController.navigate("cafe_detail/$cafeId") }
                )
            }
            composable(ROUTE_STATS) {
                StatsScreen(
                    onCafeClick = { cafeId -> navController.navigate("cafe_detail/$cafeId") }
                )
            }
            composable(ROUTE_PROFILE) {
                ProfileScreen(
                    onWrappedClick = { navController.navigate(ROUTE_WRAPPED) }
                )
            }
            composable(ROUTE_ADD_CAFE) {
                AddCafeScreen(onBack = { navController.popBackStack() })
            }
            composable(
                route = ROUTE_CAFE_DETAIL,
                arguments = listOf(navArgument("cafeId") { type = NavType.StringType })
            ) { backStackEntry ->
                val cafeId = backStackEntry.arguments?.getString("cafeId") ?: ""
                CafeDetailScreen(
                    cafeId = cafeId,
                    onBack = { navController.popBackStack() },
                    onEditClick = { editId -> navController.navigate("edit_cafe/$editId") }
                )
            }
            composable(
                route = ROUTE_EDIT_CAFE,
                arguments = listOf(navArgument("cafeId") { type = NavType.StringType })
            ) { backStackEntry ->
                val editId = backStackEntry.arguments?.getString("cafeId") ?: ""
                AddCafeScreen(
                    cafeId = editId,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(ROUTE_WRAPPED) {
                WrappedScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
