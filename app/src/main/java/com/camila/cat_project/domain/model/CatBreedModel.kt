package com.camila.cat_project.domain.model

data class CatBreedModel(
    val id: String,
    val name: String,
    val origin: String?,
    val temperament: String?,
    val lifeSpan: String?,
    val description: String?,
    val imageUrl: String,
    val isFavorite: Boolean = false
)
