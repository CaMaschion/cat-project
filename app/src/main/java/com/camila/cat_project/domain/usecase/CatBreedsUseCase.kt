package com.camila.cat_project.domain.usecase

import com.camila.cat_project.domain.model.CatBreedModel
import com.camila.cat_project.domain.repository.CatBreedRepository
import javax.inject.Inject

class CatBreedsUseCase @Inject constructor(
    private val repository: CatBreedRepository
) {
    suspend fun getAllBreeds(): List<CatBreedModel> {
        return repository.getAllBreeds()
    }
}
