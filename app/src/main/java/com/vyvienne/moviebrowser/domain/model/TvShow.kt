package com.vyvienne.moviebrowser.domain.model

data class TvShow(
    val id: Int,
    val name: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val voteAverage: Double,
    val voteCount: Int,
    val firstAirDate: String,
    val genreIds: List<Int>,
    val popularity: Double
)
