package com.camila.cat_project.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.camila.cat_project.data.local.entity.CatBreedEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CatBreedDao {
    @Query("SELECT * FROM cat_breeds")
    fun getAllBreeds(): Flow<List<CatBreedEntity>>

    @Query("SELECT * FROM cat_breeds WHERE isFavorite = 1")
    fun getFavoriteBreeds(): Flow<List<CatBreedEntity>>

    @Query("SELECT * FROM cat_breeds WHERE id = :id")
    suspend fun getBreedById(id: String): CatBreedEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBreed(breed: List<CatBreedEntity>)

    @Update
    suspend fun updateBreed(breed: CatBreedEntity)

}