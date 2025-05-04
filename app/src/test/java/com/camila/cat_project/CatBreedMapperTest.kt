package com.camila.cat_project

import com.camila.cat_project.data.local.entity.CatBreedEntity
import com.camila.cat_project.data.mapper.CatBreedMapper
import com.camila.cat_project.data.remote.dto.CatBreedDto
import com.camila.cat_project.data.remote.dto.ImageDto
import org.junit.Test
import org.junit.Assert.*

class CatBreedMapperTest {

    private val mapper = CatBreedMapper

    @Test
    fun `mapImageDto should correctly map ImageDto to CatBreedEntity`() {
        val imageDto = ImageDto(
            id = "test_id",
            url = "https://example.com/cat.jpg",
            width = 300,
            height = 300,
            breeds = listOf(
                CatBreedDto(
                    id = "abys",
                    name = "Abyssinian",
                    origin = "Egypt",
                    temperament = "Active, Energetic",
                    description = "An ancient breed",
                    lifeSpan = "14 - 15"
                )
            )
        )

        val result = mapper.mapImageDto(imageDto)

        val expected = CatBreedEntity(
            id = "abys",
            name = "Abyssinian",
            origin = "Egypt",
            temperament = "Active, Energetic",
            description = "An ancient breed",
            lifeSpan = "14 - 15",
            imageUrl = "https://example.com/cat.jpg",
            isFavorite = false
        )
        assertEquals(expected, result)
    }

    @Test
    fun `mapImageDtoList should correctly map ImageDto and return a list of CatBreedEntity`() {

        val dtoList = listOf(
            // Sem raça
            ImageDto(
                id = "no_breed",
                url = "url0",
                width = 100,
                height = 100,
                breeds = listOf()
            ),
            // Com raça: Bengal
            ImageDto(
                id = "1",
                url = "url1",
                width = 200,
                height = 200,
                breeds = listOf(
                    CatBreedDto(
                        id = "beng",
                        name = "Bengal",
                        origin = "USA",
                        description = "A hybrid breed",
                        temperament = "Energetic",
                        lifeSpan = "12 - 15"
                    )
                )
            ),
            // Com raça: Abyssinian
            ImageDto(
                id = "2",
                url = "url2",
                width = 300,
                height = 300,
                breeds = listOf(
                    CatBreedDto(
                        id = "abys",
                        name = "Abyssinian",
                        origin = "Egypt",
                        description = "An ancient breed",
                        temperament = "Active",
                        lifeSpan = "14 - 15"
                    )
                )
            )
        )

        val result = mapper.mapImageDtoList(dtoList)

        val first = result[0]
        assertEquals("no_breed", first.id)
        assertEquals("Unknown", first.name)
        assertEquals("Unknown", first.origin)
        assertEquals("No description", first.description)
        assertEquals("Unknown", first.temperament)
        assertEquals("Unknown", first.lifeSpan)
        assertEquals("url0", first.imageUrl)

        val second = result[1]
        assertEquals("beng", second.id)
        assertEquals("Bengal", second.name)
        assertEquals("USA", second.origin)
        assertEquals("A hybrid breed", second.description)
        assertEquals("Energetic", second.temperament)
        assertEquals("12 - 15", second.lifeSpan)
        assertEquals("url1", second.imageUrl)

        val third = result[2]
        assertEquals("abys", third.id)
        assertEquals("Abyssinian", third.name)
        assertEquals("Egypt", third.origin)
        assertEquals("An ancient breed", third.description)
        assertEquals("Active", third.temperament)
        assertEquals("14 - 15", third.lifeSpan)
        assertEquals("url2", third.imageUrl)

    }
}
