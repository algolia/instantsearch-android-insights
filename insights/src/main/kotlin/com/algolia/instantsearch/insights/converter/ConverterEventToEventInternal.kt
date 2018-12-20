package com.algolia.instantsearch.insights.converter

import com.algolia.instantsearch.insights.event.*


internal object ConverterEventToEventInternal : Converter<Pair<Event, String>, EventInternal> {

    override fun convert(input: Pair<Event, String>): EventInternal {
        val (event, indexName) = input

        return when (event) {
            is Event.View -> event.toEventInternal(indexName)
            is Event.Conversion -> event.toEventInternal(indexName)
            is Event.Click -> event.toEventInternal(indexName)
        }
    }

    private fun Event.Click.toEventInternal(indexName: String): EventInternal {
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

    private fun Event.Conversion.toEventInternal(indexName: String): EventInternal {
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

    private fun Event.View.toEventInternal(indexName: String): EventInternal {
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
        if (eventObjects is EventObjects.IDs) EventKey.ObjectIds.key to eventObjects.values else null

    private fun filtersOrNull(eventObjects: EventObjects) =
        if (eventObjects is EventObjects.Filters) EventKey.Filters.key to eventObjects.values else null
}