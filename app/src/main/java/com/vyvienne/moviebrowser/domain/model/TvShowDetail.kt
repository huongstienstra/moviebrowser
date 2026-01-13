package com.vyvienne.moviebrowser.domain.model

data class TvShowDetail(
    val id: Int,
    val name: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val voteAverage: Double,
    val voteCount: Int,
    val firstAirDate: String,
    val lastAirDate: String,
    val numberOfSeasons: Int,
    val numberOfEpisodes: Int,
    val genres: List<Genre>,
    val status: String,
    val tagline: String
)
