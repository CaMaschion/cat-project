package com.camila.cat_project.ui.screen

import com.camila.cat_project.domain.model.CatBreedModel

sealed class UiState {
    data object Loading : UiState()
    data class Success(val catBreeds: List<CatBreedModel>) : UiState()
    data class Error(val message: String) : UiState()
}