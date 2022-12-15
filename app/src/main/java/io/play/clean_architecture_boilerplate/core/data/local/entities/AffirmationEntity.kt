package io.play.clean_architecture_boilerplate.core.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AffirmationEntity(
    @PrimaryKey val id: String,
    val statement: String,
    val imageUrl: String
)