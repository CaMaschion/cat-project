package com.camila.cat_project.data.repository

import android.util.Log
import com.camila.cat_project.data.local.dao.CatBreedDao
import com.camila.cat_project.data.local.entity.CatBreedEntity
import com.camila.cat_project.data.mapper.CatBreedMapper
import com.camila.cat_project.data.remote.api.CatApiService
import kotlinx.coroutines.flow.first
import javax.inject.Inject

interface CatRepository {
    suspend fun getAllBreeds(): List<CatBreedEntity>
}

class CatRepositoryImpl @Inject constructor(
    private val apiService: CatApiService,
    private val mapper: CatBreedMapper,
    private val breedDao: CatBreedDao
) : CatRepository {

    override suspend fun getAllBreeds(): List<CatBreedEntity> {
        val localBreeds = breedDao.getAllBreeds().first()
        try {
            return localBreeds.ifEmpty {
                val api =
                    apiService.getBreeds(apiKey = "live_dLLr4LPMOFwVwl31R3rjNrYg5ixhIZG5kq5iE8HRZrKIiasrZbORw5HjqT8HsJhB")
                val breedEntities = mapper.mapImageDtoList(api)
                breedDao.insertBreed(breedEntities)
                breedEntities
            }
        } catch (e: Exception) {
            Log.d("CatRepository", "Error fetching breeds: ${e.message}")
            return emptyList()
        }
    }
}
