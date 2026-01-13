package com.vyvienne.moviebrowser.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.vyvienne.moviebrowser.presentation.detail.MovieDetailScreen
import com.vyvienne.moviebrowser.presentation.detail.TvShowDetailScreen
import com.vyvienne.moviebrowser.presentation.favorites.FavoritesScreen
import com.vyvienne.moviebrowser.presentation.home.HomeScreen
import com.vyvienne.moviebrowser.presentation.search.SearchScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = bottomNavItems.any { it.route == currentDestination?.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    bottomNavItems.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label
                                )
                            },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onMovieClick = { movieId ->
                        navController.navigate(Screen.MovieDetail.createRoute(movieId))
                    },
                    onTvShowClick = { tvId ->
                        navController.navigate(Screen.TvShowDetail.createRoute(tvId))
                    }
                )
            }

            composable(Screen.Search.route) {
                SearchScreen(
                    onMovieClick = { movieId ->
                        navController.navigate(Screen.MovieDetail.createRoute(movieId))
                    },
                    onTvShowClick = { tvId ->
                        navController.navigate(Screen.TvShowDetail.createRoute(tvId))
                    }
                )
            }

            composable(Screen.Favorites.route) {
                FavoritesScreen(
                    onMovieClick = { movieId ->
                        navController.navigate(Screen.MovieDetail.createRoute(movieId))
                    },
                    onTvShowClick = { tvId ->
                        navController.navigate(Screen.TvShowDetail.createRoute(tvId))
                    }
                )
            }

            composable(
                route = Screen.MovieDetail.route,
                arguments = listOf(
                    navArgument("movieId") { type = NavType.IntType }
                )
            ) {
                MovieDetailScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.TvShowDetail.route,
                arguments = listOf(
                    navArgument("tvId") { type = NavType.IntType }
                )
            ) {
                TvShowDetailScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
