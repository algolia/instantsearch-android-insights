package com.algolia.instantsearch.insights.webservice

import com.algolia.instantsearch.insights.event.Event


internal interface WebService {

    data class Response(
        val errorMessage: String?,
        val code: Int
    )

    fun sendEvent(event: Event): Response
}
