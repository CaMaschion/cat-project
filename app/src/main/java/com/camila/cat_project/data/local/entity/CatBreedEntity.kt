package com.camila.cat_project.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cat_breeds")
data class CatBreedEntity(
    @PrimaryKey val id: String,
    val name: String,
    val origin: String,
    val temperament: String,
    val description: String,
    val lifeSpan: String,
    val imageUrl: String,
    val isFavorite: Boolean = false
)
