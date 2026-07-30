package com.template.feature.home.data

import com.template.feature.home.api.ItemRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ItemRepositoryTest {
    private val testDispatcher = StandardTestDispatcher()
    private var shouldFail = false

    private val dao: ItemDao = FakeItemDao()
    private val repository: ItemRepository = ItemRepositoryImpl(
        ioDispatcher = testDispatcher,
        settings = FakeSettings(),
        dao = dao,
        httpClient = HttpClient(
            MockEngine { _ ->
                if (shouldFail) {
                    respond(
                        content = ByteReadChannel(""),
                        status = HttpStatusCode.InternalServerError
                    )
                } else {
                    respond(
                        content = ByteReadChannel(mockResponse.value),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }
            }
        ) {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                    }
                )
            }
        },
    )

    private val mockResponse = MutableStateFlow("[]")

    @Test
    fun `getItemsPagination should load more on start when empty`() = runTest(testDispatcher) {
        mockResponse.value = """
            [
                {
                    "userId": 1,
                    "id": 1,
                    "title": "T",
                    "body": "B"
                }
            ]
        """.trimIndent()

        val pagination = repository.getItemsPagination()
        val items = pagination.flow.first { it.isNotEmpty() }

        assertEquals(1, items.size)
        assertEquals(1L, items[0].id)
        assertEquals(1, dao.getItemCount())
    }

    @Test
    fun `getItemsPagination shouldn't load more on start when not empty`() =
        runTest(testDispatcher) {
            mockResponse.value = """
            [
                {
                    "userId": 2,
                    "id": 2,
                    "title": "T",
                    "body": "B"
                }
            ]
        """.trimIndent()

            dao.insertItems(
                listOf(
                    ItemData(
                        id = 1,
                        title = "T",
                        body = "B",
                        imageUrl = "I",
                    ),
                )
            )

            val pagination = repository.getItemsPagination()
            val items = pagination.flow.first()

            assertEquals(1, items.size)
            assertEquals(1, dao.getItemCount())
        }

    @Test
    fun `getItemsPagination loadMore should fetch and save items`() = runTest(testDispatcher) {
        mockResponse.value = """
            [
                {
                    "userId": 1,
                    "id": 1,
                    "title": "T",
                    "body": "B"
                }
            ]
        """.trimIndent()

        val pagination = repository.getItemsPagination()
        pagination.loadMore()

        assertEquals(1, dao.getItemCount())
    }

    @Test
    fun `getItemFlow should return item`() = runTest(testDispatcher) {
        val item = ItemData(
            id = 1,
            title = "T",
            body = "B",
            imageUrl = "I",
        )
        dao.insertItems(listOf(item))
        val result = repository.getItemFlow(1).first()
        assertEquals(item.id, result?.id)
        assertEquals(item.title, result?.title)
    }

    @Test
    fun `getItemFlow should return null when not found`() = runTest(testDispatcher) {
        val result = repository.getItemFlow(1).first()
        assertNull(result)
    }

    @Test
    fun `clearItems should reset settings and dao`() = runTest(testDispatcher) {
        dao.insertItems(
            listOf(
                ItemData(
                    id = 1,
                    title = "T",
                    body = "B",
                    imageUrl = "I",
                ),
            )
        )
        repository.clearItems()
        assertEquals(0, dao.getItemCount())
    }

    @Test
    fun `loadMore should handle HTTP errors gracefully`() = runTest(testDispatcher) {
        shouldFail = true
        val pagination = repository.getItemsPagination()
        pagination.loadMore()

        assertEquals(0, dao.getItemCount())
    }
}
