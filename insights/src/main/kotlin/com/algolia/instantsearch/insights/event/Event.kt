package com.algolia.instantsearch.insights.event


sealed class Event {

    data class Click constructor(
        val eventName: String,
        val indexName: String,
        val userToken: String,
        val timestamp: Long,
        val queryId: String? = null,
        val objectIDs: List<String>? = null,
        val positions: List<Int>? = null
    ) : Event()

    data class View constructor(
        val eventName: String,
        val indexName: String,
        val userToken: String,
        val timestamp: Long,
        val queryId: String? = null,
        val objectIDs: List<String>? = null
    ) : Event()

    data class Conversion constructor(
        val eventName: String,
        val indexName: String,
        val userToken: String,
        val timestamp: Long,
        val queryId: String? = null,
        val objectIDs: List<String>? = null
    ) : Event()
}
