package com.algolia.instantsearch.insights.webservice

import com.algolia.instantsearch.insights.event.EventInternal


internal interface WebService {

    data class Response(
        val errorMessage: String?,
        val code: Int
    )

    fun send(vararg event: EventInternal): Response
}
