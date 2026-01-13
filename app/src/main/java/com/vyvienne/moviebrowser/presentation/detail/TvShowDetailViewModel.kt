package com.vyvienne.moviebrowser.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vyvienne.moviebrowser.domain.model.Favorite
import com.vyvienne.moviebrowser.domain.model.MediaType
import com.vyvienne.moviebrowser.domain.model.TvShowDetail
import com.vyvienne.moviebrowser.domain.usecase.GetTvShowDetailUseCase
import com.vyvienne.moviebrowser.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TvShowDetailUiState(
    val isLoading: Boolean = true,
    val tvShow: TvShowDetail? = null,
    val isFavorite: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class TvShowDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getTvShowDetailUseCase: GetTvShowDetailUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val tvId: Int = checkNotNull(savedStateHandle["tvId"])

    private val _uiState = MutableStateFlow(TvShowDetailUiState())
    val uiState: StateFlow<TvShowDetailUiState> = _uiState.asStateFlow()

    init {
        loadTvShowDetail()
        observeFavoriteStatus()
    }

    private fun loadTvShowDetail() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            getTvShowDetailUseCase(tvId)
                .onSuccess { tvShow ->
                    _uiState.update { it.copy(isLoading = false, tvShow = tvShow) }
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Failed to load TV show details"
                        )
                    }
                }
        }
    }

    private fun observeFavoriteStatus() {
        viewModelScope.launch {
            toggleFavoriteUseCase.isFavorite(tvId, MediaType.TV_SHOW)
                .collect { isFavorite ->
                    _uiState.update { it.copy(isFavorite = isFavorite) }
                }
        }
    }

    fun toggleFavorite() {
        val tvShow = _uiState.value.tvShow ?: return

        viewModelScope.launch {
            if (_uiState.value.isFavorite) {
                toggleFavoriteUseCase.removeFavorite(tvId, MediaType.TV_SHOW)
            } else {
                toggleFavoriteUseCase.addFavorite(
                    Favorite(
                        id = tvShow.id,
                        title = tvShow.name,
                        posterPath = tvShow.posterPath,
                        voteAverage = tvShow.voteAverage,
                        mediaType = MediaType.TV_SHOW
                    )
                )
            }
        }
    }

    fun retry() {
        loadTvShowDetail()
    }
}
