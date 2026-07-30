package com.template.feature.home.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Query("SELECT * FROM Item ORDER BY database_id")
    fun getItemsFlow(): Flow<List<ItemData>>

    @Query("SELECT COUNT(*) FROM Item")
    suspend fun getItemCount(): Int

    @Query("SELECT * FROM Item WHERE id = :id LIMIT 1")
    fun getItemFlow(id: Long): Flow<ItemData?>

    @Upsert
    suspend fun insertItems(items: List<ItemData>)

    @Query("DELETE FROM Item WHERE id = :id")
    suspend fun deleteItemById(id: Long)

    @Query("DELETE FROM Item")
    suspend fun clearItems()

    @Transaction
    suspend fun insertItemsWithoutDuplicates(items: List<ItemData>) {
        items.forEach { deleteItemById(it.id) }
        insertItems(items)
    }
}
