package com.vyvienne.moviebrowser.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vyvienne.moviebrowser.domain.model.Favorite
import com.vyvienne.moviebrowser.domain.model.MediaType

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val posterPath: String?,
    val voteAverage: Double,
    val mediaType: String,
    val addedAt: Long = System.currentTimeMillis()
)

fun FavoriteEntity.toDomain(): Favorite {
    return Favorite(
        id = id,
        title = title,
        posterPath = posterPath,
        voteAverage = voteAverage,
        mediaType = MediaType.valueOf(mediaType),
        addedAt = addedAt
    )
}

fun Favorite.toEntity(): FavoriteEntity {
    return FavoriteEntity(
        id = id,
        title = title,
        posterPath = posterPath,
        voteAverage = voteAverage,
        mediaType = mediaType.name,
        addedAt = addedAt
    )
}
