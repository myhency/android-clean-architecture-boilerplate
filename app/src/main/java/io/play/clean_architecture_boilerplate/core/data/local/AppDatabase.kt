package io.play.clean_architecture_boilerplate.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import io.play.clean_architecture_boilerplate.core.data.local.entities.AffirmationEntity

@Database(entities = [AffirmationEntity::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun affirmationDao(): AffirmationDao
}