package com.vyvienne.moviebrowser.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.vyvienne.moviebrowser.domain.model.Genre
import com.vyvienne.moviebrowser.domain.model.MovieDetail

@JsonClass(generateAdapter = true)
data class MovieDetailDto(
    @Json(name = "id") val id: Int,
    @Json(name = "title") val title: String,
    @Json(name = "overview") val overview: String?,
    @Json(name = "poster_path") val posterPath: String?,
    @Json(name = "backdrop_path") val backdropPath: String?,
    @Json(name = "vote_average") val voteAverage: Double,
    @Json(name = "vote_count") val voteCount: Int,
    @Json(name = "release_date") val releaseDate: String?,
    @Json(name = "runtime") val runtime: Int?,
    @Json(name = "genres") val genres: List<GenreDto>?,
    @Json(name = "status") val status: String?,
    @Json(name = "tagline") val tagline: String?,
    @Json(name = "budget") val budget: Long?,
    @Json(name = "revenue") val revenue: Long?
)

@JsonClass(generateAdapter = true)
data class GenreDto(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String
)

fun MovieDetailDto.toDomain(): MovieDetail {
    return MovieDetail(
        id = id,
        title = title,
        overview = overview ?: "",
        posterPath = posterPath,
        backdropPath = backdropPath,
        voteAverage = voteAverage,
        voteCount = voteCount,
        releaseDate = releaseDate ?: "",
        runtime = runtime ?: 0,
        genres = genres?.map { Genre(it.id, it.name) } ?: emptyList(),
        status = status ?: "",
        tagline = tagline ?: ""
    )
}
