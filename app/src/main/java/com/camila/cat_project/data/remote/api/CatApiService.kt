package com.camila.cat_project.data.remote.api

import com.camila.cat_project.data.remote.dto.ImageDto
import retrofit2.http.GET
import retrofit2.http.Query

interface CatApiService {

    @GET("v1/images/search")
    suspend fun getBreeds(
        @Query("limit") limit: Int = 100,
        @Query("has_breeds") hasBreeds: Int = 1,
        @Query("api_key") apiKey: String
    ): List<ImageDto>

    @GET("v1/images/{id}")
    suspend fun getImageById(
        @retrofit2.http.Path("id") id: String
    ): ImageDto

}