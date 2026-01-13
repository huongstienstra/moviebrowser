package com.vyvienne.moviebrowser.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.vyvienne.moviebrowser.domain.model.TvShow

@JsonClass(generateAdapter = true)
data class TvShowDto(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "overview") val overview: String?,
    @Json(name = "poster_path") val posterPath: String?,
    @Json(name = "backdrop_path") val backdropPath: String?,
    @Json(name = "vote_average") val voteAverage: Double,
    @Json(name = "vote_count") val voteCount: Int,
    @Json(name = "first_air_date") val firstAirDate: String?,
    @Json(name = "genre_ids") val genreIds: List<Int>?,
    @Json(name = "popularity") val popularity: Double
)

fun TvShowDto.toDomain(): TvShow {
    return TvShow(
        id = id,
        name = name,
        overview = overview ?: "",
        posterPath = posterPath,
        backdropPath = backdropPath,
        voteAverage = voteAverage,
        voteCount = voteCount,
        firstAirDate = firstAirDate ?: "",
        genreIds = genreIds ?: emptyList(),
        popularity = popularity
    )
}
