package com.camila.cat_project

import com.camila.cat_project.domain.model.CatBreedModel
import com.camila.cat_project.domain.model.Result
import com.camila.cat_project.domain.repository.CatBreedRepository
import com.camila.cat_project.domain.usecase.GetBreedByIdUseCase
import com.camila.cat_project.domain.usecase.GetCatBreedsUseCase
import com.camila.cat_project.domain.usecase.GetFavoriteBreedsUseCase
import com.camila.cat_project.domain.usecase.SearchBreedsUseCase
import com.camila.cat_project.domain.usecase.ToggleFavoriteUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UseCaseTests {

    private val repository: CatBreedRepository = mockk()

    private val mockBreed = CatBreedModel(
        id = "1",
        name = "Abyssinian",
        origin = "Egypt",
        temperament = "Active",
        lifeSpan = "14-15",
        description = "An ancient breed",
        imageUrl = "url",
        isFavorite = false
    )

    @Test
    fun `GetCatBreedsUseCase invokes repository getAllBreeds with correct parameter`() = runTest {
        // GIVEN
        val useCase = GetCatBreedsUseCase(repository)
        every { repository.getAllBreeds(true) } returns flowOf(Result.Success(listOf(mockBreed)))

        // WHEN
        val result = useCase(shouldRefresh = true).first()

        // THEN
        verify { repository.getAllBreeds(true) }
        assertTrue(result is Result.Success)
        assertEquals(listOf(mockBreed), (result as Result.Success).data)
    }

    @Test
    fun `GetBreedByIdUseCase invokes repository getBreedById`() = runTest {
        // GIVEN
        val useCase = GetBreedByIdUseCase(repository)
        every { repository.getBreedById("1") } returns flowOf(mockBreed)

        // WHEN
        val result = useCase("1").first()

        // THEN
        verify { repository.getBreedById("1") }
        assertEquals(mockBreed, result)
    }

    @Test
    fun `GetFavoriteBreedsUseCase invokes repository getFavorites`() = runTest {
        // GIVEN
        val useCase = GetFavoriteBreedsUseCase(repository)
        val favoriteBreed = mockBreed.copy(isFavorite = true)
        every { repository.getFavorites() } returns flowOf(listOf(favoriteBreed))

        // WHEN
        val result = useCase().first()

        // THEN
        verify { repository.getFavorites() }
        assertEquals(listOf(favoriteBreed), result)
    }

    @Test
    fun `SearchBreedsUseCase invokes repository searchBreeds`() = runTest {
        // GIVEN
        val useCase = SearchBreedsUseCase(repository)
        every { repository.searchBreeds("Aby") } returns flowOf(listOf(mockBreed))

        // WHEN
        val result = useCase("Aby").first()

        // THEN
        verify { repository.searchBreeds("Aby") }
        assertEquals(listOf(mockBreed), result)
    }

    @Test
    fun `ToggleFavoriteUseCase invokes repository toggleFavorite`() = runTest {
        // GIVEN
        val useCase = ToggleFavoriteUseCase(repository)
        coEvery { repository.toggleFavorite("1") } returns Result.Success(Unit)

        // WHEN
        val result = useCase("1")

        // THEN
        coVerify { repository.toggleFavorite("1") }
        assertTrue(result is Result.Success)
    }

    @Test
    fun `ToggleFavoriteUseCase returns error when repository fails`() = runTest {
        // GIVEN
        val useCase = ToggleFavoriteUseCase(repository)
        val error = Result.Error(Exception("Failed"), "Failed to toggle favorite")
        coEvery { repository.toggleFavorite("1") } returns error

        // WHEN
        val result = useCase("1")

        // THEN
        assertTrue(result is Result.Error)
        assertEquals("Failed to toggle favorite", (result as Result.Error).message)
    }
}
