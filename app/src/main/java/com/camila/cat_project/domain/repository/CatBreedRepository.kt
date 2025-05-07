package com.camila.cat_project.domain.repository

import com.camila.cat_project.domain.model.CatBreedModel

interface CatBreedRepository {
    suspend fun getAllBreeds(): List<CatBreedModel>
}