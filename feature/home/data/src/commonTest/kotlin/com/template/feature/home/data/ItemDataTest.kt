package com.template.feature.home.data

import kotlin.test.Test
import kotlin.test.assertEquals

class ItemDataTest {
    @Test
    fun `toEntity should map correct fields`() {
        val data = ItemData(
            id = 123L,
            title = "Test Title",
            body = "Test Body",
            imageUrl = "https://example.com/image.png",
        )
        val entity = data.toEntity()
        assertEquals(data.id, entity.id)
        assertEquals(data.title, entity.title)
        assertEquals(data.body, entity.body)
        assertEquals(data.imageUrl, entity.imageUrl)
    }

    @Test
    fun `toEntity should handle body correctly`() {
        val data = ItemData(
            id = 1L,
            title = "T",
            body = "B",
            imageUrl = "",
        )

        val entity = data.toEntity()
        assertEquals("B", entity.body)
        assertEquals("", entity.imageUrl)
    }
}
