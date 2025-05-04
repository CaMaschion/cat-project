package com.camila.cat_project

import com.camila.cat_project.data.local.dao.CatBreedDao
import com.camila.cat_project.data.local.entity.CatBreedEntity
import com.camila.cat_project.data.mapper.CatBreedMapper
import com.camila.cat_project.data.remote.api.CatApiService
import com.camila.cat_project.data.remote.dto.ImageDto
import com.camila.cat_project.data.repository.CatRepositoryImpl
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CatRepositoryTest {

    private lateinit var catRepository: CatRepositoryImpl
    private val apiService: CatApiService = mockk()
    private val breedDao: CatBreedDao = mockk()
    private val mapper: CatBreedMapper = mockk()
    private val testDispatcher = StandardTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
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
    fun `GIVEN local DB has data WHEN call getAllBreeds THEN return local data without calling API`(): Unit =
        runTest(testDispatcher) {
            // GIVEN
            val localData =
                listOf(
                    CatBreedEntity(
                        "1",
                        "Bengal",
                        "India",
                        "Active",
                        "desc",
                        "15",
                        "url",
                        false
                    )
                )
            coEvery { breedDao.getAllBreeds() } returns flowOf(localData)

            // WHEN
            val result = catRepository.getAllBreeds()

            // THEN
            assertEquals(localData, result)
            coVerify(exactly = 0) { apiService.getBreeds(any(), any(), any()) }
        }

    @Test
    fun `GIVEN local DB is empty WHEN call getAllBreeds THEN fetch from API, save and return`() =
        runTest(testDispatcher) {
            // GIVEN
            val emptyLocal = emptyList<CatBreedEntity>()
            val apiResponse = listOf(ImageDto("1", "url", 100, 100, emptyList()))
            val mappedData = listOf(
                CatBreedEntity(
                    "1",
                    "Unknown",
                    "Unknown",
                    "Unknown",
                    "",
                    "Unknown",
                    "url",
                    false
                )
            )

            coEvery { breedDao.getAllBreeds() } returns flowOf(emptyLocal)
            coEvery { apiService.getBreeds(any(), any(), any()) } returns apiResponse
            every { mapper.mapImageDtoList(apiResponse) } returns mappedData
            coEvery { breedDao.insertBreed(mappedData) } just Runs

            // WHEN
            val result = catRepository.getAllBreeds()

            // THEN
            assertEquals(mappedData, result)
            coVerify { apiService.getBreeds(any(), any(), any()) }
            coVerify { breedDao.insertBreed(mappedData) }
        }
}