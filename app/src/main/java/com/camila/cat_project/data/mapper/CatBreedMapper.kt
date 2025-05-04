package com.camila.cat_project.data.mapper

import com.camila.cat_project.data.local.entity.CatBreedEntity
import com.camila.cat_project.data.remote.dto.ImageDto

object CatBreedMapper {

    fun mapImageDto(dto: ImageDto): CatBreedEntity {
        val breed = dto.breeds.firstOrNull()
        return CatBreedEntity(
            id = breed?.id ?: dto.id,
            name = breed?.name ?: "Unknown",
            origin = breed?.origin ?: "Unknown",
            temperament = breed?.temperament ?: "Unknown",
            description = breed?.description ?: "No description",
            lifeSpan = breed?.lifeSpan ?: "Unknown",
            imageUrl = dto.url,
            isFavorite = false
        )
    }

    fun mapImageDtoList(dtoList: List<ImageDto>): List<CatBreedEntity> {
        return dtoList.map { mapImageDto(it) }
    }
}