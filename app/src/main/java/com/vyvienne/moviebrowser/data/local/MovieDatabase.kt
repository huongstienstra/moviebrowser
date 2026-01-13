package com.vyvienne.moviebrowser.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.vyvienne.moviebrowser.data.local.dao.FavoriteDao
import com.vyvienne.moviebrowser.data.local.entity.FavoriteEntity

@Database(
    entities = [FavoriteEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MovieDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
}
