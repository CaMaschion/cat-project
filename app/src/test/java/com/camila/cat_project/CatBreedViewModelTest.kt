package com.camila.cat_project

import com.camila.cat_project.domain.model.CatBreedModel
import com.camila.cat_project.domain.model.Result
import com.camila.cat_project.domain.usecase.GetCatBreedsUseCase
import com.camila.cat_project.domain.usecase.SearchBreedsUseCase
import com.camila.cat_project.domain.usecase.ToggleFavoriteUseCase
import com.camila.cat_project.ui.screen.CatBreedViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CatBreedViewModelTest {

    private val getCatBreedsUseCase: GetCatBreedsUseCase = mockk()
    private val searchBreedsUseCase: SearchBreedsUseCase = mockk()
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase = mockk()

    private lateinit var viewModel: CatBreedViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val mockBreeds = listOf(
        CatBreedModel(
            id = "1",
            name = "Abyssinian",
            origin = "Egypt",
            temperament = "Active",
            lifeSpan = "14-15",
            description = "An ancient breed",
            imageUrl = "url1",
            isFavorite = false
        ),
        CatBreedModel(
            id = "2",
            name = "Bengal",
            origin = "USA",
            temperament = "Energetic",
            lifeSpan = "12-15",
            description = "A hybrid breed",
            imageUrl = "url2",
            isFavorite = true
        )
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `WHEN ViewModel is initialized THEN loads breeds and shows loading state`() = runTest {
        // GIVEN
        every { getCatBreedsUseCase(any()) } returns flowOf(Result.Success(mockBreeds))
        every { searchBreedsUseCase(any()) } returns flowOf(emptyList())

        // WHEN
        viewModel = CatBreedViewModel(getCatBreedsUseCase, searchBreedsUseCase, toggleFavoriteUseCase)

        // Initial state should be loading
        assertTrue(viewModel.uiState.value.isLoading)

        advanceUntilIdle()

        // THEN
        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(mockBreeds, viewModel.uiState.value.breeds)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `GIVEN API error WHEN ViewModel loads breeds THEN shows error state`() = runTest {
        // GIVEN
        val errorMessage = "Network error"
        every { getCatBreedsUseCase(any()) } returns flowOf(Result.Error(Exception(errorMessage), errorMessage))
        every { searchBreedsUseCase(any()) } returns flowOf(emptyList())

        // WHEN
        viewModel = CatBreedViewModel(getCatBreedsUseCase, searchBreedsUseCase, toggleFavoriteUseCase)
        advanceUntilIdle()

        // THEN
        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(errorMessage, viewModel.uiState.value.error)
        assertTrue(viewModel.uiState.value.breeds.isEmpty())
    }

    @Test
    fun `WHEN onRefresh is called THEN shows refreshing state and loads data`() = runTest {
        // GIVEN
        every { getCatBreedsUseCase(any()) } returns flowOf(Result.Success(mockBreeds))
        every { searchBreedsUseCase(any()) } returns flowOf(emptyList())

        viewModel = CatBreedViewModel(getCatBreedsUseCase, searchBreedsUseCase, toggleFavoriteUseCase)
        advanceUntilIdle()

        // WHEN
        viewModel.onRefresh()

        // THEN - should trigger refresh
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isRefreshing)
        assertEquals(mockBreeds, viewModel.uiState.value.breeds)
    }

    @Test
    fun `WHEN toggle favorite succeeds THEN updates breed favorite status`() = runTest {
        // GIVEN
        every { getCatBreedsUseCase(any()) } returns flowOf(Result.Success(mockBreeds))
        every { searchBreedsUseCase(any()) } returns flowOf(emptyList())
        coEvery { toggleFavoriteUseCase("1") } returns Result.Success(Unit)

        viewModel = CatBreedViewModel(getCatBreedsUseCase, searchBreedsUseCase, toggleFavoriteUseCase)
        advanceUntilIdle()

        // WHEN
        viewModel.onToggleFavorite("1")
        advanceUntilIdle()

        // THEN
        val updatedBreed = viewModel.uiState.value.breeds.find { it.id == "1" }
        assertTrue(updatedBreed?.isFavorite == true)
    }

    @Test
    fun `WHEN search query changes THEN updates search query in state`() = runTest {
        // GIVEN
        every { getCatBreedsUseCase(any()) } returns flowOf(Result.Success(mockBreeds))
        every { searchBreedsUseCase(any()) } returns flowOf(emptyList())

        viewModel = CatBreedViewModel(getCatBreedsUseCase, searchBreedsUseCase, toggleFavoriteUseCase)
        advanceUntilIdle()

        // WHEN
        viewModel.onSearchQueryChanged("Bengal")

        // THEN
        assertEquals("Bengal", viewModel.uiState.value.searchQuery)
    }

    @Test
    fun `WHEN clearError is called THEN clears error from state`() = runTest {
        // GIVEN
        val errorMessage = "Network error"
        every { getCatBreedsUseCase(any()) } returns flowOf(Result.Error(Exception(errorMessage), errorMessage))
        every { searchBreedsUseCase(any()) } returns flowOf(emptyList())

        viewModel = CatBreedViewModel(getCatBreedsUseCase, searchBreedsUseCase, toggleFavoriteUseCase)
        advanceUntilIdle()

        // Verify error exists
        assertEquals(errorMessage, viewModel.uiState.value.error)

        // WHEN
        viewModel.clearError()

        // THEN
        assertNull(viewModel.uiState.value.error)
    }
}
