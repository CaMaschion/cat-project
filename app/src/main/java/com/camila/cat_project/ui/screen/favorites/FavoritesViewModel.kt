package com.camila.cat_project.ui.screen.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.camila.cat_project.domain.model.Result
import com.camila.cat_project.domain.usecase.GetFavoriteBreedsUseCase
import com.camila.cat_project.domain.usecase.ToggleFavoriteUseCase
import com.camila.cat_project.ui.screen.FavoritesUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getFavoriteBreedsUseCase: GetFavoriteBreedsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState(isLoading = true))
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            getFavoriteBreedsUseCase().collectLatest { favorites ->
                _uiState.update {
                    it.copy(
                        favorites = favorites,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onRemoveFavorite(breedId: String) {
        viewModelScope.launch {
            toggleFavoriteUseCase(breedId)
        }
    }
}
