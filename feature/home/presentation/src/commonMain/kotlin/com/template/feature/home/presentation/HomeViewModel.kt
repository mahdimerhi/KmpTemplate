package com.template.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.template.base.api.Pagination
import com.template.feature.home.api.ItemEntity
import com.template.feature.home.api.ItemRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Inject
@ViewModelKey
@ContributesIntoMap(AppScope::class)
class HomeViewModel(private val repository: ItemRepository) : ViewModel() {
    data class Screen(
        val items: List<ItemEntity>? = null,
    )

    sealed interface Action {
        data object LoadMoreItems : Action
    }

    private val _screen: MutableStateFlow<Screen> = MutableStateFlow(Screen())
    val screen: StateFlow<Screen> = _screen.asStateFlow()
    private var loadJob: Job? = null
    private var itemsPagination: Pagination<ItemEntity>? = null

    fun load() {
        if (loadJob != null) return
        loadJob = viewModelScope.launch {
            repository.clearItems()

            val pagination = repository.getItemsPagination()
            itemsPagination = pagination
            pagination.flow.collectLatest { items ->
                _screen.update {
                    it.copy(items = items)
                }
            }
        }
    }

    fun onAction(action: Action) {
        when (action) {
            Action.LoadMoreItems -> {
                viewModelScope.launch {
                    itemsPagination?.loadMore?.invoke()
                }
            }
        }
    }
}
