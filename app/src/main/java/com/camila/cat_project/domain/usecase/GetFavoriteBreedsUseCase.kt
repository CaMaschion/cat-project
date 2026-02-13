package com.camila.cat_project.domain.usecase

import com.camila.cat_project.domain.model.CatBreedModel
import com.camila.cat_project.domain.repository.CatBreedRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavoriteBreedsUseCase @Inject constructor(
    private val repository: CatBreedRepository
) {
    operator fun invoke(): Flow<List<CatBreedModel>> {
        return repository.getFavorites()
    }
}
