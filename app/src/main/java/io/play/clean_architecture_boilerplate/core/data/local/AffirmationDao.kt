package io.play.clean_architecture_boilerplate.core.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.play.clean_architecture_boilerplate.core.data.local.entities.AffirmationEntity

@Dao
interface AffirmationDao {
    @Query("select * from AffirmationEntity")
    suspend fun getAll(): List<AffirmationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(affirmationEntity: AffirmationEntity)

    @Delete
    suspend fun delete(affirmationEntity: AffirmationEntity)

    @Update
    suspend fun update(affirmationEntity: AffirmationEntity)
}