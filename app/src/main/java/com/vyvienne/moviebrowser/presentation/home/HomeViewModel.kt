package com.vyvienne.moviebrowser.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vyvienne.moviebrowser.domain.model.Movie
import com.vyvienne.moviebrowser.domain.model.TvShow
import com.vyvienne.moviebrowser.domain.usecase.GetPopularMoviesUseCase
import com.vyvienne.moviebrowser.domain.usecase.GetPopularTvShowsUseCase
import com.vyvienne.moviebrowser.domain.usecase.GetTrendingMoviesUseCase
import com.vyvienne.moviebrowser.domain.usecase.GetTrendingTvShowsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = true,
    val trendingMovies: List<Movie> = emptyList(),
    val popularMovies: List<Movie> = emptyList(),
    val trendingTvShows: List<TvShow> = emptyList(),
    val popularTvShows: List<TvShow> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getTrendingMoviesUseCase: GetTrendingMoviesUseCase,
    private val getPopularMoviesUseCase: GetPopularMoviesUseCase,
    private val getTrendingTvShowsUseCase: GetTrendingTvShowsUseCase,
    private val getPopularTvShowsUseCase: GetPopularTvShowsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadContent()
    }

    fun loadContent() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val trendingMoviesResult = getTrendingMoviesUseCase()
            val popularMoviesResult = getPopularMoviesUseCase()
            val trendingTvShowsResult = getTrendingTvShowsUseCase()
            val popularTvShowsResult = getPopularTvShowsUseCase()

            val hasError = trendingMoviesResult.isFailure &&
                    popularMoviesResult.isFailure &&
                    trendingTvShowsResult.isFailure &&
                    popularTvShowsResult.isFailure

            _uiState.update { state ->
                state.copy(
                    isLoading = false,
                    trendingMovies = trendingMoviesResult.getOrDefault(emptyList()),
                    popularMovies = popularMoviesResult.getOrDefault(emptyList()),
                    trendingTvShows = trendingTvShowsResult.getOrDefault(emptyList()),
                    popularTvShows = popularTvShowsResult.getOrDefault(emptyList()),
                    error = if (hasError) "Failed to load content. Please try again." else null
                )
            }
        }
    }
}
