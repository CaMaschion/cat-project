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
    @Query("SELECT * FROM cat_breeds ORDER BY name ASC")
    fun getAllBreeds(): Flow<List<CatBreedEntity>>

    @Query("SELECT * FROM cat_breeds WHERE isFavorite = 1 ORDER BY name ASC")
    fun getFavoriteBreeds(): Flow<List<CatBreedEntity>>

    @Query("SELECT * FROM cat_breeds WHERE id = :id")
    suspend fun getBreedById(id: String): CatBreedEntity?

    @Query("SELECT * FROM cat_breeds WHERE id = :id")
    fun getBreedByIdFlow(id: String): Flow<CatBreedEntity?>

    @Query("SELECT * FROM cat_breeds WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchBreeds(query: String): Flow<List<CatBreedEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBreeds(breeds: List<CatBreedEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBreed(breed: CatBreedEntity)

    @Update
    suspend fun updateBreed(breed: CatBreedEntity)

    @Query("UPDATE cat_breeds SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: String, isFavorite: Boolean)

    @Query("SELECT COUNT(*) FROM cat_breeds")
    suspend fun getBreedsCount(): Int

    @Query("DELETE FROM cat_breeds")
    suspend fun deleteAllBreeds()
}