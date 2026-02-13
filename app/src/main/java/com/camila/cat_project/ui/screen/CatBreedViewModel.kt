package com.camila.cat_project.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.camila.cat_project.domain.model.Result
import com.camila.cat_project.domain.usecase.GetCatBreedsUseCase
import com.camila.cat_project.domain.usecase.SearchBreedsUseCase
import com.camila.cat_project.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class CatBreedViewModel @Inject constructor(
    private val getCatBreedsUseCase: GetCatBreedsUseCase,
    private val searchBreedsUseCase: SearchBreedsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CatBreedsUiState(isLoading = true))
    val uiState: StateFlow<CatBreedsUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")

    init {
        loadBreeds(shouldRefresh = false)
        observeSearchQuery()
    }

    private fun loadBreeds(shouldRefresh: Boolean) {
        viewModelScope.launch {
            if (shouldRefresh) {
                _uiState.update { it.copy(isRefreshing = true) }
            } else {
                _uiState.update { it.copy(isLoading = true) }
            }

            getCatBreedsUseCase(shouldRefresh).collectLatest { result ->
                when (result) {
                    is Result.Success -> {
                        _uiState.update {
                            it.copy(
                                breeds = result.data,
                                isLoading = false,
                                isRefreshing = false,
                                error = null
                            )
                        }
                    }
                    is Result.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isRefreshing = false,
                                error = result.message ?: "Unknown error occurred"
                            )
                        }
                    }
                }
            }
        }
    }

    private fun observeSearchQuery() {
        viewModelScope.launch {
            _searchQuery
                .debounce(300)
                .distinctUntilChanged()
                .flatMapLatest { query ->
                    if (query.isBlank()) {
                        getCatBreedsUseCase(shouldRefresh = false)
                    } else {
                        searchBreedsUseCase(query).let { flow ->
                            kotlinx.coroutines.flow.flow {
                                flow.collect { breeds ->
                                    emit(Result.Success(breeds))
                                }
                            }
                        }
                    }
                }
                .collectLatest { result ->
                    when (result) {
                        is Result.Success -> {
                            _uiState.update {
                                it.copy(breeds = result.data, error = null)
                            }
                        }
                        is Result.Error -> {
                            // Keep current breeds on search error
                        }
                    }
                }
        }
    }

    fun onRefresh() {
        loadBreeds(shouldRefresh = true)
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        _searchQuery.value = query
    }

    fun onToggleFavorite(breedId: String) {
        viewModelScope.launch {
            when (val result = toggleFavoriteUseCase(breedId)) {
                is Result.Success -> {
                    // Update local state immediately for better UX
                    _uiState.update { state ->
                        state.copy(
                            breeds = state.breeds.map { breed ->
                                if (breed.id == breedId) {
                                    breed.copy(isFavorite = !breed.isFavorite)
                                } else {
                                    breed
                                }
                            }
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(error = result.message)
                    }
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}