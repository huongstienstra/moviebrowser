package com.vyvienne.moviebrowser.presentation.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Search : Screen("search")
    data object Favorites : Screen("favorites")
    data object MovieDetail : Screen("movie/{movieId}") {
        fun createRoute(movieId: Int) = "movie/$movieId"
    }
    data object TvShowDetail : Screen("tv/{tvId}") {
        fun createRoute(tvId: Int) = "tv/$tvId"
    }
}
