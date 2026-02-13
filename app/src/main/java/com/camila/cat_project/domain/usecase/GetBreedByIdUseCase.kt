package com.camila.cat_project.domain.usecase

import com.camila.cat_project.domain.model.CatBreedModel
import com.camila.cat_project.domain.repository.CatBreedRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBreedByIdUseCase @Inject constructor(
    private val repository: CatBreedRepository
) {
    operator fun invoke(id: String): Flow<CatBreedModel?> {
        return repository.getBreedById(id)
    }
}
