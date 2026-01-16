package com.vyvienne.moviebrowser.domain.usecase

import com.vyvienne.moviebrowser.domain.model.Movie
import com.vyvienne.moviebrowser.domain.repository.MovieRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetPopularMoviesUseCaseTest {

    private lateinit var repository: MovieRepository
    private lateinit var useCase: GetPopularMoviesUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetPopularMoviesUseCase(repository)
    }

    @Test
    fun `invoke returns movies from repository on success`() = runTest {
        // Given
        val expectedMovies = listOf(
            createTestMovie(1, "Movie 1"),
            createTestMovie(2, "Movie 2")
        )
        coEvery { repository.getPopularMovies(1) } returns Result.success(expectedMovies)

        // When
        val result = useCase()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedMovies, result.getOrNull())
        coVerify(exactly = 1) { repository.getPopularMovies(1) }
    }

    @Test
    fun `invoke returns failure when repository fails`() = runTest {
        // Given
        val exception = RuntimeException("Network error")
        coEvery { repository.getPopularMovies(1) } returns Result.failure(exception)

        // When
        val result = useCase()

        // Then
        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke passes page parameter to repository`() = runTest {
        // Given
        coEvery { repository.getPopularMovies(3) } returns Result.success(emptyList())

        // When
        useCase(page = 3)

        // Then
        coVerify(exactly = 1) { repository.getPopularMovies(3) }
    }

    @Test
    fun `invoke uses default page 1 when not specified`() = runTest {
        // Given
        coEvery { repository.getPopularMovies(1) } returns Result.success(emptyList())

        // When
        useCase()

        // Then
        coVerify(exactly = 1) { repository.getPopularMovies(1) }
    }

    private fun createTestMovie(id: Int, title: String) = Movie(
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
