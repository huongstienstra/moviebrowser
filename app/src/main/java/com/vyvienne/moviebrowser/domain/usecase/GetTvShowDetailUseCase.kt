package com.vyvienne.moviebrowser.domain.usecase

import com.vyvienne.moviebrowser.domain.model.TvShowDetail
import com.vyvienne.moviebrowser.domain.repository.MovieRepository
import javax.inject.Inject

class GetTvShowDetailUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(tvId: Int): Result<TvShowDetail> {
        return repository.getTvShowDetail(tvId)
    }
}
