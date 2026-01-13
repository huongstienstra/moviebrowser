package com.vyvienne.moviebrowser.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vyvienne.moviebrowser.domain.model.Favorite
import com.vyvienne.moviebrowser.domain.model.MediaType
import com.vyvienne.moviebrowser.domain.model.MovieDetail
import com.vyvienne.moviebrowser.domain.usecase.GetMovieDetailUseCase
import com.vyvienne.moviebrowser.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MovieDetailUiState(
    val isLoading: Boolean = true,
    val movie: MovieDetail? = null,
    val isFavorite: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getMovieDetailUseCase: GetMovieDetailUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val movieId: Int = checkNotNull(savedStateHandle["movieId"])

    private val _uiState = MutableStateFlow(MovieDetailUiState())
    val uiState: StateFlow<MovieDetailUiState> = _uiState.asStateFlow()

    init {
        loadMovieDetail()
        observeFavoriteStatus()
    }

    private fun loadMovieDetail() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            getMovieDetailUseCase(movieId)
                .onSuccess { movie ->
                    _uiState.update { it.copy(isLoading = false, movie = movie) }
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Failed to load movie details"
                        )
                    }
                }
        }
    }

    private fun observeFavoriteStatus() {
        viewModelScope.launch {
            toggleFavoriteUseCase.isFavorite(movieId, MediaType.MOVIE)
                .collect { isFavorite ->
                    _uiState.update { it.copy(isFavorite = isFavorite) }
                }
        }
    }

    fun toggleFavorite() {
        val movie = _uiState.value.movie ?: return

        viewModelScope.launch {
            if (_uiState.value.isFavorite) {
                toggleFavoriteUseCase.removeFavorite(movieId, MediaType.MOVIE)
            } else {
                toggleFavoriteUseCase.addFavorite(
                    Favorite(
                        id = movie.id,
                        title = movie.title,
                        posterPath = movie.posterPath,
                        voteAverage = movie.voteAverage,
                        mediaType = MediaType.MOVIE
                    )
                )
            }
        }
    }

    fun retry() {
        loadMovieDetail()
    }
}
