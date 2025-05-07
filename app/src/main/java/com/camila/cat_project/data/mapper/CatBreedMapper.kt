package com.camila.cat_project.data.mapper

import com.camila.cat_project.data.local.entity.CatBreedEntity
import com.camila.cat_project.data.remote.dto.ImageDto
import com.camila.cat_project.domain.model.CatBreedModel

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

    fun mapCatBreedEntity(entity: CatBreedEntity): CatBreedModel {
        return CatBreedModel(
            id = entity.id,
            name = entity.name,
            origin = entity.origin,
            temperament = entity.temperament,
            lifeSpan = entity.lifeSpan,
            description = entity.description,
            imageUrl = entity.imageUrl,
            isFavorite = entity.isFavorite
        )
    }

    fun mapCatBreedEntityList(entityList: List<CatBreedEntity>): List<CatBreedModel> {
        return entityList.map { mapCatBreedEntity(it) }
    }
}