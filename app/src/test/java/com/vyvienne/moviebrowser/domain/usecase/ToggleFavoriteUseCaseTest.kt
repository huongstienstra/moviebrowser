package com.vyvienne.moviebrowser.domain.usecase

import com.vyvienne.moviebrowser.domain.model.Favorite
import com.vyvienne.moviebrowser.domain.model.MediaType
import com.vyvienne.moviebrowser.domain.repository.MovieRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ToggleFavoriteUseCaseTest {

    private lateinit var repository: MovieRepository
    private lateinit var useCase: ToggleFavoriteUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = ToggleFavoriteUseCase(repository)
    }

    @Test
    fun `addFavorite calls repository addFavorite`() = runTest {
        // Given
        val favorite = createTestFavorite(1, "Test Movie", MediaType.MOVIE)
        coEvery { repository.addFavorite(favorite) } returns Unit

        // When
        useCase.addFavorite(favorite)

        // Then
        coVerify(exactly = 1) { repository.addFavorite(favorite) }
    }

    @Test
    fun `removeFavorite calls repository removeFavorite with correct parameters`() = runTest {
        // Given
        coEvery { repository.removeFavorite(1, MediaType.MOVIE) } returns Unit

        // When
        useCase.removeFavorite(1, MediaType.MOVIE)

        // Then
        coVerify(exactly = 1) { repository.removeFavorite(1, MediaType.MOVIE) }
    }

    @Test
    fun `removeFavorite handles TV_SHOW media type`() = runTest {
        // Given
        coEvery { repository.removeFavorite(5, MediaType.TV_SHOW) } returns Unit

        // When
        useCase.removeFavorite(5, MediaType.TV_SHOW)

        // Then
        coVerify(exactly = 1) { repository.removeFavorite(5, MediaType.TV_SHOW) }
    }

    @Test
    fun `isFavorite returns true when item is favorited`() = runTest {
        // Given
        every { repository.isFavorite(1, MediaType.MOVIE) } returns flowOf(true)

        // When
        val result = useCase.isFavorite(1, MediaType.MOVIE).first()

        // Then
        assertTrue(result)
        verify(exactly = 1) { repository.isFavorite(1, MediaType.MOVIE) }
    }

    @Test
    fun `isFavorite returns false when item is not favorited`() = runTest {
        // Given
        every { repository.isFavorite(99, MediaType.TV_SHOW) } returns flowOf(false)

        // When
        val result = useCase.isFavorite(99, MediaType.TV_SHOW).first()

        // Then
        assertFalse(result)
    }

    private fun createTestFavorite(id: Int, title: String, mediaType: MediaType) = Favorite(
        id = id,
        title = title,
        posterPath = "/test.jpg",
        voteAverage = 8.0,
        mediaType = mediaType,
        addedAt = System.currentTimeMillis()
    )
}
