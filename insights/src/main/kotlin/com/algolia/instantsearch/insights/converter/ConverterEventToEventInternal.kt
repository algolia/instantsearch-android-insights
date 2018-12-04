package com.algolia.instantsearch.insights.converter

import com.algolia.instantsearch.insights.event.Event
import com.algolia.instantsearch.insights.event.EventInternal
import com.algolia.instantsearch.insights.event.EventKey
import com.algolia.instantsearch.insights.event.EventType


internal object ConverterEventToEventInternal : Converter<Event, EventInternal> {

    override fun convert(input: Event): EventInternal {
        return when (input) {
            is Event.View -> input.toEventInternal()
            is Event.Conversion -> input.toEventInternal()
            is Event.Click -> input.toEventInternal()
        }
    }

    private fun Event.Click.toEventInternal(): EventInternal {
        return mapOf(
            EventKey.EventType.key to EventType.Click.key,
            EventKey.EventName.key to eventName,
            EventKey.IndexName.key to indexName,
            EventKey.Timestamp.key to timestamp,
            EventKey.QueryId.key to queryId,
            EventKey.UserToken.key to userToken,
            EventKey.ObjectIds.key to objectIDs,
            EventKey.Positions.key to positions
        )
    }

    private fun Event.Conversion.toEventInternal(): EventInternal {
        return mapOf(
            EventKey.EventType.key to EventType.Conversion.key,
            EventKey.EventName.key to eventName,
            EventKey.IndexName.key to indexName,
            EventKey.Timestamp.key to timestamp,
            EventKey.QueryId.key to queryId,
            EventKey.UserToken.key to userToken,
            EventKey.ObjectIds.key to objectIDs
        )
    }

    private fun Event.View.toEventInternal(): EventInternal {
        return mapOf(
            EventKey.EventType.key to EventType.View.key,
            EventKey.EventName.key to eventName,
            EventKey.IndexName.key to indexName,
            EventKey.Timestamp.key to timestamp,
            EventKey.QueryId.key to queryId,
            EventKey.UserToken.key to userToken,
            EventKey.ObjectIds.key to objectIDs
        )
    }
}