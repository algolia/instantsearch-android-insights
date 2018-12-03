package com.algolia.instantsearch.insights.converter

import com.algolia.instantsearch.insights.event.Event
import com.algolia.instantsearch.insights.event.Event.Companion.EventTypeKey
import com.algolia.instantsearch.insights.event.EventType
import org.json.JSONObject


internal object ConverterEventToString : Converter<Event, String> {

    override fun convert(input: Event): String {
        val type = when (input) {
            is Event.Click -> EventType.Click
            is Event.View -> EventType.View
            is Event.Conversion -> EventType.Conversion
        }
        return JSONObject().also { json ->
            json.put(EventTypeKey, type.name.toLowerCase())
            input.params.entries.forEach { json.put(it.key, it.value) }
        }.toString()
    }
}
