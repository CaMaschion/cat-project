package com.camila.cat_project.domain.usecase

import com.camila.cat_project.domain.model.Result
import com.camila.cat_project.domain.repository.CatBreedRepository
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val repository: CatBreedRepository
) {
    suspend operator fun invoke(id: String): Result<Unit> {
        return repository.toggleFavorite(id)
    }
}
