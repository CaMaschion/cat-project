package com.camila.cat_project.di

import android.content.Context
import androidx.room.Room
import com.camila.cat_project.BuildConfig
import com.camila.cat_project.data.local.dao.CatBreedDao
import com.camila.cat_project.data.local.database.CatDatabase
import com.camila.cat_project.data.mapper.CatBreedMapper
import com.camila.cat_project.data.remote.api.CatApiService
import com.camila.cat_project.data.repository.CatRepositoryImpl
import com.camila.cat_project.domain.repository.CatBreedRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    private const val BASE_URL = "https://api.thecatapi.com/"

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("x-api-key", BuildConfig.CAT_API_KEY)
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideCatApi(retrofit: Retrofit): CatApiService {
        return retrofit.create(CatApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideCatBreedDao(database: CatDatabase): CatBreedDao {
        return database.catBreedDao()
    }

    @Provides
    @Singleton
    fun provideCatDatabase(@ApplicationContext appContext: Context): CatDatabase {
        return Room.databaseBuilder(
            appContext,
            CatDatabase::class.java,
            "cat_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideCatBreedMapper(): CatBreedMapper {
        return CatBreedMapper
    }

    @Provides
    @Singleton
    fun provideCatBreedRepository(
        catApiService: CatApiService,
        catBreedDao: CatBreedDao,
        catBreedMapper: CatBreedMapper
    ): CatBreedRepository {
        return CatRepositoryImpl(catApiService, catBreedMapper, catBreedDao)
    }

}
