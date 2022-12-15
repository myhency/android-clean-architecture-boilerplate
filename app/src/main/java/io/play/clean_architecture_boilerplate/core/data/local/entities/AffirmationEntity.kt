package io.play.clean_architecture_boilerplate.core.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AffirmationEntity(
    val statement: String,
    val todayFeeling: String,
    val createdAt: String,
    val imageUrl: String,
) {
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}