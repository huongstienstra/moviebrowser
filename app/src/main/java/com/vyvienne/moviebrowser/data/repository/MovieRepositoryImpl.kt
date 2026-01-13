package com.vyvienne.moviebrowser.data.repository

import com.vyvienne.moviebrowser.data.local.dao.FavoriteDao
import com.vyvienne.moviebrowser.data.local.entity.toDomain
import com.vyvienne.moviebrowser.data.local.entity.toEntity
import com.vyvienne.moviebrowser.data.remote.api.TMDBApi
import com.vyvienne.moviebrowser.data.remote.dto.toDomain
import com.vyvienne.moviebrowser.domain.model.Favorite
import com.vyvienne.moviebrowser.domain.model.MediaType
import com.vyvienne.moviebrowser.domain.model.Movie
import com.vyvienne.moviebrowser.domain.model.MovieDetail
import com.vyvienne.moviebrowser.domain.model.TvShow
import com.vyvienne.moviebrowser.domain.model.TvShowDetail
import com.vyvienne.moviebrowser.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val api: TMDBApi,
    private val favoriteDao: FavoriteDao
) : MovieRepository {

    override suspend fun getPopularMovies(page: Int): Result<List<Movie>> {
        return runCatching {
            api.getPopularMovies(page).results.map { it.toDomain() }
        }
    }

    override suspend fun getTopRatedMovies(page: Int): Result<List<Movie>> {
        return runCatching {
            api.getTopRatedMovies(page).results.map { it.toDomain() }
        }
    }

    override suspend fun getNowPlayingMovies(page: Int): Result<List<Movie>> {
        return runCatching {
            api.getNowPlayingMovies(page).results.map { it.toDomain() }
        }
    }

    override suspend fun getTrendingMovies(page: Int): Result<List<Movie>> {
        return runCatching {
            api.getTrendingMovies(page).results.map { it.toDomain() }
        }
    }

    override suspend fun getMovieDetail(movieId: Int): Result<MovieDetail> {
        return runCatching {
            api.getMovieDetail(movieId).toDomain()
        }
    }

    override suspend fun searchMovies(query: String, page: Int): Result<List<Movie>> {
        return runCatching {
            api.searchMovies(query, page).results.map { it.toDomain() }
        }
    }

    override suspend fun getPopularTvShows(page: Int): Result<List<TvShow>> {
        return runCatching {
            api.getPopularTvShows(page).results.map { it.toDomain() }
        }
    }

    override suspend fun getTopRatedTvShows(page: Int): Result<List<TvShow>> {
        return runCatching {
            api.getTopRatedTvShows(page).results.map { it.toDomain() }
        }
    }

    override suspend fun getTrendingTvShows(page: Int): Result<List<TvShow>> {
        return runCatching {
            api.getTrendingTvShows(page).results.map { it.toDomain() }
        }
    }

    override suspend fun getTvShowDetail(tvId: Int): Result<TvShowDetail> {
        return runCatching {
            api.getTvShowDetail(tvId).toDomain()
        }
    }

    override suspend fun searchTvShows(query: String, page: Int): Result<List<TvShow>> {
        return runCatching {
            api.searchTvShows(query, page).results.map { it.toDomain() }
        }
    }

    override fun getAllFavorites(): Flow<List<Favorite>> {
        return favoriteDao.getAllFavorites().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun isFavorite(id: Int, mediaType: MediaType): Flow<Boolean> {
        return favoriteDao.isFavorite(id, mediaType.name)
    }

    override suspend fun addFavorite(favorite: Favorite) {
        favoriteDao.insertFavorite(favorite.toEntity())
    }

    override suspend fun removeFavorite(id: Int, mediaType: MediaType) {
        favoriteDao.deleteFavoriteById(id, mediaType.name)
    }
}
