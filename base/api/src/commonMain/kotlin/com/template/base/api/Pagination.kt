package com.template.base.api

import kotlinx.coroutines.flow.Flow

class Pagination<T>(
    val flow: Flow<List<T>>,
    val loadMore: suspend () -> Unit,
)
