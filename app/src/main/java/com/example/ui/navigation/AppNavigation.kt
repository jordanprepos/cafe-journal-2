package com.example.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.ui.add.AddCafeScreen
import com.example.ui.detail.CafeDetailScreen
import com.example.ui.home.HomeScreen
import com.example.ui.locations.LocationsScreen
import com.example.ui.profile.ProfileScreen
import com.example.ui.stats.StatsScreen

const val ROUTE_HOME = "home"
const val ROUTE_LOCATIONS = "locations"
const val ROUTE_STATS = "stats"
const val ROUTE_PROFILE = "profile"
const val ROUTE_ADD_CAFE = "add_cafe"
const val ROUTE_CAFE_DETAIL = "cafe_detail/{cafeId}"
const val ROUTE_EDIT_CAFE = "edit_cafe/{cafeId}"

data class BottomNavItem(val name: String, val route: String, val icon: ImageVector)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

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

            if (showBottomBar) {
                NavigationBar {
                    navItems.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any { 
                            it.route == item.route 
                        } == true
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.name) },
                            label = { Text(item.name) },
                            selected = selected,
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
                StatsScreen()
            }
            composable(ROUTE_PROFILE) {
                ProfileScreen()
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
        }
    }
}
