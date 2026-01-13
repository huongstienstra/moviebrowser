package com.vyvienne.moviebrowser.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vyvienne.moviebrowser.domain.model.Movie
import com.vyvienne.moviebrowser.domain.model.TvShow
import com.vyvienne.moviebrowser.domain.usecase.SearchMoviesUseCase
import com.vyvienne.moviebrowser.domain.usecase.SearchTvShowsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val movies: List<Movie> = emptyList(),
    val tvShows: List<TvShow> = emptyList(),
    val error: String? = null,
    val hasSearched: Boolean = false
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchMoviesUseCase: SearchMoviesUseCase,
    private val searchTvShowsUseCase: SearchTvShowsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query) }

        searchJob?.cancel()

        if (query.isBlank()) {
            _uiState.update { it.copy(movies = emptyList(), tvShows = emptyList(), hasSearched = false) }
            return
        }

        searchJob = viewModelScope.launch {
            delay(500) // Debounce
            search(query)
        }
    }

    private suspend fun search(query: String) {
        _uiState.update { it.copy(isLoading = true, error = null) }

        val moviesResult = searchMoviesUseCase(query)
        val tvShowsResult = searchTvShowsUseCase(query)

        _uiState.update { state ->
            state.copy(
                isLoading = false,
                movies = moviesResult.getOrDefault(emptyList()),
                tvShows = tvShowsResult.getOrDefault(emptyList()),
                hasSearched = true,
                error = if (moviesResult.isFailure && tvShowsResult.isFailure) {
                    "Search failed. Please try again."
                } else null
            )
        }
    }

    fun retry() {
        val query = _uiState.value.query
        if (query.isNotBlank()) {
            viewModelScope.launch {
                search(query)
            }
        }
    }
}
