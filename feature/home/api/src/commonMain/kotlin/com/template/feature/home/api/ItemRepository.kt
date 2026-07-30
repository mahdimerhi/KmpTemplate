package com.template.feature.home.api

import com.template.base.api.Pagination
import kotlinx.coroutines.flow.Flow

interface ItemRepository {
    fun getItemsPagination(): Pagination<ItemEntity>
    fun getItemFlow(id: Long): Flow<ItemEntity?>
    suspend fun clearItems()
}
