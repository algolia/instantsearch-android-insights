package com.algolia.instantsearch.insights.event

import com.algolia.instantsearch.insights.converter.IndexNameKey


internal sealed class Event constructor(params: Map<String, Any>, open val indexName: String) {
    open val params = mutableMapOf<String, Any>().also {
        it.putAll(params)
        it[IndexNameKey] = indexName
    }.toMap()

    data class View(override val params: Map<String, Any>, override val indexName: String) : Event(params, indexName)

    data class Click(override val params: Map<String, Any>, override val indexName: String) : Event(params, indexName)

    data class Conversion(override val params: Map<String, Any>, override val indexName: String) : Event(params, indexName)
}
