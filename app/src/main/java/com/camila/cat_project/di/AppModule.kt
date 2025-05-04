package com.camila.cat_project.di

import com.camila.cat_project.data.remote.api.CatApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    private const val BASE_URL = "https://api.thecatapi.com/"

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideCatApi(retrofit: Retrofit): CatApiService {
        return retrofit.create(CatApiService::class.java)
    }

    // okhttp client is for adding headers
    @Provides
    fun providesOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader(
                        "x-api-key",
                        "live_dLLr4LPMOFwVwl31R3rjNrYg5ixhIZG5kq5iE8HRZrKIiasrZbORw5HjqT8HsJhB"
                    )
                    .build()
                chain.proceed(request)
            }
            .build()
    }
}
