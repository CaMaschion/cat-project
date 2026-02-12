package com.camila.cat_project.ui.screen.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.camila.cat_project.domain.model.Result
import com.camila.cat_project.domain.usecase.GetBreedByIdUseCase
import com.camila.cat_project.domain.usecase.ToggleFavoriteUseCase
import com.camila.cat_project.ui.screen.BreedDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getBreedByIdUseCase: GetBreedByIdUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val breedId: String = checkNotNull(savedStateHandle["breedId"])

    private val _uiState = MutableStateFlow(BreedDetailUiState(isLoading = true))
    val uiState: StateFlow<BreedDetailUiState> = _uiState.asStateFlow()

    init {
        loadBreedDetails()
    }

    private fun loadBreedDetails() {
        viewModelScope.launch {
            getBreedByIdUseCase(breedId).collectLatest { breed ->
                _uiState.update {
                    it.copy(
                        breed = breed,
                        isLoading = false,
                        error = if (breed == null) "Breed not found" else null
                    )
                }
            }
        }
    }

    fun onToggleFavorite() {
        viewModelScope.launch {
            when (val result = toggleFavoriteUseCase(breedId)) {
                is Result.Success -> {
                    // State will be updated automatically via Flow
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(error = result.message)
                    }
                }
            }
        }
    }
}
