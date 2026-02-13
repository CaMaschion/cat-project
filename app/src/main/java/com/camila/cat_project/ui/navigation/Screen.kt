package com.camila.cat_project.ui.navigation

sealed class Screen(val route: String) {
    data object Breeds : Screen("breeds")
    data object Favorites : Screen("favorites")
    data object Detail : Screen("detail/{breedId}") {
        fun createRoute(breedId: String) = "detail/$breedId"
    }
}
