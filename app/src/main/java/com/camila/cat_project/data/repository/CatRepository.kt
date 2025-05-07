package com.camila.cat_project.data.repository

import android.util.Log
import com.camila.cat_project.data.local.dao.CatBreedDao
import com.camila.cat_project.data.local.entity.CatBreedEntity
import com.camila.cat_project.data.mapper.CatBreedMapper
import com.camila.cat_project.data.remote.api.CatApiService
import com.camila.cat_project.domain.model.CatBreedModel
import com.camila.cat_project.domain.repository.CatBreedRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class CatRepositoryImpl @Inject constructor(
    private val apiService: CatApiService,
    private val mapper: CatBreedMapper,
    private val breedDao: CatBreedDao
) : CatBreedRepository {

    override suspend fun getAllBreeds(): List<CatBreedModel> {
        val localBreeds = breedDao.getAllBreeds().first()
        return try {
            val entities = localBreeds.ifEmpty {
                val api =
                    apiService.getBreeds(apiKey = "live_dLLr4LPMOFwVwl31R3rjNrYg5ixhIZG5kq5iE8HRZrKIiasrZbORw5HjqT8HsJhB")

                val breedEntitiesFromApi = mapper.mapImageDtoList(api)
                breedDao.insertBreed(breedEntitiesFromApi)

                breedEntitiesFromApi
            }

            mapper.mapCatBreedEntityList(entities)

        } catch (e: Exception) {
            Log.d("CatRepository", "Error fetching breeds: ${e.message}")
            emptyList()
        }
    }
}
