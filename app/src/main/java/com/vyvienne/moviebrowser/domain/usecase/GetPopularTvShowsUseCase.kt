package com.vyvienne.moviebrowser.domain.usecase

import com.vyvienne.moviebrowser.domain.model.TvShow
import com.vyvienne.moviebrowser.domain.repository.MovieRepository
import javax.inject.Inject

class GetPopularTvShowsUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(page: Int = 1): Result<List<TvShow>> {
        return repository.getPopularTvShows(page)
    }
}
