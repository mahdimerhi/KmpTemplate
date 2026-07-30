package com.template.feature.home.data

import com.template.base.api.Constants
import com.template.base.api.Pagination
import com.template.base.api.Qualifiers
import com.template.base.api.Settings
import com.template.feature.home.api.ItemEntity
import com.template.feature.home.api.ItemRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

private const val PAGE_SIZE = 10

@ContributesBinding(AppScope::class)
@Inject
class ItemRepositoryImpl(
    @param:Qualifiers.Dispatchers.Io private val ioDispatcher: CoroutineDispatcher,
    private val settings: Settings,
    private val dao: ItemDao,
    private val httpClient: HttpClient,
) : ItemRepository {
    private val itemsMutex = Mutex()

    override fun getItemsPagination(): Pagination<ItemEntity> {
        return Pagination(
            flow = dao.getItemsFlow()
                .onStart {
                    if (settings.getBoolean(KEY_HAS_MORE_ITEMS) != false &&
                        dao.getItemCount() == 0
                    ) {
                        loadMore()
                    }
                }
                .map { it.map(ItemData::toEntity) }
                .flowOn(ioDispatcher),
            loadMore = ::loadMore,
        )
    }

    private suspend fun loadMore(): Unit = withContext(ioDispatcher) {
        if (itemsMutex.isLocked) return@withContext
        if (settings.getBoolean(KEY_HAS_MORE_ITEMS) == false) return@withContext
        itemsMutex.withLock {
            if (settings.getBoolean(KEY_HAS_MORE_ITEMS) == false) return@withLock
            try {
                val page = settings.getString(KEY_ITEMS_CURRENT_PAGE)?.toIntOrNull() ?: 1
                val data: List<PostResponse> =
                    httpClient.get("${Constants.API}posts?_page=$page&_limit=$PAGE_SIZE").body()
                val nextPage = page + 1
                val items = data.map(PostResponse::toData)
                val hasMore = data.size >= PAGE_SIZE
                settings.setString(KEY_ITEMS_CURRENT_PAGE, nextPage.toString())
                dao.insertItemsWithoutDuplicates(items)
                settings.setBoolean(KEY_HAS_MORE_ITEMS, hasMore)
            } catch (_: Exception) {
                ensureActive()
            }
        }
    }

    override fun getItemFlow(id: Long): Flow<ItemEntity?> {
        return dao.getItemFlow(id).map { it?.toEntity() }
    }

    override suspend fun clearItems(): Unit = withContext(ioDispatcher) {
        dao.clearItems()
        settings.setBoolean(KEY_HAS_MORE_ITEMS, true)
        settings.setString(KEY_ITEMS_CURRENT_PAGE, null)
    }
}

private const val KEY_HAS_MORE_ITEMS = "item_has_more_items"
private const val KEY_ITEMS_CURRENT_PAGE = "item_current_page"
