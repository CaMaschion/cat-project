package com.camila.cat_project.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CatBreedDto(
    val id: String,
    val name: String,
    val origin: String?,
    val temperament: String?,
    @SerializedName("life_span")
    val lifeSpan: String?,
    val description: String?
)
