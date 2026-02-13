package com.camila.cat_project.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.camila.cat_project.domain.model.CatBreedModel
import com.camila.cat_project.ui.components.CatBreedItem
import com.camila.cat_project.ui.components.EmptyStateView
import com.camila.cat_project.ui.components.ErrorView
import com.camila.cat_project.ui.components.LoadingView
import com.camila.cat_project.ui.theme.CatprojectTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatBreedScreen(
    modifier: Modifier = Modifier,
    viewModel: CatBreedViewModel = hiltViewModel(),
    onBreedClick: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Show snackbar for errors when we have cached data
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            if (uiState.breeds.isNotEmpty()) {
                snackbarHostState.showSnackbar(error)
                viewModel.clearError()
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Cat Breeds") }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search Bar
            SearchBar(
                query = uiState.searchQuery,
                onQueryChange = viewModel::onSearchQueryChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Content
            when {
                uiState.isLoading && uiState.breeds.isEmpty() -> {
                    LoadingView()
                }
                uiState.error != null && uiState.breeds.isEmpty() -> {
                    ErrorView(
                        message = uiState.error ?: "Unknown error",
                        onRetry = viewModel::onRefresh
                    )
                }
                uiState.isEmpty -> {
                    EmptyStateView(
                        title = "No cat breeds found",
                        subtitle = if (uiState.searchQuery.isNotBlank()) {
                            "Try a different search term"
                        } else {
                            "Tap refresh to load breeds"
                        }
                    )
                }
                else -> {
                    CatBreedList(
                        breeds = uiState.breeds,
                        isRefreshing = uiState.isRefreshing,
                        onBreedClick = onBreedClick,
                        onFavoriteClick = viewModel::onToggleFavorite
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text("Search cat breeds...") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Search"
            )
        },
        trailingIcon = {
            if (query.isNotBlank()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = "Clear search"
                    )
                }
            }
        },
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        shape = MaterialTheme.shapes.medium
    )
}

@Composable
private fun CatBreedList(
    breeds: List<CatBreedModel>,
    isRefreshing: Boolean,
    onBreedClick: (String) -> Unit,
    onFavoriteClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = breeds,
                key = { it.id }
            ) { breed ->
                CatBreedItem(
                    breed = breed,
                    onItemClick = onBreedClick,
                    onFavoriteClick = onFavoriteClick
                )
            }
        }

        // Show loading indicator overlay when refreshing
        if (isRefreshing) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                LoadingView()
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun CatBreedScreenPreview() {
    CatprojectTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            CatBreedList(
                breeds = listOf(
                    CatBreedModel(
                        id = "1",
                        name = "Abyssinian",
                        origin = "Egypt",
                        temperament = "Active, Energetic",
                        lifeSpan = "14 - 15",
                        description = "An ancient breed",
                        imageUrl = "",
                        isFavorite = true
                    ),
                    CatBreedModel(
                        id = "2",
                        name = "Bengal",
                        origin = "United States",
                        temperament = "Alert, Agile",
                        lifeSpan = "12 - 15",
                        description = "A hybrid breed",
                        imageUrl = "",
                        isFavorite = false
                    )
                ),
                isRefreshing = false,
                onBreedClick = {},
                onFavoriteClick = {}
            )
        }
    }
}