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
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeItemRepository
    private lateinit var viewModel: HomeViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeItemRepository()
        viewModel = HomeViewModel(repository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `load should clear items and fetch pagination`() = runTest {
        viewModel.load()
        advanceUntilIdle()

        assertTrue(repository.clearItemsCalled)
        assertEquals(emptyList(), viewModel.screen.value.items)

        val items = listOf(
            ItemEntity(1L, "Title 1", "Body 1", "Image 1"),
            ItemEntity(2L, "Title 2", "Body 2", "Image 2")
        )
        repository.itemsFlow.value = items
        advanceUntilIdle()

        assertEquals(items, viewModel.screen.value.items)
    }

    @Test
    fun `load should only run once`() = runTest {
        viewModel.load()
        viewModel.load()
        advanceUntilIdle()

        assertEquals(1, repository.clearItemsCallCount)
    }

    @Test
    fun `onAction LoadMoreItems should call loadMore on pagination`() = runTest {
        viewModel.load()
        advanceUntilIdle()

        viewModel.onAction(HomeViewModel.Action.LoadMoreItems)
        advanceUntilIdle()

        assertTrue(repository.loadMoreCalled)
    }

    private class FakeItemRepository : ItemRepository {
        val itemsFlow =
            MutableStateFlow<List<ItemEntity>>(emptyList())
        var clearItemsCalled = false
        var clearItemsCallCount = 0
        var loadMoreCalled = false

        override fun getItemsPagination(): Pagination<ItemEntity> {
            return Pagination(
                flow = itemsFlow,
                loadMore = { loadMoreCalled = true }
            )
        }

        override fun getItemFlow(id: Long): Flow<ItemEntity?> {
            error("Not used")
        }

        override suspend fun clearItems() {
            clearItemsCalled = true
            clearItemsCallCount++
        }
    }

    @Test
    fun `onAction(loadMoreItems) should do nothing if load() was never called`() {


    }
}
