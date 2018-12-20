package com.algolia.instantsearch.insights.database

import android.content.Context
import com.algolia.instantsearch.insights.converter.ConverterEventInternalToString
import com.algolia.instantsearch.insights.converter.ConverterStringToEventInternal
import com.algolia.instantsearch.insights.event.EventInternal
import com.algolia.instantsearch.insights.prefixAlgolia

internal class DatabaseSharedPreferences(
    context: Context,
    override val indexName: String
) : Database {

    private val preferences = context.sharedPreferences(prefixAlgolia(indexName))


    override fun append(event: EventInternal) {
        val events = preferences.events
            .toMutableSet()
            .also { it.add(ConverterEventInternalToString.convert(event)) }

        preferences.events = events
    }

    override fun overwrite(events: List<EventInternal>) {
        preferences.events = ConverterEventInternalToString.convert(events).toSet()
    }

    override fun read(): List<EventInternal> {
        return ConverterStringToEventInternal.convert(preferences.events.toList())
    }

    override fun count(): Int {
        return preferences.events.size
    }

    override fun clear() {
        preferences.events = setOf()
    }
}
