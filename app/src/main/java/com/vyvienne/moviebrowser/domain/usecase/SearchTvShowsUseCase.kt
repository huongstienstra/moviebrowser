package com.vyvienne.moviebrowser.domain.usecase

import com.vyvienne.moviebrowser.domain.model.TvShow
import com.vyvienne.moviebrowser.domain.repository.MovieRepository
import javax.inject.Inject

class SearchTvShowsUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(query: String, page: Int = 1): Result<List<TvShow>> {
        return repository.searchTvShows(query, page)
    }
}
