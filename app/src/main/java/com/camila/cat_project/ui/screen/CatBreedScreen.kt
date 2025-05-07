package com.camila.cat_project.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun CatBreedScreen(
    modifier: Modifier = Modifier,
    viewModel: CatBreedViewModel = hiltViewModel()
) {
    val state by viewModel.catBreedState.collectAsState()
    Box(modifier = modifier.fillMaxSize()) {
        when (state) {
            is UiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize())
            }

            is UiState.Success -> {
                val breeds = (state as UiState.Success).catBreeds
                LazyColumn {
                    items(breeds) { breed ->
                        Text(text = breed.name, modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize())
                    }
                }
            }

            is UiState.Error -> {
                val errorMessage = (state as UiState.Error).message
                Text(
                    text = "Error loading breeds: $errorMessage",
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }

}

@Composable
@Preview
fun CatBreedScreenPreview() {
    CatBreedScreen()
}