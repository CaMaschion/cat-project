package com.camila.cat_project.data.repository

import com.camila.cat_project.data.local.dao.CatBreedDao
import com.camila.cat_project.data.mapper.CatBreedMapper
import com.camila.cat_project.data.remote.api.CatApiService
import com.camila.cat_project.domain.model.CatBreedModel
import com.camila.cat_project.domain.model.Result
import com.camila.cat_project.domain.repository.CatBreedRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CatRepositoryImpl @Inject constructor(
    private val apiService: CatApiService,
    private val mapper: CatBreedMapper,
    private val breedDao: CatBreedDao
) : CatBreedRepository {

    /**
     * Offline-first strategy:
     * 1. Emit cached data immediately if available
     * 2. If shouldRefresh OR cache is empty, fetch from network
     * 3. On network success, update cache and emit new data
     * 4. On network failure, emit error only if cache was empty
     */
    override fun getAllBreeds(shouldRefresh: Boolean): Flow<Result<List<CatBreedModel>>> = flow {
        // First, emit cached data
        val cachedBreeds = breedDao.getAllBreeds().first()
        val hasCache = cachedBreeds.isNotEmpty()

        if (hasCache) {
            emit(Result.Success(mapper.mapCatBreedEntityList(cachedBreeds)))
        }

        // Fetch from network if refresh requested or no cache
        if (shouldRefresh || !hasCache) {
            try {
                val apiResponse = apiService.getBreeds()
                val entities = mapper.mapImageDtoList(apiResponse)

                // Preserve favorite status when updating
                val updatedEntities = entities.map { newEntity ->
                    val existingEntity = breedDao.getBreedById(newEntity.id)
                    newEntity.copy(isFavorite = existingEntity?.isFavorite ?: false)
                }

                breedDao.insertBreeds(updatedEntities)

                // Emit fresh data
                val freshBreeds = breedDao.getAllBreeds().first()
                emit(Result.Success(mapper.mapCatBreedEntityList(freshBreeds)))
            } catch (e: Exception) {
                // Only emit error if we had no cache
                if (!hasCache) {
                    emit(Result.Error(e, "Failed to load cat breeds: ${e.message}"))
                }
                // If we have cache, we already emitted it, so just log the error
            }
        }
    }.catch { e ->
        emit(Result.Error(e, "Unexpected error: ${e.message}"))
    }

    override fun getBreedById(id: String): Flow<CatBreedModel?> {
        return breedDao.getBreedByIdFlow(id).map { entity ->
            entity?.let { mapper.mapCatBreedEntity(it) }
        }
    }

    override fun getFavorites(): Flow<List<CatBreedModel>> {
        return breedDao.getFavoriteBreeds().map { entities ->
            mapper.mapCatBreedEntityList(entities)
        }
    }

    override suspend fun toggleFavorite(id: String): Result<Unit> {
        return try {
            val breed = breedDao.getBreedById(id)
            if (breed != null) {
                breedDao.updateFavoriteStatus(id, !breed.isFavorite)
                Result.Success(Unit)
            } else {
                Result.Error(IllegalArgumentException("Breed not found"), "Breed with id $id not found")
            }
        } catch (e: Exception) {
            Result.Error(e, "Failed to update favorite: ${e.message}")
        }
    }

    override fun searchBreeds(query: String): Flow<List<CatBreedModel>> {
        return breedDao.searchBreeds(query).map { entities ->
            mapper.mapCatBreedEntityList(entities)
        }
    }

    override suspend fun refreshBreeds(): Result<Unit> {
        return try {
            val apiResponse = apiService.getBreeds()
            val entities = mapper.mapImageDtoList(apiResponse)

            // Preserve favorite status
            val updatedEntities = entities.map { newEntity ->
                val existingEntity = breedDao.getBreedById(newEntity.id)
                newEntity.copy(isFavorite = existingEntity?.isFavorite ?: false)
            }

            breedDao.insertBreeds(updatedEntities)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e, "Failed to refresh breeds: ${e.message}")
        }
    }
}
