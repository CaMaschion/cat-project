package com.camila.cat_project.domain.repository

import com.camila.cat_project.domain.model.CatBreedModel
import com.camila.cat_project.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface CatBreedRepository {
    /**
     * Get all breeds with offline-first strategy.
     * Emits cached data first, then fetches from network if shouldRefresh is true.
     */
    fun getAllBreeds(shouldRefresh: Boolean = false): Flow<Result<List<CatBreedModel>>>

    /**
     * Get breed by ID from local cache
     */
    fun getBreedById(id: String): Flow<CatBreedModel?>

    /**
     * Get favorite breeds
     */
    fun getFavorites(): Flow<List<CatBreedModel>>

    /**
     * Toggle favorite status for a breed
     */
    suspend fun toggleFavorite(id: String): Result<Unit>

    /**
     * Search breeds by name
     */
    fun searchBreeds(query: String): Flow<List<CatBreedModel>>

    /**
     * Force refresh breeds from network
     */
    suspend fun refreshBreeds(): Result<Unit>
}