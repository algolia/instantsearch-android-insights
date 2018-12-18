package com.algolia.instantsearch.insights.converter

import com.algolia.instantsearch.insights.event.*


internal object ConverterEventToEventInternal : Converter<Event, EventInternal> {

    override fun convert(input: Event): EventInternal {
        return when (input) {
            is Event.View -> input.toEventInternal()
            is Event.Conversion -> input.toEventInternal()
            is Event.Click -> input.toEventInternal()
        }
    }

    private fun Event.Click.toEventInternal(): EventInternal {
        return listOfNotNull(
            EventKey.EventType.key to EventType.Click.key,
            EventKey.EventName.key to eventName,
            EventKey.IndexName.key to indexName,
            EventKey.Timestamp.key to timestamp,
            EventKey.QueryId.key to queryId,
            EventKey.UserToken.key to userToken,
            EventKey.Positions.key to positions,
            objectIDsOrNull(eventObjects),
            filtersOrNull(eventObjects)
        ).toMap()
    }

    private fun Event.Conversion.toEventInternal(): EventInternal {
        return listOfNotNull(
            EventKey.EventType.key to EventType.Conversion.key,
            EventKey.EventName.key to eventName,
            EventKey.IndexName.key to indexName,
            EventKey.Timestamp.key to timestamp,
            EventKey.QueryId.key to queryId,
            EventKey.UserToken.key to userToken,
            objectIDsOrNull(eventObjects),
            filtersOrNull(eventObjects)
        ).toMap()
    }

    private fun Event.View.toEventInternal(): EventInternal {
        return listOfNotNull(
            EventKey.EventType.key to EventType.View.key,
            EventKey.EventName.key to eventName,
            EventKey.IndexName.key to indexName,
            EventKey.Timestamp.key to timestamp,
            EventKey.QueryId.key to queryId,
            EventKey.UserToken.key to userToken,
            objectIDsOrNull(eventObjects),
            filtersOrNull(eventObjects)
        ).toMap()
    }

    private fun objectIDsOrNull(eventObjects: EventObjects) =
        if (eventObjects is EventObjects.IDs) EventKey.ObjectIds.key to eventObjects.list else null

    private fun filtersOrNull(eventObjects: EventObjects) =
        if (eventObjects is EventObjects.Filters) EventKey.Filters.key to eventObjects.list else null
}