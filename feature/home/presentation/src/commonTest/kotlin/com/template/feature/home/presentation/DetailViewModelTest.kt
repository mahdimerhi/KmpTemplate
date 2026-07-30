package com.template.feature.home.presentation

import com.template.base.api.Pagination
import com.template.feature.home.api.ItemEntity
import com.template.feature.home.api.ItemRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeItemRepository
    private lateinit var viewModel: DetailViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeItemRepository()
        viewModel = DetailViewModel(repository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `load should update screen with item`() = runTest {
        val item = ItemEntity(1L, "Title", "Body", "Image")
        repository.itemFlow.value = item

        viewModel.load(1L)
        advanceUntilIdle()

        assertEquals(item, viewModel.screen.value.item)
    }

    @Test
    fun `load with different id should update screen`() = runTest {
        val item1 = ItemEntity(1L, "Title 1", "Body 1", "Image 1")
        val item2 = ItemEntity(2L, "Title 2", "Body 2", "Image 2")

        viewModel.load(1L)
        repository.itemFlow.value = item1
        advanceUntilIdle()
        assertEquals(item1, viewModel.screen.value.item)

        viewModel.load(2L)
        repository.itemFlow.value = item2
        advanceUntilIdle()
        assertEquals(item2, viewModel.screen.value.item)
    }

    @Test
    fun `load should handle null items`() = runTest {
        viewModel.load(1L)
        repository.itemFlow.value = null
        advanceUntilIdle()

        assertNull(viewModel.screen.value.item)
    }

    private class FakeItemRepository : ItemRepository {
        val itemFlow = MutableStateFlow<ItemEntity?>(null)

        override fun getItemFlow(id: Long): Flow<ItemEntity?> = itemFlow

        override fun getItemsPagination(): Pagination<ItemEntity> {
            error("Not used")
        }

        override suspend fun clearItems() {
            error("Not used")
        }
    }
}
