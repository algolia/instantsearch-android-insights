package com.algolia.instantsearch.insights

import android.content.SharedPreferences


internal fun SharedPreferences.consumeEvents(
    eventConsumer: (List<String>) -> List<NetworkResponse>
): List<NetworkResponse> {
    val networkResponses = eventConsumer(events.toList())
    val failedEvents = networkResponses.filterNot { it.code.isValidHttpCode() }

    this.events = failedEvents.map { it.serializedEvent }.toSet()
    return failedEvents
}

internal fun Int.isValidHttpCode() = this == 200 || this == 201

internal fun NetworkManager.eventConsumer(indexName: String): (List<String>) -> List<NetworkResponse> {
    return { serializedEvents ->
        serializedEvents.map {
            val event = ConverterStringToEvent.convert(it)
            val (errorMessage, code) = try {
                sendEvent(event)
            } catch (exception: Exception) {
                NetworkManager.Response(exception.localizedMessage, -1)
            }
            val message = if (code.isValidHttpCode()) {
                "Sync succeeded for $event."
            } else {
                "$errorMessage (Code: $code)"
            }
            Logger.log(indexName, message)
            NetworkResponse(
                code = code,
                serializedEvent = it
            )
        }
    }
}
