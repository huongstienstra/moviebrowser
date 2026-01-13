package com.vyvienne.moviebrowser.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vyvienne.moviebrowser.domain.model.MediaType
import com.vyvienne.moviebrowser.presentation.components.ErrorMessage
import com.vyvienne.moviebrowser.presentation.components.LoadingIndicator
import com.vyvienne.moviebrowser.presentation.components.MediaCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onMovieClick: (Int) -> Unit,
    onTvShowClick: (Int) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Movie Browser") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                LoadingIndicator(modifier = Modifier.padding(paddingValues))
            }
            uiState.error != null && uiState.trendingMovies.isEmpty() -> {
                ErrorMessage(
                    message = uiState.error!!,
                    onRetry = { viewModel.loadContent() },
                    modifier = Modifier.padding(paddingValues)
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    if (uiState.trendingMovies.isNotEmpty()) {
                        item {
                            MediaSectionContent(
                                title = "Trending Movies",
                                items = uiState.trendingMovies,
                                onItemClick = onMovieClick
                            )
                        }
                    }

                    if (uiState.popularMovies.isNotEmpty()) {
                        item {
                            MediaSectionContent(
                                title = "Popular Movies",
                                items = uiState.popularMovies,
                                onItemClick = onMovieClick
                            )
                        }
                    }

                    if (uiState.trendingTvShows.isNotEmpty()) {
                        item {
                            TvShowSectionContent(
                                title = "Trending TV Shows",
                                items = uiState.trendingTvShows,
                                onItemClick = onTvShowClick
                            )
                        }
                    }

                    if (uiState.popularTvShows.isNotEmpty()) {
                        item {
                            TvShowSectionContent(
                                title = "Popular TV Shows",
                                items = uiState.popularTvShows,
                                onItemClick = onTvShowClick
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MediaSectionContent(
    title: String,
    items: List<com.vyvienne.moviebrowser.domain.model.Movie>,
    onItemClick: (Int) -> Unit
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items.size) { index ->
            val movie = items[index]
            MediaCard(
                title = movie.title,
                posterPath = movie.posterPath,
                voteAverage = movie.voteAverage,
                onClick = { onItemClick(movie.id) }
            )
        }
    }
}

@Composable
private fun TvShowSectionContent(
    title: String,
    items: List<com.vyvienne.moviebrowser.domain.model.TvShow>,
    onItemClick: (Int) -> Unit
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items.size) { index ->
            val tvShow = items[index]
            MediaCard(
                title = tvShow.name,
                posterPath = tvShow.posterPath,
                voteAverage = tvShow.voteAverage,
                onClick = { onItemClick(tvShow.id) }
            )
        }
    }
}
