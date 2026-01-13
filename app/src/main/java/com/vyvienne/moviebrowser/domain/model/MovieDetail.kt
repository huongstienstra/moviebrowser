package com.vyvienne.moviebrowser.domain.model

data class MovieDetail(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val voteAverage: Double,
    val voteCount: Int,
    val releaseDate: String,
    val runtime: Int,
    val genres: List<Genre>,
    val status: String,
    val tagline: String
)

data class Genre(
    val id: Int,
    val name: String
)
