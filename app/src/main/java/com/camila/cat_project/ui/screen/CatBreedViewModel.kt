package com.camila.cat_project.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.camila.cat_project.domain.usecase.CatBreedsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CatBreedViewModel @Inject constructor(
    private val getCatBreedsUseCase: CatBreedsUseCase
): ViewModel() {

    private val _catBreedState = MutableStateFlow<UiState>(UiState.Loading)
    val catBreedState: StateFlow<UiState> get() = _catBreedState

    init {
        getCatBreeds()
    }

    private fun getCatBreeds() {
        _catBreedState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val breeds = getCatBreedsUseCase()
                _catBreedState.value = UiState.Success(breeds)
            } catch (e: Exception) {
                _catBreedState.value = UiState.Error("Error fetching cat breeds: ${e.message}")
            }
        }
    }
}