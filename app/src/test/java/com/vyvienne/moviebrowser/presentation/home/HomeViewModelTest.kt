package com.vyvienne.moviebrowser.presentation.home

import com.vyvienne.moviebrowser.domain.model.Movie
import com.vyvienne.moviebrowser.domain.model.TvShow
import com.vyvienne.moviebrowser.domain.usecase.GetPopularMoviesUseCase
import com.vyvienne.moviebrowser.domain.usecase.GetPopularTvShowsUseCase
import com.vyvienne.moviebrowser.domain.usecase.GetTrendingMoviesUseCase
import com.vyvienne.moviebrowser.domain.usecase.GetTrendingTvShowsUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private lateinit var getTrendingMoviesUseCase: GetTrendingMoviesUseCase
    private lateinit var getPopularMoviesUseCase: GetPopularMoviesUseCase
    private lateinit var getTrendingTvShowsUseCase: GetTrendingTvShowsUseCase
    private lateinit var getPopularTvShowsUseCase: GetPopularTvShowsUseCase

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getTrendingMoviesUseCase = mockk()
        getPopularMoviesUseCase = mockk()
        getTrendingTvShowsUseCase = mockk()
        getPopularTvShowsUseCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state shows loading`() = runTest {
        // Given
        coEvery { getTrendingMoviesUseCase() } returns Result.success(emptyList())
        coEvery { getPopularMoviesUseCase() } returns Result.success(emptyList())
        coEvery { getTrendingTvShowsUseCase() } returns Result.success(emptyList())
        coEvery { getPopularTvShowsUseCase() } returns Result.success(emptyList())

        // When
        val viewModel = createViewModel()

        // Then - initial state before coroutines complete
        assertTrue(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `loadContent populates all content on success`() = runTest {
        // Given
        val trendingMovies = listOf(createTestMovie(1, "Trending Movie"))
        val popularMovies = listOf(createTestMovie(2, "Popular Movie"))
        val trendingTvShows = listOf(createTestTvShow(3, "Trending TV"))
        val popularTvShows = listOf(createTestTvShow(4, "Popular TV"))

        coEvery { getTrendingMoviesUseCase() } returns Result.success(trendingMovies)
        coEvery { getPopularMoviesUseCase() } returns Result.success(popularMovies)
        coEvery { getTrendingTvShowsUseCase() } returns Result.success(trendingTvShows)
        coEvery { getPopularTvShowsUseCase() } returns Result.success(popularTvShows)

        // When
        val viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(trendingMovies, state.trendingMovies)
        assertEquals(popularMovies, state.popularMovies)
        assertEquals(trendingTvShows, state.trendingTvShows)
        assertEquals(popularTvShows, state.popularTvShows)
        assertNull(state.error)
    }

    @Test
    fun `loadContent shows error only when all requests fail`() = runTest {
        // Given
        val exception = RuntimeException("Network error")
        coEvery { getTrendingMoviesUseCase() } returns Result.failure(exception)
        coEvery { getPopularMoviesUseCase() } returns Result.failure(exception)
        coEvery { getTrendingTvShowsUseCase() } returns Result.failure(exception)
        coEvery { getPopularTvShowsUseCase() } returns Result.failure(exception)

        // When
        val viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals("Failed to load content. Please try again.", state.error)
    }

    @Test
    fun `loadContent shows partial content when some requests succeed`() = runTest {
        // Given
        val trendingMovies = listOf(createTestMovie(1, "Trending Movie"))
        val exception = RuntimeException("Network error")

        coEvery { getTrendingMoviesUseCase() } returns Result.success(trendingMovies)
        coEvery { getPopularMoviesUseCase() } returns Result.failure(exception)
        coEvery { getTrendingTvShowsUseCase() } returns Result.failure(exception)
        coEvery { getPopularTvShowsUseCase() } returns Result.failure(exception)

        // When
        val viewModel = createViewModel()
        advanceUntilIdle()

        // Then - no error shown because at least one succeeded
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(trendingMovies, state.trendingMovies)
        assertTrue(state.popularMovies.isEmpty())
        assertNull(state.error)
    }

    @Test
    fun `loadContent can be retried after error`() = runTest {
        // Given - first call fails
        val exception = RuntimeException("Network error")
        coEvery { getTrendingMoviesUseCase() } returns Result.failure(exception)
        coEvery { getPopularMoviesUseCase() } returns Result.failure(exception)
        coEvery { getTrendingTvShowsUseCase() } returns Result.failure(exception)
        coEvery { getPopularTvShowsUseCase() } returns Result.failure(exception)

        val viewModel = createViewModel()
        advanceUntilIdle()

        // Verify error state
        assertEquals("Failed to load content. Please try again.", viewModel.uiState.value.error)

        // Given - retry succeeds
        val movies = listOf(createTestMovie(1, "Movie"))
        coEvery { getTrendingMoviesUseCase() } returns Result.success(movies)
        coEvery { getPopularMoviesUseCase() } returns Result.success(emptyList())
        coEvery { getTrendingTvShowsUseCase() } returns Result.success(emptyList())
        coEvery { getPopularTvShowsUseCase() } returns Result.success(emptyList())

        // When
        viewModel.loadContent()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertNull(state.error)
        assertEquals(movies, state.trendingMovies)
    }

    private fun createViewModel() = HomeViewModel(
        getTrendingMoviesUseCase = getTrendingMoviesUseCase,
        getPopularMoviesUseCase = getPopularMoviesUseCase,
        getTrendingTvShowsUseCase = getTrendingTvShowsUseCase,
        getPopularTvShowsUseCase = getPopularTvShowsUseCase
    )

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

    private fun createTestTvShow(id: Int, name: String) = TvShow(
        id = id,
        name = name,
        overview = "Test overview",
        posterPath = "/test.jpg",
        backdropPath = "/backdrop.jpg",
        voteAverage = 8.0,
        voteCount = 200,
        firstAirDate = "2024-01-01",
        genreIds = listOf(18, 10765),
        popularity = 150.0
    )
}
