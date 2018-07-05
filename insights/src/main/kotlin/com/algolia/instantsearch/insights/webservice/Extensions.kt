package com.algolia.instantsearch.insights.webservice

import com.algolia.instantsearch.insights.InsightsLogger
import com.algolia.instantsearch.insights.database.Database
import com.algolia.instantsearch.insights.event.Event
import com.algolia.instantsearch.insights.event.EventResponse


internal fun List<EventResponse>.filterEventsNetworkFailure(): List<EventResponse> {
    return this.filter { it.code == -1 }
}

internal fun Int.isValidHttpCode() = this == 200 || this == 201

internal fun WebService.sendEvent(indexName: String, event: Event): EventResponse {
    val (errorMessage, code) = try {
        sendEvent(event)
    } catch (exception: Exception) {
        WebService.Response(exception.localizedMessage, -1)
    }
    val message = if (code.isValidHttpCode()) {
        "Sync succeeded for $event."
    } else {
        "$errorMessage (Code: $code)"
    }
    InsightsLogger.log(indexName, message)
    return EventResponse(
        code = code,
        event = event
    )
}

internal fun WebService.sendEvents(indexName: String, events: List<Event>): List<EventResponse> {
    return events.map { event -> sendEvent(indexName, event) }
}


internal fun WebService.uploadEvents(database: Database, indexName: String): List<EventResponse> {
    val events = database.read()

    InsightsLogger.log(indexName, "Flushing remaining ${events.size} events.")

    val failedEvents = sendEvents(indexName, events).filterEventsNetworkFailure()

    database.overwrite(failedEvents.map { it.event })
    return failedEvents
}
