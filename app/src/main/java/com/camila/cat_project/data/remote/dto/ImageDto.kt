package com.camila.cat_project.data.remote.dto

data class ImageDto(
    val id: String,
    val url: String,
    val width: Int,
    val height: Int,
    val breeds: List<CatBreedDto>
)
