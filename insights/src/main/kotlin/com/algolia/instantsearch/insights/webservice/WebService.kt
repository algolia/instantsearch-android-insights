package com.algolia.instantsearch.insights.webservice

import com.algolia.instantsearch.insights.event.Event


internal interface WebService {

    data class Response(
        val errorMessage: String?,
        val code: Int
    )

    fun send(events: List<Event>): Response
    fun send(event: Event) = send(listOf(event))
}
