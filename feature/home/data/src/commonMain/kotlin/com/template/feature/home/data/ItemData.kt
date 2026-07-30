package com.template.feature.home.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.template.feature.home.api.ItemEntity
import kotlinx.serialization.Serializable

@Entity(tableName = "Item")
data class ItemData(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "database_id")
    val databaseId: Long = 0L,
    @ColumnInfo(name = "id")
    val id: Long,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "body")
    val body: String,
    @ColumnInfo(name = "image_url")
    val imageUrl: String,
)

internal fun ItemData.toEntity(): ItemEntity {
    return ItemEntity(
        id = id,
        title = title,
        body = body,
        imageUrl = imageUrl,
    )
}

@Serializable
internal data class PostResponse(
    val userId: Long,
    val id: Long,
    val title: String,
    val body: String,
) {
    fun toData(): ItemData {
        return ItemData(
            id = id,
            title = title,
            body = body,
            imageUrl = "https://picsum.photos/seed/$id/400/300",
        )
    }
}
