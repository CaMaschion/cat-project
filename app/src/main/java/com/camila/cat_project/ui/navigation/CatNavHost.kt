package com.camila.cat_project.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.camila.cat_project.ui.screen.CatBreedScreen
import com.camila.cat_project.ui.screen.detail.DetailScreen
import com.camila.cat_project.ui.screen.favorites.FavoritesScreen

@Composable
fun CatNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Breeds.route,
        modifier = modifier
    ) {
        composable(route = Screen.Breeds.route) {
            CatBreedScreen(
                onBreedClick = { breedId ->
                    navController.navigate(Screen.Detail.createRoute(breedId))
                }
            )
        }

        composable(route = Screen.Favorites.route) {
            FavoritesScreen(
                onBreedClick = { breedId ->
                    navController.navigate(Screen.Detail.createRoute(breedId))
                }
            )
        }

        composable(
            route = Screen.Detail.route,
            arguments = listOf(
                navArgument("breedId") { type = NavType.StringType }
            )
        ) {
            DetailScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
