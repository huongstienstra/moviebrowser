package com.vyvienne.moviebrowser.data.repository

import com.vyvienne.moviebrowser.data.local.dao.FavoriteDao
import com.vyvienne.moviebrowser.data.local.entity.FavoriteEntity
import com.vyvienne.moviebrowser.data.remote.api.TMDBApi
import com.vyvienne.moviebrowser.data.remote.dto.MovieDto
import com.vyvienne.moviebrowser.data.remote.dto.MovieResponse
import com.vyvienne.moviebrowser.domain.model.Favorite
import com.vyvienne.moviebrowser.domain.model.MediaType
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MovieRepositoryImplTest {

    private lateinit var api: TMDBApi
    private lateinit var favoriteDao: FavoriteDao
    private lateinit var repository: MovieRepositoryImpl

    @Before
    fun setup() {
        api = mockk()
        favoriteDao = mockk()
        repository = MovieRepositoryImpl(api, favoriteDao)
    }

    // API Tests

    @Test
    fun `getPopularMovies returns mapped movies on success`() = runTest {
        // Given
        val movieDto = createTestMovieDto(1, "Test Movie")
        val response = MovieResponse(
            page = 1,
            results = listOf(movieDto),
            totalPages = 1,
            totalResults = 1
        )
        coEvery { api.getPopularMovies(1) } returns response

        // When
        val result = repository.getPopularMovies(1)

        // Then
        assertTrue(result.isSuccess)
        val movies = result.getOrNull()!!
        assertEquals(1, movies.size)
        assertEquals("Test Movie", movies[0].title)
        assertEquals(1, movies[0].id)
    }

    @Test
    fun `getPopularMovies returns failure on API error`() = runTest {
        // Given
        coEvery { api.getPopularMovies(1) } throws RuntimeException("Network error")

        // When
        val result = repository.getPopularMovies(1)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `searchMovies passes query to API`() = runTest {
        // Given
        val response = MovieResponse(
            page = 1,
            results = listOf(createTestMovieDto(1, "Batman")),
            totalPages = 1,
            totalResults = 1
        )
        coEvery { api.searchMovies("Batman", 1) } returns response

        // When
        val result = repository.searchMovies("Batman", 1)

        // Then
        assertTrue(result.isSuccess)
        coVerify { api.searchMovies("Batman", 1) }
    }

    @Test
    fun `DTO to domain mapping handles null values gracefully`() = runTest {
        // Given - DTO with null fields
        val movieDto = MovieDto(
            id = 1,
            title = "Test",
            overview = null,
            posterPath = null,
            backdropPath = null,
            voteAverage = 7.5,
            voteCount = 100,
            releaseDate = null,
            genreIds = null,
            popularity = 50.0
        )
        val response = MovieResponse(
            page = 1,
            results = listOf(movieDto),
            totalPages = 1,
            totalResults = 1
        )
        coEvery { api.getPopularMovies(1) } returns response

        // When
        val result = repository.getPopularMovies(1)

        // Then
        assertTrue(result.isSuccess)
        val movie = result.getOrNull()!![0]
        assertEquals("", movie.overview) // null converted to empty string
        assertEquals("", movie.releaseDate) // null converted to empty string
        assertEquals(emptyList<Int>(), movie.genreIds) // null converted to empty list
    }

    // Favorites Tests

    @Test
    fun `getAllFavorites returns mapped favorites from DAO`() = runTest {
        // Given
        val entity = FavoriteEntity(
            id = 1,
            title = "Favorite Movie",
            posterPath = "/poster.jpg",
            voteAverage = 8.5,
            mediaType = "MOVIE",
            addedAt = 1000L
        )
        every { favoriteDao.getAllFavorites() } returns flowOf(listOf(entity))

        // When
        val result = repository.getAllFavorites().first()

        // Then
        assertEquals(1, result.size)
        assertEquals("Favorite Movie", result[0].title)
        assertEquals(MediaType.MOVIE, result[0].mediaType)
    }

    @Test
    fun `isFavorite returns flow from DAO`() = runTest {
        // Given
        every { favoriteDao.isFavorite(1, "MOVIE") } returns flowOf(true)

        // When
        val result = repository.isFavorite(1, MediaType.MOVIE).first()

        // Then
        assertTrue(result)
        verify { favoriteDao.isFavorite(1, "MOVIE") }
    }

    @Test
    fun `addFavorite converts domain to entity and inserts`() = runTest {
        // Given
        val favorite = Favorite(
            id = 1,
            title = "Test Movie",
            posterPath = "/poster.jpg",
            voteAverage = 8.0,
            mediaType = MediaType.MOVIE,
            addedAt = 1000L
        )
        coEvery { favoriteDao.insertFavorite(any()) } returns Unit

        // When
        repository.addFavorite(favorite)

        // Then
        coVerify {
            favoriteDao.insertFavorite(match { entity ->
                entity.id == 1 &&
                entity.title == "Test Movie" &&
                entity.mediaType == "MOVIE"
            })
        }
    }

    @Test
    fun `removeFavorite calls DAO with correct parameters`() = runTest {
        // Given
        coEvery { favoriteDao.deleteFavoriteById(5, "TV_SHOW") } returns Unit

        // When
        repository.removeFavorite(5, MediaType.TV_SHOW)

        // Then
        coVerify { favoriteDao.deleteFavoriteById(5, "TV_SHOW") }
    }

    private fun createTestMovieDto(id: Int, title: String) = MovieDto(
        id = id,
        title = title,
        overview = "Test overview",
        posterPath = "/test.jpg",
        backdropPath = "/backdrop.jpg",
        voteAverage = 7.5,
        voteCount = 100,
        releaseDate = "2024-01-01",
        genreIds = listOf(28, 12),
        popularity = 100.0
    )
}
