package com.vyvienne.moviebrowser.domain.repository

import com.vyvienne.moviebrowser.domain.model.Favorite
import com.vyvienne.moviebrowser.domain.model.MediaType
import com.vyvienne.moviebrowser.domain.model.Movie
import com.vyvienne.moviebrowser.domain.model.MovieDetail
import com.vyvienne.moviebrowser.domain.model.TvShow
import com.vyvienne.moviebrowser.domain.model.TvShowDetail
import kotlinx.coroutines.flow.Flow

interface MovieRepository {

    // Movies
    suspend fun getPopularMovies(page: Int = 1): Result<List<Movie>>
    suspend fun getTopRatedMovies(page: Int = 1): Result<List<Movie>>
    suspend fun getNowPlayingMovies(page: Int = 1): Result<List<Movie>>
    suspend fun getTrendingMovies(page: Int = 1): Result<List<Movie>>
    suspend fun getMovieDetail(movieId: Int): Result<MovieDetail>
    suspend fun searchMovies(query: String, page: Int = 1): Result<List<Movie>>

    // TV Shows
    suspend fun getPopularTvShows(page: Int = 1): Result<List<TvShow>>
    suspend fun getTopRatedTvShows(page: Int = 1): Result<List<TvShow>>
    suspend fun getTrendingTvShows(page: Int = 1): Result<List<TvShow>>
    suspend fun getTvShowDetail(tvId: Int): Result<TvShowDetail>
    suspend fun searchTvShows(query: String, page: Int = 1): Result<List<TvShow>>

    // Favorites
    fun getAllFavorites(): Flow<List<Favorite>>
    fun isFavorite(id: Int, mediaType: MediaType): Flow<Boolean>
    suspend fun addFavorite(favorite: Favorite)
    suspend fun removeFavorite(id: Int, mediaType: MediaType)
}
