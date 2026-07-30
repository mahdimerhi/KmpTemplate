package com.template.feature.home.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeItemDao : ItemDao {
    private val items =
        MutableStateFlow<List<ItemData>>(emptyList())

    override fun getItemsFlow(): Flow<List<ItemData>> = items

    override suspend fun getItemCount(): Int = items.value.size

    override fun getItemFlow(id: Long): Flow<ItemData?> {
        return items.map { list ->
            list.find { it.id == id }
        }
    }

    override suspend fun insertItems(items: List<ItemData>) {
        this.items.update { items }
    }

    override suspend fun deleteItemById(id: Long) {
        items.update { items -> items.filter { it.id != id } }
    }

    override suspend fun clearItems() {
        items.update { emptyList() }
    }
}
