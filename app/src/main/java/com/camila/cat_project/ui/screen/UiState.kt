package com.camila.cat_project.ui.screen

import com.camila.cat_project.domain.model.CatBreedModel

data class CatBreedsUiState(
    val breeds: List<CatBreedModel> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val searchQuery: String = ""
) {
    val isEmpty: Boolean get() = breeds.isEmpty() && !isLoading && error == null
}

data class BreedDetailUiState(
    val breed: CatBreedModel? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

data class FavoritesUiState(
    val favorites: List<CatBreedModel> = emptyList(),
    val isLoading: Boolean = false
) {
    val isEmpty: Boolean get() = favorites.isEmpty() && !isLoading
}