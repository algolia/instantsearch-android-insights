package com.algolia.instantsearch.insights.database

import android.content.Context
import com.algolia.instantsearch.insights.converter.ConverterEventToString
import com.algolia.instantsearch.insights.converter.ConverterStringToEvent
import com.algolia.instantsearch.insights.event.Event
import com.algolia.instantsearch.insights.prefixAlgolia

internal class DatabaseSharedPreferences(
    context: Context,
    override val indexName: String
) : Database {

    private val preferences = context.sharedPreferences(prefixAlgolia(indexName))


    override fun append(event: Event) {
        val events = preferences.serializedEvents
            .map(ConverterStringToEvent::convert)
            .toMutableList()
            .also { it.add(event) }

        preferences.serializedEvents = ConverterEventToString.convert(events).toSet()
    }

    override fun overwrite(events: List<Event>) {
        preferences.serializedEvents = ConverterEventToString.convert(events).toSet()
    }

    override fun read(): List<Event> {
        return ConverterStringToEvent.convert(preferences.serializedEvents.toList())
    }

    override fun clear() {
        preferences.serializedEvents = setOf()
    }
}
