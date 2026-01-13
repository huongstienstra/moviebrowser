package com.vyvienne.moviebrowser.domain.model

data class Favorite(
    val id: Int,
    val title: String,
    val posterPath: String?,
    val voteAverage: Double,
    val mediaType: MediaType,
    val addedAt: Long = System.currentTimeMillis()
)

enum class MediaType {
    MOVIE,
    TV_SHOW
}
