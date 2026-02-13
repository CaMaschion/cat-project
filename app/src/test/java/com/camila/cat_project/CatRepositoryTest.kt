package com.camila.cat_project

import com.camila.cat_project.data.local.dao.CatBreedDao
import com.camila.cat_project.data.local.entity.CatBreedEntity
import com.camila.cat_project.data.mapper.CatBreedMapper
import com.camila.cat_project.data.remote.api.CatApiService
import com.camila.cat_project.data.remote.dto.ImageDto
import com.camila.cat_project.data.repository.CatRepositoryImpl
import com.camila.cat_project.domain.model.CatBreedModel
import com.camila.cat_project.domain.model.Result
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CatRepositoryTest {

    private lateinit var catRepository: CatRepositoryImpl
    private val apiService: CatApiService = mockk()
    private val breedDao: CatBreedDao = mockk()
    private val mapper: CatBreedMapper = mockk()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        catRepository = CatRepositoryImpl(
            apiService = apiService,
            mapper = mapper,
            breedDao = breedDao
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `GIVEN local DB has data WHEN getAllBreeds without refresh THEN return cached data`() =
        runTest(testDispatcher) {
            // GIVEN
            val localData = listOf(
                CatBreedEntity("1", "Bengal", "India", "Active", "desc", "15", "url", false)
            )
            val mappedModels = listOf(
                CatBreedModel("1", "Bengal", "India", "Active", "15", "desc", "url", false)
            )

            coEvery { breedDao.getAllBreeds() } returns flowOf(localData)
            every { mapper.mapCatBreedEntityList(localData) } returns mappedModels

            // WHEN
            val results = catRepository.getAllBreeds(shouldRefresh = false).toList()

            // THEN
            assertTrue(results.isNotEmpty())
            val successResult = results.first()
            assertTrue(successResult is Result.Success)
            assertEquals(mappedModels, (successResult as Result.Success).data)
            coVerify(exactly = 0) { apiService.getBreeds(any(), any(), any(), any()) }
        }

    @Test
    fun `GIVEN local DB is empty WHEN getAllBreeds THEN fetch from API and cache`() =
        runTest(testDispatcher) {
            // GIVEN
            val emptyLocal = emptyList<CatBreedEntity>()
            val apiResponse = listOf(ImageDto("1", "url", 100, 100, emptyList()))
            val mappedEntities = listOf(
                CatBreedEntity("1", "Unknown", "Unknown", "Unknown", "", "Unknown", "url", false)
            )
            val mappedModels = listOf(
                CatBreedModel("1", "Unknown", "Unknown", "Unknown", "Unknown", "", "url", false)
            )

            coEvery { breedDao.getAllBreeds() } returns flowOf(emptyLocal) andThen flowOf(mappedEntities)
            coEvery { apiService.getBreeds(any(), any(), any(), any()) } returns apiResponse
            every { mapper.mapImageDtoList(apiResponse) } returns mappedEntities
            coEvery { breedDao.getBreedById(any()) } returns null
            coEvery { breedDao.insertBreeds(any()) } just Runs
            every { mapper.mapCatBreedEntityList(mappedEntities) } returns mappedModels

            // WHEN
            val results = catRepository.getAllBreeds(shouldRefresh = false).toList()

            // THEN
            assertTrue(results.isNotEmpty())
            coVerify { apiService.getBreeds(any(), any(), any(), any()) }
            coVerify { breedDao.insertBreeds(any()) }
        }

    @Test
    fun `GIVEN local DB has data WHEN getAllBreeds with refresh THEN return cached then fetch API`() =
        runTest(testDispatcher) {
            // GIVEN
            val localData = listOf(
                CatBreedEntity("1", "Bengal", "India", "Active", "desc", "15", "url", false)
            )
            val mappedModels = listOf(
                CatBreedModel("1", "Bengal", "India", "Active", "15", "desc", "url", false)
            )
            val apiResponse = listOf(ImageDto("1", "url", 100, 100, emptyList()))

            coEvery { breedDao.getAllBreeds() } returns flowOf(localData)
            coEvery { apiService.getBreeds(any(), any(), any(), any()) } returns apiResponse
            every { mapper.mapCatBreedEntityList(localData) } returns mappedModels
            every { mapper.mapImageDtoList(apiResponse) } returns localData
            coEvery { breedDao.getBreedById(any()) } returns localData.first()
            coEvery { breedDao.insertBreeds(any()) } just Runs

            // WHEN
            val results = catRepository.getAllBreeds(shouldRefresh = true).toList()

            // THEN
            assertTrue(results.isNotEmpty())
            // First emission is cached data
            assertTrue(results.first() is Result.Success)
            coVerify { apiService.getBreeds(any(), any(), any(), any()) }
        }

    @Test
    fun `GIVEN breed exists WHEN toggleFavorite THEN update favorite status`() =
        runTest(testDispatcher) {
            // GIVEN
            val breed = CatBreedEntity("1", "Bengal", "India", "Active", "desc", "15", "url", false)
            coEvery { breedDao.getBreedById("1") } returns breed
            coEvery { breedDao.updateFavoriteStatus("1", true) } just Runs

            // WHEN
            val result = catRepository.toggleFavorite("1")

            // THEN
            assertTrue(result is Result.Success)
            coVerify { breedDao.updateFavoriteStatus("1", true) }
        }

    @Test
    fun `GIVEN breed does not exist WHEN toggleFavorite THEN return error`() =
        runTest(testDispatcher) {
            // GIVEN
            coEvery { breedDao.getBreedById("nonexistent") } returns null

            // WHEN
            val result = catRepository.toggleFavorite("nonexistent")

            // THEN
            assertTrue(result is Result.Error)
        }

    @Test
    fun `WHEN searchBreeds THEN return filtered breeds from DAO`() =
        runTest(testDispatcher) {
            // GIVEN
            val searchResults = listOf(
                CatBreedEntity("1", "Bengal", "India", "Active", "desc", "15", "url", false)
            )
            val mappedModels = listOf(
                CatBreedModel("1", "Bengal", "India", "Active", "15", "desc", "url", false)
            )

            coEvery { breedDao.searchBreeds("Bengal") } returns flowOf(searchResults)
            every { mapper.mapCatBreedEntityList(searchResults) } returns mappedModels

            // WHEN
            val results = catRepository.searchBreeds("Bengal").toList()

            // THEN
            assertEquals(1, results.size)
            assertEquals(mappedModels, results.first())
        }
}