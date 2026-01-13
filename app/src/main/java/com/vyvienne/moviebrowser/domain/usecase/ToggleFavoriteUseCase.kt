package com.vyvienne.moviebrowser.domain.usecase

import com.vyvienne.moviebrowser.domain.model.Favorite
import com.vyvienne.moviebrowser.domain.model.MediaType
import com.vyvienne.moviebrowser.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend fun addFavorite(favorite: Favorite) {
        repository.addFavorite(favorite)
    }

    suspend fun removeFavorite(id: Int, mediaType: MediaType) {
        repository.removeFavorite(id, mediaType)
    }

    fun isFavorite(id: Int, mediaType: MediaType): Flow<Boolean> {
        return repository.isFavorite(id, mediaType)
    }
}
