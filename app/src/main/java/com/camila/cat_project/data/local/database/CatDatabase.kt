package com.camila.cat_project.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.camila.cat_project.data.local.dao.CatBreedDao
import com.camila.cat_project.data.local.entity.CatBreedEntity

@Database(entities = [CatBreedEntity::class], version = 1)
abstract class CatDatabase : RoomDatabase() {
    abstract fun catBreedDao(): CatBreedDao
}