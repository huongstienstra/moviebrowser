package com.vyvienne.moviebrowser.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.vyvienne.moviebrowser.domain.model.Genre
import com.vyvienne.moviebrowser.domain.model.TvShowDetail

@JsonClass(generateAdapter = true)
data class TvShowDetailDto(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "overview") val overview: String?,
    @Json(name = "poster_path") val posterPath: String?,
    @Json(name = "backdrop_path") val backdropPath: String?,
    @Json(name = "vote_average") val voteAverage: Double,
    @Json(name = "vote_count") val voteCount: Int,
    @Json(name = "first_air_date") val firstAirDate: String?,
    @Json(name = "last_air_date") val lastAirDate: String?,
    @Json(name = "number_of_seasons") val numberOfSeasons: Int?,
    @Json(name = "number_of_episodes") val numberOfEpisodes: Int?,
    @Json(name = "genres") val genres: List<GenreDto>?,
    @Json(name = "status") val status: String?,
    @Json(name = "tagline") val tagline: String?
)

fun TvShowDetailDto.toDomain(): TvShowDetail {
    return TvShowDetail(
        id = id,
        name = name,
        overview = overview ?: "",
        posterPath = posterPath,
        backdropPath = backdropPath,
        voteAverage = voteAverage,
        voteCount = voteCount,
        firstAirDate = firstAirDate ?: "",
        lastAirDate = lastAirDate ?: "",
        numberOfSeasons = numberOfSeasons ?: 0,
        numberOfEpisodes = numberOfEpisodes ?: 0,
        genres = genres?.map { Genre(it.id, it.name) } ?: emptyList(),
        status = status ?: "",
        tagline = tagline ?: ""
    )
}
