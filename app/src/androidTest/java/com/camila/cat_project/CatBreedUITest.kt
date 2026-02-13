package com.camila.cat_project

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.camila.cat_project.domain.model.CatBreedModel
import com.camila.cat_project.ui.components.CatBreedItem
import com.camila.cat_project.ui.components.EmptyStateView
import com.camila.cat_project.ui.components.ErrorView
import com.camila.cat_project.ui.theme.CatprojectTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CatBreedUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testBreed = CatBreedModel(
        id = "1",
        name = "Abyssinian",
        origin = "Egypt",
        temperament = "Active, Energetic, Independent",
        lifeSpan = "14 - 15",
        description = "The Abyssinian is easy to care for",
        imageUrl = "https://cdn2.thecatapi.com/images/test.jpg",
        isFavorite = false
    )

    @Test
    fun catBreedItem_displaysBreedInfo() {
        composeTestRule.setContent {
            CatprojectTheme {
                CatBreedItem(
                    breed = testBreed,
                    onItemClick = {},
                    onFavoriteClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Abyssinian").assertIsDisplayed()
        composeTestRule.onNodeWithText("Egypt").assertIsDisplayed()
        composeTestRule.onNodeWithText("Active, Energetic, Independent").assertIsDisplayed()
    }

    @Test
    fun catBreedItem_clickTriggersCallback() {
        var clickedId = ""

        composeTestRule.setContent {
            CatprojectTheme {
                CatBreedItem(
                    breed = testBreed,
                    onItemClick = { clickedId = it },
                    onFavoriteClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Abyssinian").performClick()
        assertEquals("1", clickedId)
    }

    @Test
    fun catBreedItem_favoriteClickTriggersCallback() {
        var favoriteClickedId = ""

        composeTestRule.setContent {
            CatprojectTheme {
                CatBreedItem(
                    breed = testBreed,
                    onItemClick = {},
                    onFavoriteClick = { favoriteClickedId = it }
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Add to favorites").performClick()
        assertEquals("1", favoriteClickedId)
    }

    @Test
    fun catBreedItem_showsFilledHeartWhenFavorite() {
        val favoriteBreed = testBreed.copy(isFavorite = true)

        composeTestRule.setContent {
            CatprojectTheme {
                CatBreedItem(
                    breed = favoriteBreed,
                    onItemClick = {},
                    onFavoriteClick = {}
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Remove from favorites").assertIsDisplayed()
    }

    @Test
    fun errorView_displaysErrorMessageAndRetryButton() {
        var retryClicked = false
        val errorMessage = "Network error occurred"

        composeTestRule.setContent {
            CatprojectTheme {
                ErrorView(
                    message = errorMessage,
                    onRetry = { retryClicked = true }
                )
            }
        }

        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").performClick()
        assertTrue(retryClicked)
    }

    @Test
    fun emptyStateView_displaysTitleAndSubtitle() {
        val title = "No cat breeds found"
        val subtitle = "Try a different search"

        composeTestRule.setContent {
            CatprojectTheme {
                EmptyStateView(
                    title = title,
                    subtitle = subtitle
                )
            }
        }

        composeTestRule.onNodeWithText(title).assertIsDisplayed()
        composeTestRule.onNodeWithText(subtitle).assertIsDisplayed()
    }
}